import uuid

from aegis_api.domain.arena import (
    ArenaMatchResult,
)

from aegis_api.services.crypto import (
    canonical_hash,
    sha256_text,
)


class ArenaOrchestrator:

    def __init__(
        self,
        *,
        scenarios,
        agents,
        judge,
        elo,
        repository,
        memory,
        memory_policy,
        evolution_agent,
        candidates,
    ):
        self.scenarios = scenarios
        self.agents = agents

        self.judge = judge
        self.elo = elo

        self.repository = repository

        self.memory = memory

        self.memory_policy = (
            memory_policy
        )

        self.evolution_agent = (
            evolution_agent
        )

        self.candidates = candidates

    async def run(
        self,
        scenario_id: str,
    ) -> ArenaMatchResult:
        scenario = (
            self.scenarios
            .get(
                scenario_id
            )
        )
        return await self.run_scenario(
            scenario
        )

    async def run_scenario(
        self,
        scenario: dict,
        *,
        attack_override=None,
    ) -> ArenaMatchResult:
        scenario_id = (
            scenario[
                "scenario_id"
            ]
        )

        scenario_hash = (
            canonical_hash(
                scenario
            )
        )

        match_id = (
            "match_"
            + uuid.uuid4().hex
        )

        memories = await self.memory.recall(
            query=(
                scenario[
                    "title"
                ]
                + " "
                + scenario[
                    "capability"
                ]
            ),

            category=None,
        )

        if attack_override is None:
            attack = (
                await self.agents
                .red_attack(
                    scenario
                )
            )
        else:
            attack = attack_override

        initial_defense = (
            await self.agents
            .defend(
                scenario=scenario,

                attack=attack,

                memories=memories,
            )
        )

        critique = (
            await self.agents
            .critique(
                scenario=scenario,

                attack=attack,

                defense=(
                    initial_defense
                ),
            )
        )

        final_defense = (
            await self.agents
            .final_defense(
                scenario=scenario,

                attack=attack,

                initial_defense=(
                    initial_defense
                ),

                critique=critique,

                memories=memories,
            )
        )

        judgement = (
            self.judge.judge(
                scenario=scenario,

                defense=(
                    final_defense
                ),
            )
        )

        recalled_memory_ids = [
            item["memory_id"]
            for item
            in memories
            if isinstance(item, dict) and "memory_id" in item
        ]

        if hasattr(self.memory, "record_reuse_outcome"):
            await self.memory.record_reuse_outcome(
                memory_ids=recalled_memory_ids,
                successful=(
                    judgement.winner == "BLUE"
                    and not judgement.critical_violation
                ),
            )


        capability = (
            scenario[
                "capability"
            ]
        )

        red_before = (
            await self.repository
            .rating(
                agent_role=(
                    "RED_TEAM"
                ),

                capability=(
                    capability
                ),
            )
        )

        blue_before = (
            await self.repository
            .rating(
                agent_role=(
                    "BLUE_TEAM"
                ),

                capability=(
                    capability
                ),
            )
        )

        elo = self.elo.update(
            red_rating=(
                red_before
            ),

            blue_rating=(
                blue_before
            ),

            winner=(
                judgement.winner
            ),
        )

        if (
            judgement.winner
            == "BLUE"
        ):
            red_result = "LOSS"
            blue_result = "WIN"

        elif (
            judgement.winner
            == "RED"
        ):
            red_result = "WIN"
            blue_result = "LOSS"

        else:
            red_result = "DRAW"
            blue_result = "DRAW"

        await self.repository.update_rating(
            agent_role="RED_TEAM",

            capability=(
                capability
            ),

            rating=(
                elo.red_after
            ),

            result=(
                red_result
            ),
        )

        await self.repository.update_rating(
            agent_role="BLUE_TEAM",

            capability=(
                capability
            ),

            rating=(
                elo.blue_after
            ),

            result=(
                blue_result
            ),
        )

        transcript = {
            "scenario_hash":
                scenario_hash,

            "attack":
                attack.model_dump(
                    mode="json"
                ),

            "initial_defense":
                initial_defense
                .model_dump(
                    mode="json"
                ),

            "critique":
                critique.model_dump(
                    mode="json"
                ),

            "final_defense":
                final_defense
                .model_dump(
                    mode="json"
                ),

            "judgement":
                judgement.model_dump(
                    mode="json"
                ),
        }

        transcript_hash = (
            canonical_hash(
                transcript
            )
        )

        await self.repository.save_match(
            match_id=(
                match_id
            ),

            scenario_id=(
                scenario_id
            ),

            scenario_hash=(
                scenario_hash
            ),

            capability=(
                capability
            ),

            red_output_hash=(
                canonical_hash(
                    attack.model_dump(
                        mode="json"
                    )
                )
            ),

            blue_output_hash=(
                canonical_hash(
                    final_defense
                    .model_dump(
                        mode="json"
                    )
                )
            ),

            critic_output_hash=(
                canonical_hash(
                    critique.model_dump(
                        mode="json"
                    )
                )
            ),

            blue_score=(
                judgement
                .blue_score
            ),

            red_score=(
                judgement
                .red_score
            ),

            winner=(
                judgement
                .winner
            ),

            critical_violation=(
                judgement
                .critical_violation
            ),

            transcript_hash=(
                transcript_hash
            ),

            outcome=transcript,
        )

        admission = (
            self.memory_policy
            .evaluate(
                scenario=scenario,

                defense=(
                    final_defense
                ),

                judgement=(
                    judgement
                ),
            )
        )

        admission_id = (
            "admission_"
            + uuid.uuid4().hex
        )

        content_hash = (
            sha256_text(
                admission.content
                if admission.content
                else (
                    match_id
                    + ":rejected"
                )
            )
        )

        await self.repository.memory_admission(
            admission_id=(
                admission_id
            ),

            match_id=(
                match_id
            ),

            content_hash=(
                content_hash
            ),

            score=(
                judgement
                .blue_score
            ),

            critical_violation=(
                judgement
                .critical_violation
            ),

            admitted=(
                admission
                .admitted
            ),

            rejection_reason=(
                admission.reason
            ),
        )

        if admission.admitted:

            await self.memory.promote_memory(
                source_episode_id=None,

                category=(
                    capability
                ),

                content=(
                    admission
                    .content
                ),

                utility_score=(
                    judgement
                    .blue_score
                ),

                metadata={
                    "source_match_id":
                        match_id,

                    "scenario_id":
                        scenario_id,

                    "transcript_hash":
                        transcript_hash,

                    "memory_source":
                        "BLUE_DEFENSE_ONLY"
                },
            )

        proposal = (
            await self.evolution_agent
            .propose(
                scenario=scenario,

                outputs={
                    "RED_TEAM":
                        attack.model_dump(),

                    "BLUE_TEAM":
                        final_defense
                        .model_dump(),

                    "BLUE_CRITIC":
                        critique.model_dump(),
                },

                evaluation=(
                    judgement
                    .model_dump()
                ),
            )
        )

        (
            candidate_id,
            candidate_status,
        ) = (
            await self.candidates
            .persist(
                source_match_id=(
                    match_id
                ),

                proposal=proposal,
            )
        )

        return ArenaMatchResult(
            match_id=(
                match_id
            ),

            scenario_id=(
                scenario_id
            ),

            capability=(
                capability
            ),

            attack=attack,

            initial_defense=(
                initial_defense
            ),

            blue_critique=(
                critique
            ),

            final_defense=(
                final_defense
            ),

            judgement=(
                judgement
            ),

            elo=elo,

            transcript_hash=(
                transcript_hash
            ),

            memory_admitted=(
                admission
                .admitted
            ),

            candidate_id=(
                candidate_id
            ),

            candidate_status=(
                candidate_status
            ),
        )
