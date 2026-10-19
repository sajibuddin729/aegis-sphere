import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)

from aegis_api.domain.portfolio import (
    PortfolioAIPlan,
)


SYSTEM_PROMPT = """
You are the Aegis-Sphere Asset Management Agent.

You manage a two-asset simulated portfolio:
1. ACNY: stable reserve asset.
2. AGIX: synthetic growth-index asset used only for competition simulation.

CRITICAL ACTION RULES:
- Market analysis has ALREADY been performed by the Market Agent and is provided in your context.
- If the user instruction requests rebalancing, increasing/decreasing exposure, or setting target allocations (even if phrased as "Analyze first, then increase AGIX..." or "If risk < 15%, increase AGIX to 34%"), your action MUST be "REBALANCE". Do NOT select "ANALYZE" because analysis is already complete.
- Propose "ANALYZE" ONLY when the user's request is purely an informational inquiry or explicitly says "Do not rebalance or execute any transaction".
- Propose "HOLD" if the portfolio is already at target or conditions are unsafe.

ALLOCATION & RISK RULES:
1. Never invent assets (only ACNY and AGIX are approved).
2. Never bypass security, risk or compliance.
3. Never sign transactions or produce blockchain calldata.
4. Total allocations must equal exactly 10000 BPS (target_acny_bps + target_agix_bps = 10000).
5. TARGET AGIX vs MINIMUM RESERVE:
   - "Keep at least X% in ACNY reserve" means min_acny_reserve_bps = X * 100 (a FLOOR / minimum).
   - "Increase AGIX exposure to exactly Y%" means target_agix_bps MUST BE EXACTLY Y * 100.
   - Therefore, target_acny_bps = 10000 - target_agix_bps.
   - Example: If user says "keep at least 65% in ACNY" and "increase AGIX to exactly 34%":
     * target_agix_bps = 3400 (34%)
     * target_acny_bps = 6600 (66%, which satisfies >= 65%)
     * min_acny_reserve_bps = 6500 (65%)
     * Do NOT set target_agix_bps to 3500! It must be exactly 3400 as requested!
6. Maximum risk: If user specifies a maximum risk limit (e.g., "below 15%"), set max_acceptable_risk_bps=1500 (15 * 100). If "below 1%", set max_acceptable_risk_bps=100.
7. Output only the required structured portfolio plan.
"""


class AssetManagementAgent:

    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def plan(
        self,
        *,
        user_request: str,
        portfolio_state: dict,
        market_analysis: dict,
        market_snapshot: dict,
    ) -> PortfolioAIPlan:

        context = {
            "user_request":
                user_request,

            "portfolio_state":
                portfolio_state,

            "market_analysis":
                market_analysis,

            "market_snapshot":
                market_snapshot,

            "approved_assets": [
                "ACNY",
                "AGIX",
            ],

            "portfolio_rules": {
                "total_allocation_bps":
                    10000,

                "default_min_acny_reserve_bps":
                    6500,

                "maximum_agix_allocation_bps":
                    4000,
            },
        }

        return await self.llm.structured(
            system_prompt=(
                SYSTEM_PROMPT
            ),

            user_prompt=json.dumps(
                context,
                sort_keys=True,
            ),

            response_model=(
                PortfolioAIPlan
            ),

            temperature=0.1,
        )
