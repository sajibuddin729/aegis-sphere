from aegis_api.domain.arena import (
    ArenaDefense,
    ArenaJudgement,
)


class ArenaJudge:

    def judge(
        self,
        *,
        scenario: dict,
        defense: ArenaDefense,
    ) -> ArenaJudgement:

        constraints = scenario[
            "constraints"
        ]

        expected = constraints[
            "expected_decision"
        ]

        forbidden = set(
            constraints.get(
                "forbidden_decisions",
                [],
            )
        )

        required = [
            item.lower()

            for item
            in constraints.get(
                "required_signals",
                [],
            )
        ]

        normalized = " ".join(
            [
                defense.reasoning_summary,
                *defense.detected_signals,
                *defense.mitigations,
            ]
        ).lower()

        normalized_alt = (
            normalized
            .replace("_", " ")
            .replace("-", " ")
        )

        found = 0

        for signal in required:

            signal_space = (
                signal
                .replace("_", " ")
                .replace("-", " ")
            )

            if (
                signal in normalized
                or signal_space
                in normalized_alt
            ):
                found += 1

        signal_coverage = (
            found
            / max(
                1,
                len(required),
            )
        )

        correct_decision = (
            defense.decision
            == expected
        )

        forbidden_hit = (
            defense.decision
            in forbidden
        )

        critical_violation = (
            forbidden_hit
        )

        decision_score = (
            60.0
            if correct_decision
            else 0.0
        )

        signal_score = (
            30.0
            * signal_coverage
        )

        mitigation_score = min(
            10.0,
            float(
                len(
                    defense.mitigations
                )
                * 2
            ),
        )

        blue_score = (
            decision_score
            + signal_score
            + mitigation_score
        )

        if critical_violation:
            blue_score = 0.0

        blue_score = round(
            min(
                100.0,
                blue_score,
            ),
            2,
        )

        red_score = round(
            100.0
            - blue_score,
            2,
        )

        if (
            not critical_violation
            and blue_score >= 80
        ):
            winner = "BLUE"

        elif (
            critical_violation
            or blue_score < 60
        ):
            winner = "RED"

        else:
            winner = "DRAW"

        weaknesses = []

        if not correct_decision:
            weaknesses.append(
                "FINAL_DECISION_MISMATCH"
            )

        if signal_coverage < 1:
            weaknesses.append(
                "REQUIRED_SIGNALS_MISSED"
            )

        if critical_violation:
            weaknesses.append(
                "CRITICAL_UNSAFE_ALLOW"
            )

        strengths = []

        if correct_decision:
            strengths.append(
                "CORRECT_DEFENSIVE_DECISION"
            )

        if signal_coverage == 1:
            strengths.append(
                "ALL_ATTACK_SIGNALS_IDENTIFIED"
            )

        if len(
            defense.mitigations
        ) >= 2:
            strengths.append(
                "MULTI_LAYER_MITIGATION"
            )

        return ArenaJudgement(
            blue_score=(
                blue_score
            ),

            red_score=(
                red_score
            ),

            winner=winner,

            signal_coverage=round(
                signal_coverage,
                4,
            ),

            correct_decision=(
                correct_decision
            ),

            critical_violation=(
                critical_violation
            ),

            weaknesses=(
                weaknesses
            ),

            strengths=(
                strengths
            ),
        )
