import re

from dataclasses import dataclass

from aegis_api.domain.arena import (
    ArenaDefense,
    ArenaJudgement,
)


@dataclass(frozen=True)
class MemoryAdmission:

    admitted: bool

    content: str

    reason: str | None


class ArenaMemoryAdmissionPolicy:

    SECRET_PATTERNS = [
        re.compile(
            r"BEGIN PRIVATE KEY",
            re.IGNORECASE,
        ),

        re.compile(
            r"AEGIS_BRIDGE_KEY",
            re.IGNORECASE,
        ),

        re.compile(
            r"FISCO_ACCOUNT_PEM",
            re.IGNORECASE,
        ),

        re.compile(
            r"\b(?:seed phrase|mnemonic)\b",
            re.IGNORECASE,
        ),
    ]

    def evaluate(
        self,
        *,
        scenario: dict,
        defense: ArenaDefense,
        judgement: ArenaJudgement,
    ) -> MemoryAdmission:

        if (
            judgement
            .critical_violation
        ):
            return MemoryAdmission(
                admitted=False,
                content="",
                reason=(
                    "CRITICAL_VIOLATION"
                ),
            )

        if (
            judgement.winner
            != "BLUE"
        ):
            return MemoryAdmission(
                admitted=False,
                content="",
                reason=(
                    "BLUE_TEAM_NOT_WINNER"
                ),
            )

        if (
            judgement.blue_score
            < 85
        ):
            return MemoryAdmission(
                admitted=False,
                content="",
                reason=(
                    "SCORE_BELOW_MEMORY_THRESHOLD"
                ),
            )

        # Deliberately exclude the Red-Team
        # payload/summary from reusable memory.
        content = (
            "Capability: "
            + scenario[
                "capability"
            ]
            + "\nScenario: "
            + scenario[
                "title"
            ]
            + "\nDefensive decision: "
            + defense.decision
            + "\nDetected signals: "
            + "; ".join(
                defense
                .detected_signals
            )
            + "\nMitigations: "
            + "; ".join(
                defense
                .mitigations
            )
        )

        for pattern in (
            self.SECRET_PATTERNS
        ):

            if pattern.search(
                content
            ):
                return MemoryAdmission(
                    admitted=False,
                    content="",
                    reason=(
                        "SECRET_PATTERN_DETECTED"
                    ),
                )

        return MemoryAdmission(
            admitted=True,
            content=content,
            reason=None,
        )
