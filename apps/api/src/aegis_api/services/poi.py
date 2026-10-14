from datetime import (
    datetime,
    timedelta,
    timezone,
)
import secrets
import uuid

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
    ):
        self.policy_version = (
            policy_version
        )

    def build_transfer_poi(
        self,
        *,
        recipient: str,
        amount_minor: int,
        risk_score_bps: int,
        risk_class: str,
        purpose: str,
        asset: str,
        leader_agent_id: str,
        model_version: str,
    ) -> tuple[
        AegisPoI,
        str,
        str,
        str,
    ]:

        now = datetime.now(timezone.utc)

        expiry = (
            now
            + timedelta(minutes=5)
        )

        intent_id = sha256_text(
            str(uuid.uuid4())
        )

        policy_hash = sha256_text(
            self.policy_version
        )

        poi = AegisPoI(
            schema_version="aegis-poi-v1",

            intent_id=intent_id,

            leader_agent_id=(
                leader_agent_id
            ),

            model_version=(
                model_version
            ),

            action="TRANSFER",

            asset=asset,

            amount_minor=amount_minor,

            recipient=recipient,

            risk_score_bps=(
                risk_score_bps
            ),

            risk_class=risk_class,

            policy_version=(
                self.policy_version
            ),

            policy_hash=policy_hash,

            purpose=purpose,

            decision="EXECUTE",

            timestamp_utc=(
                now.isoformat()
            ),

            expiry_utc=(
                expiry.isoformat()
            ),

            nonce=secrets.token_hex(16),
        )

        poi_hash = canonical_hash(
            poi.model_dump(
                mode="json"
            )
        )

        return (
            poi,
            poi_hash,
            intent_id,
            policy_hash,
        )
