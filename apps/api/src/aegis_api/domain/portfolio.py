from typing import Literal

from pydantic import (
    BaseModel,
    Field,
    field_validator,
    model_validator,
)


class PortfolioState(BaseModel):
    manager: str

    acnyToken: str
    agixToken: str

    balanceAcnyMinor: str
    balanceAgixMinor: str

    totalValueAcnyMinor: str

    acnyAllocationBps: str
    agixAllocationBps: str

    priceAgixInAcny: str


class PortfolioAIPlan(BaseModel):

    action: Literal[
        "REBALANCE",
        "HOLD",
        "ANALYZE",
    ]

    target_acny_bps: int = Field(
        ge=0,
        le=10000,
    )

    target_agix_bps: int = Field(
        ge=0,
        le=10000,
    )

    min_acny_reserve_bps: int = Field(
        ge=0,
        le=10000,
    )

    max_acceptable_risk_bps: int | None = Field(
        default=None,
        ge=0,
        le=10000,
    )

    confidence: float = Field(
        ge=0,
        le=1,
    )

    reasoning_summary: str

    @field_validator("confidence", mode="before")
    @classmethod
    def normalize_confidence(cls, v: object) -> float:
        val = float(v)  # type: ignore[arg-type]
        if val > 1.0:
            return min(1.0, val / 100.0)
        return val

    @model_validator(mode="after")
    def allocation_sum(
        self,
    ):
        if (
            self.target_acny_bps
            + self.target_agix_bps
            != 10000
        ):
            raise ValueError(
                "Target allocation must equal 10000 BPS"
            )

        return self


class RebalanceCalculation(BaseModel):

    direction: Literal[
        "ACNY_TO_AGIX",
        "AGIX_TO_ACNY",
        "NONE",
    ]

    amount_in_minor: int

    min_amount_out_minor: int

    current_acny_bps: int
    current_agix_bps: int

    target_acny_bps: int
    target_agix_bps: int

    projected_acny_bps: int
    projected_agix_bps: int

    notional_acny_minor: int


class PortfolioSecurityAnalysis(
    BaseModel
):
    safe_to_continue: bool

    reserve_violation: bool

    concentration_risk: bool

    policy_bypass_attempt: bool

    suspicious_rebalance: bool

    severity: Literal[
        "SAFE",
        "CAUTION",
        "CRITICAL",
    ]

    reasons: list[str]


class AutonomousPortfolioResult(
    BaseModel
):
    decision: Literal[
        "EXECUTE",
        "REJECT",
        "HOLD",
        "ANALYZE",
    ]

    reason: str

    transaction_hash: str | None = None

    block_number: (
        int | str | None
    ) = None

    poi_hash: str | None = None
    intent_id: str | None = None
    policy_hash: str | None = None

    direction: str | None = None

    amount_in_minor: int | None = None
    amount_out_minor: int | None = None

    calculated_risk_bps: int | None = None

    target_acny_bps: int | None = None
    target_agix_bps: int | None = None

    final_acny_bps: int | None = None
    final_agix_bps: int | None = None

    security_status: str
    compliance_status: str

    market_summary: str

    reasoning_summary: str

    compliance_evidence_hash: str | None = None
    audit_transaction_hash: str | None = None
    audit_block_number: int | str | None = None
