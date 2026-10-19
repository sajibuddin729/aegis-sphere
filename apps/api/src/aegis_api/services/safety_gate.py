from dataclasses import dataclass
from aegis_api.clients.guard import (
    GuardClient,
)


@dataclass(frozen=True)
class SafetyDecision:
    allowed: bool
    severity: str
    categories: list[str]


class IndependentSafetyGate:

    def __init__(
        self,
        guard: GuardClient,
    ):
        self.guard = guard

    async def inspect(
        self,
        content: str,
    ) -> SafetyDecision:

        result = await self.guard.check(
            content
        )
        safety = result["safety"]

        if safety == "Unsafe":
            return SafetyDecision(
                allowed=False,
                severity="CRITICAL",
                categories=result[
                    "categories"
                ],
            )

        if safety == "Controversial":
            return SafetyDecision(
                allowed=True,
                severity="CAUTION",
                categories=result[
                    "categories"
                ],
            )

        return SafetyDecision(
            allowed=True,
            severity="SAFE",
            categories=result[
                "categories"
            ],
        )
