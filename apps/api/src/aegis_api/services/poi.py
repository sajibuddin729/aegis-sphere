from datetime import (
    datetime,
    timezone,
)
import secrets

from aegis_api.domain.poi import AegisPoI
from aegis_api.services.crypto import (
    canonical_hash,
    sha256_text,
)


class PoIService:
    def __init__(
        self,
        *,
        policy_version: str,
        policy_hash: str | None = None,
    ):
        self.policy_version = policy_version
        self.policy_hash = policy_hash

    def build_transfer_poi(
        self,
        *,
        recipient: str,
        amount_minor: int,
        risk_score_bps: int,
        risk_class: str,
        purpose: str = "",
        asset: str = "ACNY",
        leader_agent_id: str,
        model_version: str,
        user_request_hash: str | None = None,
        evidence_hash: str | None = None,
        explanation_hash: str | None = None,
        decision: str = "APPROVE",
        action: str = "TRANSFER",
        compliance_evidence_hash: str | None = None,
        intent_id: str | None = None,
    ) -> tuple[
        AegisPoI,
        str,
        str,
        str,
    ]:
        now = datetime.now(timezone.utc)
        now_ts = int(now.timestamp())
        expiry_ts = now_ts + 300  # 5 minutes validity

        if intent_id is None:
            intent_id = "0x" + secrets.token_hex(32)
        elif not intent_id.startswith("0x"):
            intent_id = "0x" + intent_id

        if not self.policy_hash:
            raise RuntimeError("EFFECTIVE_POLICY_HASH_REQUIRED")

        policy_hash = self.policy_hash
        if not policy_hash.startswith("0x"):
            policy_hash = "0x" + policy_hash

        ev_hash = evidence_hash or compliance_evidence_hash or ("0x" + "00" * 32)
        if not ev_hash.startswith("0x"):
            ev_hash = "0x" + ev_hash

        req_hash = user_request_hash or ("0x" + "00" * 32)
        if not req_hash.startswith("0x"):
            req_hash = "0x" + req_hash

        exp_hash = explanation_hash or sha256_text(purpose or "no-explanation")
        if not exp_hash.startswith("0x"):
            exp_hash = "0x" + exp_hash

        recip = recipient.lower()
        if not recip.startswith("0x"):
            recip = "0x" + recip

        poi = AegisPoI(
            intentId=intent_id,
            userRequestHash=req_hash,
            leaderAgentId=leader_agent_id,
            modelVersion=model_version,
            action=action,
            asset=asset,
            amount=amount_minor,
            recipient=recip,
            riskScore=risk_score_bps,
            riskClass=risk_class,
            policyVersion=self.policy_version,
            policyHash=policy_hash,
            evidenceHash=ev_hash,
            explanationHash=exp_hash,
            timestamp=now_ts,
            expiry=expiry_ts,
            nonce="0x" + secrets.token_hex(32),
            decision=decision,
        )

        poi_hash = canonical_hash(
            poi.model_dump()
        )

        return (
            poi,
            poi_hash,
            intent_id,
            policy_hash,
        )
