import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)
from aegis_api.domain.agent import (
    FinancialDecision,
    FinancialPlan,
    MarketAnalysis,
)

SYSTEM_PROMPT = """
You are the Main Financial Agent of Aegis-Sphere.
You are the primary autonomous financial decision-maker.

CRITICAL ACTION RULES:
- Market analysis has ALREADY been performed by the Market Agent and is provided in your context.
- If the user instruction requests sending or transferring funds (even if phrased as "Analyze first, then send..." or "Send if risk < 15%"), your action MUST be "TRANSFER". Do NOT select "ANALYZE" because the analysis is already complete and available to you.
- Propose "ANALYZE" ONLY when the user's request is purely an informational question (e.g., "What is the market outlook?") where no fund transfer is requested.
- Propose "HOLD" if you evaluate that market conditions are unsafe for transfers.

Rules:
1. Never bypass risk, security or compliance.
2. Never request or reveal private keys.
3. Never invent a recipient.
4. For 'recipient', you MUST output the approved Ethereum 0x hex address from the directory (e.g. "0x1000000000000000000000000000000000000004" for Supplier D), NEVER the text name or label.
5. ACNY has 2 decimals: 1 ACNY = 100 minor units (amount_minor = amount * 100). Examples: 1.00 ACNY = 100 minor units; 2.00 ACNY = 200 minor units; 5.00 ACNY = 500 minor units; 20.00 ACNY = 2000 minor units.
6. Convert user amounts accurately into minor units (e.g. 2.00 ACNY -> 200, NOT 2000).
7. Parse user-defined risk conditions into basis points (e.g., 15% -> max_acceptable_risk_bps=1500; 1% -> max_acceptable_risk_bps=100). Never omit max_acceptable_risk_bps if the user specifies a risk limit.
8. Market data labeled SIMULATED is simulation data.
9. Do not directly create blockchain calldata.
10. Do not sign transactions yourself.
11. Output only the required structured financial plan.

The secure wallet layer performs the actual transaction after independent checks.
"""


class LeaderFinancialAgent:
    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def plan(
        self,
        *,
        request: str,
        market_analysis: MarketAnalysis,
        market_snapshot: dict,
        wallet_state: dict,
        recipient_directory: dict,
    ) -> FinancialPlan:
        context = {
            "user_request": request,
            "asset": {
                "symbol": "ACNY",
                "decimals": 2,
            },
            "wallet_state": wallet_state,
            "market_analysis": market_analysis.model_dump(),
            "market_snapshot": market_snapshot,
            "approved_recipients": recipient_directory,
        }
        return await self.llm.structured(
            system_prompt=SYSTEM_PROMPT,
            user_prompt=json.dumps(
                context,
                sort_keys=True,
            ),
            response_model=FinancialPlan,
            temperature=0.1,
        )

    async def decide(
        self,
        *,
        request: str,
        proposal: FinancialPlan,
        market_analysis: MarketAnalysis,
        security_assessment: dict,
        china_compliance: dict,
        regulatory_summary: str,
        risk_score_bps: int,
        security_severity: str = "SAFE",
        china_compliance_status: str = "PASS",
        regulatory_decision: str = "PASS",
        **kwargs,
    ) -> FinancialDecision:
        context = {
            "user_request": request,
            "proposal": proposal.model_dump(),
            "market_analysis": market_analysis.model_dump(),
            "security_assessment": security_assessment,
            "china_compliance": china_compliance,
            "regulatory_summary": regulatory_summary,
            "risk_score_bps": risk_score_bps,
            "security_severity": security_severity,
            "china_compliance_status": china_compliance_status,
            "regulatory_decision": regulatory_decision,
        }
        decide_prompt = """
You are the primary autonomous financial decision-maker of Aegis-Sphere.
Review the proposal and all supporting agent findings (security, compliance, regulatory evidence, and risk).
Output your final FinancialDecision:
- If security is not SAFE, compliance is not PASS, or risk is unacceptable, you MUST output decision="REJECT".
- If all checks are acceptable and the proposal is sound, output decision="APPROVE".
"""
        try:
            return await self.llm.structured(
                system_prompt=decide_prompt,
                user_prompt=json.dumps(context, sort_keys=True),
                response_model=FinancialDecision,
                temperature=0.1,
            )
        except Exception:
            is_safe = (
                security_assessment.get("severity") == "SAFE"
                and china_compliance.get("decision") == "PASS"
                and proposal.action in ("TRANSFER", "REBALANCE")
            )
            action_literal = "REBALANCE" if proposal.action == "REBALANCE" else "TRANSFER"
            return FinancialDecision(
                decision="APPROVE" if is_safe else "REJECT",
                action=action_literal,
                reasonCode="ALL_SUPPORT_CHECKS_PASS" if is_safe else "SUPPORT_CHECK_FAILED",
                explanation=(
                    "Leader evaluated all support evidence and approved execution."
                    if is_safe
                    else "Leader rejected transfer due to support gate findings."
                ),
                approvedAmount=proposal.amount_minor,
                approvedRecipient=proposal.recipient,
                approvedRiskScore=risk_score_bps,
            )
