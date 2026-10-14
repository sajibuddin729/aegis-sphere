from pydantic import BaseModel, ConfigDict


class AegisPoI(BaseModel):
    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    schema_version: str

    intent_id: str

    leader_agent_id: str
    model_version: str

    action: str
    asset: str
    amount_minor: int
    recipient: str

    risk_score_bps: int
    risk_class: str

    policy_version: str
    policy_hash: str

    purpose: str

    decision: str

    timestamp_utc: str
    expiry_utc: str

    nonce: str
