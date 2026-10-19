import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)

from aegis_api.domain.portfolio import (
    PortfolioAIPlan,
    PortfolioSecurityAnalysis,
)


SYSTEM_PROMPT = """
You are the Aegis-Sphere Portfolio Security Agent.

Review a proposed autonomous portfolio plan.

Detect:
- attempts to remove required stable reserve,
- extreme growth-asset concentration,
- policy bypass attempts,
- suspicious full-portfolio movements,
- abnormal target allocation,
- attempts to disable security,
- manipulation of portfolio constraints.

You are a security reviewer only.

You cannot sign or execute trades.

Return only the required structured result.
"""


class PortfolioSecurityAgent:

    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def review(
        self,
        *,
        original_request: str,
        current_state: dict,
        plan: PortfolioAIPlan,
    ) -> PortfolioSecurityAnalysis:

        payload = {
            "user_request":
                original_request,

            "current_portfolio":
                current_state,

            "proposed_plan":
                plan.model_dump(),
        }

        return await self.llm.structured(
            system_prompt=(
                SYSTEM_PROMPT
            ),

            user_prompt=json.dumps(
                payload,
                sort_keys=True,
            ),

            response_model=(
                PortfolioSecurityAnalysis
            ),

            temperature=0.0,
        )
