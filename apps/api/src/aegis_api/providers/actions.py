from dataclasses import dataclass

from aegis_api.domain.poi import (
    AegisPoI,
)
from aegis_api.domain.wallet import (
    TransferDecision,
    TransferRequest,
)
from aegis_api.providers.wallet import (
    AegisWalletProvider,
)
from aegis_api.services.poi import (
    PoIService,
)
from aegis_api.services.risk import (
    RiskService,
)


@dataclass(frozen=True)
class PreparedTransfer:
    request: TransferRequest
    poi: AegisPoI
    poi_hash: str
    intent_id: str
    policy_hash: str


class AegisActionProvider:
    def __init__(
        self,
        *,
        wallet: AegisWalletProvider,
        risk_service: RiskService,
        poi_service: PoIService,
        asset_symbol: str,
    ):
        self.wallet = wallet
        self.risk_service = risk_service
        self.poi_service = poi_service
        self.asset_symbol = asset_symbol
        self.executed_intents: set[str] = set()

    async def prepare_transfer(
        self,
        request: TransferRequest,
        *,
        leader_agent_id: str,
        model_version: str,
        compliance_evidence_hash: str | None = None,
        evidence_hash: str | None = None,
        user_request_hash: str | None = None,
        explanation_hash: str | None = None,
        decision: str = "APPROVE",
        action: str = "TRANSFER",
        intent_id: str | None = None,
    ) -> PreparedTransfer:
        spent_result = (
            await self.wallet.get_daily_spent()
        )
        spent_today = int(
            spent_result["spentTodayMinor"]
        )

        risk = self.risk_service.evaluate(
            amount_minor=request.amount_minor,
            risk_score_bps=request.risk_score_bps,
            spent_today_minor=spent_today,
        )

        if not risk.approved:
            raise RuntimeError(
                f"Risk evaluation rejected: {risk.reason}"
            )

        (
            poi,
            poi_hash,
            intent_id,
            policy_hash,
        ) = self.poi_service.build_transfer_poi(
            recipient=request.recipient,
            amount_minor=request.amount_minor,
            risk_score_bps=request.risk_score_bps,
            risk_class=risk.risk_class,
            purpose=request.purpose,
            asset=self.asset_symbol,
            leader_agent_id=leader_agent_id,
            model_version=model_version,
            compliance_evidence_hash=compliance_evidence_hash,
            evidence_hash=evidence_hash or compliance_evidence_hash,
            user_request_hash=user_request_hash,
            explanation_hash=explanation_hash,
            decision=decision,
            action=action,
            intent_id=intent_id,
        )

        return PreparedTransfer(
            request=request,
            poi=poi,
            poi_hash=poi_hash,
            intent_id=intent_id,
            policy_hash=policy_hash,
        )

    async def execute_prepared(
        self,
        prepared: PreparedTransfer,
    ) -> TransferDecision:
        if prepared.intent_id in self.executed_intents:
            raise RuntimeError(
                f"AEGIS: INTENT_ALREADY_EXECUTED ({prepared.intent_id})"
            )

        request = prepared.request
        tx = await self.wallet.transfer(
            recipient=request.recipient,
            amount_minor=request.amount_minor,
            poi_hash=prepared.poi_hash,
            intent_id=prepared.intent_id,
            policy_hash=prepared.policy_hash,
            risk_score_bps=request.risk_score_bps,
            expiry=getattr(prepared.poi, "expiry", None),
        )

        self.executed_intents.add(prepared.intent_id)

        return TransferDecision(
            decision="EXECUTE",
            reason="ON_CHAIN_SUCCESS",
            transaction_hash=tx["transactionHash"],
            block_number=tx["blockNumber"],
            poi_hash=prepared.poi_hash,
            intent_id=prepared.intent_id,
            policy_hash=prepared.policy_hash,
            risk_score_bps=request.risk_score_bps,
            amount_minor=request.amount_minor,
            recipient=request.recipient,
        )

    async def transfer_asset(
        self,
        request: TransferRequest,
        *,
        leader_agent_id: str,
        model_version: str,
    ) -> TransferDecision:
        prepared = await self.prepare_transfer(
            request,
            leader_agent_id=leader_agent_id,
            model_version=model_version,
        )
        return await self.execute_prepared(prepared)
