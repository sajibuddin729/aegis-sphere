import uuid

from aegis_api.domain.curriculum import (
    CurriculumRunResult,
    RegressionEventResult,
)
from aegis_api.services.crypto import (
    canonical_hash,
)
from aegis_api.services.curriculum_control import (
    neutral_control_attack,
)


class CurriculumEngine:

    def __init__(
        self,
        *,
        seeds,
        scheduler,
        mutator,
        repository,
        arena,
        regression,
        memory_maintenance,
    ):
        self.seeds = seeds
        self.scheduler = scheduler
        self.mutator = mutator
        self.repository = repository
        self.arena = arena
        self.regression = regression
        self.memory_maintenance = memory_maintenance

    async def run(
        self,
        *,
        steps: int,
    ) -> CurriculumRunResult:

        stats_before = await self.repository.capability_stats()

        if not stats_before:
            raise RuntimeError(
                "NO_CAPABILITY_DATA"
            )

        ranked_before = self.scheduler.rank(
            stats_before
        )

        # Freeze missing baselines only.
        for item in ranked_before:
            baseline_payload = {
                "capability": item.capability,
                "rating": item.rating,
                "score": item.recent_average_score,
                "critical_rate": item.critical_rate,
            }

            await self.repository.ensure_baseline(
                capability=item.capability,
                rating=item.rating,
                score=item.recent_average_score,
                critical_rate=item.critical_rate,
                baseline_hash=canonical_hash(baseline_payload),
            )

        run_id = "curriculum_" + uuid.uuid4().hex

        await self.repository.create_run(
            run_id=run_id,
            steps=steps,
        )

        results = []
        regressions = []
        control_count = 0
        adversarial_count = 0
        quarantined_memories = 0

        for index in range(steps):
            current_stats = await self.repository.capability_stats()
            ranked = self.scheduler.rank(current_stats)
            selected = ranked[0]

            # Alternate control/adversarial so "always BLOCK" cannot win.
            mode = "CONTROL" if index % 2 == 0 else "ADVERSARIAL"

            seeds = self.seeds.for_capability(
                selected.capability,
                mode=mode,
            )

            if not seeds:
                # Fallback only if a mode is missing for that capability.
                fallback = "ADVERSARIAL" if mode == "CONTROL" else "CONTROL"
                seeds = self.seeds.for_capability(
                    selected.capability,
                    mode=fallback,
                )
                mode = fallback

            if not seeds:
                raise RuntimeError(
                    "NO_CURRICULUM_SEED_" + selected.capability
                )

            seed = seeds[index % len(seeds)]

            difficulty = max(
                int(seed["difficulty"]),
                selected.recommended_difficulty,
            )
            difficulty = min(5, difficulty)

            variant = self.mutator.mutate(
                seed=seed,
                mutation_index=index + 1,
                difficulty=difficulty,
            )

            variant_hash = canonical_hash(variant)
            variant_id = variant["scenario_id"]

            await self.repository.save_variant(
                variant_id=variant_id,
                base_scenario_id=seed["scenario_id"],
                capability=selected.capability,
                mode=mode,
                difficulty=difficulty,
                mutation_index=index + 1,
                variant_hash=variant_hash,
                scenario=variant,
            )

            if mode == "CONTROL":
                attack_override = neutral_control_attack()
                control_count += 1
            else:
                attack_override = None
                adversarial_count += 1

            match = await self.arena.run_scenario(
                variant,
                attack_override=attack_override,
            )

            baseline = await self.repository.baseline(
                selected.capability
            )

            regression = self.regression.detect(
                baseline_score=float(baseline["baseline_score"]),
                observed_score=match.judgement.blue_score,
                critical_violation=match.judgement.critical_violation,
            )

            step_id = "step_" + uuid.uuid4().hex

            await self.repository.save_step(
                step_id=step_id,
                run_id=run_id,
                step_index=index,
                capability=selected.capability,
                priority_score=selected.priority_score,
                mode=mode,
                difficulty=difficulty,
                variant_id=variant_id,
                match_id=match.match_id,
                blue_score=match.judgement.blue_score,
                winner=match.judgement.winner,
                critical_violation=match.judgement.critical_violation,
                regression_detected=regression.detected,
            )

            if regression.detected:
                event_id = "regression_" + uuid.uuid4().hex

                await self.repository.save_regression(
                    event_id=event_id,
                    run_id=run_id,
                    step_id=step_id,
                    capability=selected.capability,
                    baseline_score=float(baseline["baseline_score"]),
                    observed_score=match.judgement.blue_score,
                    score_delta=regression.delta,
                    critical_violation=match.judgement.critical_violation,
                    severity=regression.severity,
                    details={
                        "scenario_id": variant_id,
                        "match_id": match.match_id,
                        "mode": mode,
                        "difficulty": difficulty,
                    },
                )

                count = await self.repository.quarantine_match_memories(
                    match_id=match.match_id,
                    reason="CURRICULUM_" + regression.severity + "_REGRESSION",
                )
                quarantined_memories += count

                regressions.append(
                    RegressionEventResult(
                        capability=selected.capability,
                        baseline_score=float(baseline["baseline_score"]),
                        observed_score=match.judgement.blue_score,
                        delta=regression.delta,
                        severity=regression.severity,
                    )
                )

            results.append(match)

        maintenance = await self.memory_maintenance.refresh()
        quarantined_memories += maintenance["quarantined"]

        stats_after = await self.repository.capability_stats()
        ranked_after = self.scheduler.rank(stats_after)

        for item in ranked_after:
            await self.repository.save_snapshot(
                run_id=run_id,
                capability=item.capability,
                rating=item.rating,
                recent_average_score=item.recent_average_score,
                critical_rate=item.critical_rate,
                priority_score=item.priority_score,
            )

        average = sum(
            value.judgement.blue_score
            for value in results
        ) / len(results)

        critical_count = sum(
            1 for value in results
            if value.judgement.critical_violation
        )

        shortlist = await self.repository.candidate_shortlist(limit=5)

        summary = {
            "weakest_before": ranked_before[0].capability,
            "weakest_after": ranked_after[0].capability,
            "candidate_shortlist": shortlist,
            "control_scenarios": control_count,
            "adversarial_scenarios": adversarial_count,
            "quarantined_memories": quarantined_memories,
        }

        await self.repository.complete_run(
            run_id=run_id,
            completed_steps=len(results),
            average_blue_score=round(average, 2),
            critical_violations=critical_count,
            regression_count=len(regressions),
            summary=summary,
        )

        return CurriculumRunResult(
            run_id=run_id,
            requested_steps=steps,
            completed_steps=len(results),
            average_blue_score=round(average, 2),
            critical_violations=critical_count,
            regressions=regressions,
            control_scenarios=control_count,
            adversarial_scenarios=adversarial_count,
            weakest_capability_before=ranked_before[0].capability,
            weakest_capability_after=ranked_after[0].capability,
            candidate_shortlist=shortlist,
            quarantined_memories=quarantined_memories,
        )
