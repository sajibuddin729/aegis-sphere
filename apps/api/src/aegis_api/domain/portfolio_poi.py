from pydantic import (
    BaseModel,
    ConfigDict,
)


class PortfolioPoI(BaseModel):

    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    schema_version: str

    intent_id: str

    leader_agent_id: str
    model_version: str

    action: str

    source_asset: str
    target_asset: str

    amount_in_minor: int
    min_amount_out_minor: int

    current_acny_bps: int
    current_agix_bps: int

    target_acny_bps: int
    target_agix_bps: int

    projected_acny_bps: int
    projected_agix_bps: int

    risk_score_bps: int
    risk_class: str

    market_snapshot_hash: str

    policy_version: str
    policy_hash: str

    decision: str

    timestamp_utc: str
    expiry_utc: str

    nonce: str
    compliance_evidence_hash: str | None = None
