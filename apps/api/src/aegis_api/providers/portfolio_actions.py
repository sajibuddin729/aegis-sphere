from dataclasses import dataclass

from aegis_api.domain.portfolio_poi import (
    PortfolioPoI,
)
from aegis_api.providers.portfolio import (
    AegisPortfolioProvider,
)
from aegis_api.services.portfolio_poi import (
    PortfolioPoIService,
)


@dataclass(frozen=True)
class PreparedPortfolioAction:
    calculation: object
    risk: object
    poi: PortfolioPoI
    poi_hash: str
    intent_id: str
    policy_hash: str


class PortfolioActionProvider:
    def __init__(
        self,
        *,
        portfolio: AegisPortfolioProvider,
        poi_service: PortfolioPoIService,
        leader_agent_id: str,
        model_version: str,
    ):
        self.portfolio = portfolio
        self.poi_service = poi_service
        self.leader_agent_id = leader_agent_id
        self.model_version = model_version

    async def prepare(
        self,
        *,
        calculation,
        risk,
        market_snapshot: dict,
        compliance_evidence_hash: str | None = None,
        evidence_hash: str | None = None,
        user_request_hash: str | None = None,
        explanation_hash: str | None = None,
        decision: str = "APPROVE",
    ) -> PreparedPortfolioAction:
        (
            poi,
            poi_hash,
            intent_id,
            policy_hash,
        ) = self.poi_service.build(
            calculation=calculation,
            risk_score_bps=risk.score_bps,
            risk_class=risk.risk_class,
            market_snapshot=market_snapshot,
            leader_agent_id=self.leader_agent_id,
            model_version=self.model_version,
            compliance_evidence_hash=compliance_evidence_hash,
            evidence_hash=evidence_hash,
            user_request_hash=user_request_hash,
            explanation_hash=explanation_hash,
            decision=decision,
        )

        return PreparedPortfolioAction(
            calculation=calculation,
            risk=risk,
            poi=poi,
            poi_hash=poi_hash,
            intent_id=intent_id,
            policy_hash=policy_hash,
        )

    async def execute_prepared(
        self,
        prepared: PreparedPortfolioAction,
    ) -> dict:
        calculation = prepared.calculation
        risk = prepared.risk

        tx = await self.portfolio.rebalance(
            direction=calculation.direction,
            amount_in_minor=calculation.amount_in_minor,
            min_amount_out_minor=calculation.min_amount_out_minor,
            poi_hash=prepared.poi_hash,
            intent_id=prepared.intent_id,
            policy_hash=prepared.policy_hash,
            risk_score_bps=risk.score_bps,
            expiry=getattr(prepared.poi, "expiry", None),
        )

        return {
            "transaction": tx,
            "poi": prepared.poi,
            "poi_hash": prepared.poi_hash,
            "intent_id": prepared.intent_id,
            "policy_hash": prepared.policy_hash,
        }

    async def execute(
        self,
        *,
        calculation,
        risk,
        market_snapshot: dict,
    ) -> dict:
        prepared = await self.prepare(
            calculation=calculation,
            risk=risk,
            market_snapshot=market_snapshot,
        )
        return await self.execute_prepared(prepared)
