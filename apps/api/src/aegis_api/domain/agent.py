from typing import Literal
from pydantic import BaseModel, ConfigDict, Field, field_validator


class FinancialDecision(BaseModel):
    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    decision: Literal[
        "APPROVE",
        "REJECT",
    ]
    action: Literal[
        "TRANSFER",
        "REBALANCE",
    ]
    reasonCode: str
    explanation: str
    approvedAmount: int | None = None
    approvedRecipient: str | None = None
    approvedRiskScore: int


class AutonomousRequest(BaseModel):
    instruction: str = Field(
        min_length=3,
        max_length=1000,
    )


class MarketAnalysis(BaseModel):
    outlook: Literal[
        "BULLISH",
        "NEUTRAL",
        "BEARISH",
    ]
    volatility: Literal[
        "LOW",
        "MEDIUM",
        "HIGH",
    ]
    liquidity: Literal[
        "LOW",
        "MEDIUM",
        "HIGH",
    ]
    summary: str
    warnings: list[str] = []


class FinancialPlan(BaseModel):
    action: Literal[
        "TRANSFER",
        "HOLD",
        "ANALYZE",
    ]
    recipient: str | None = None
    amount_minor: int | None = None
    purpose: str
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


class SecurityAnalysis(BaseModel):
    safe_to_continue: bool
    prompt_injection_detected: bool
    policy_bypass_attempt: bool
    suspicious_transfer_pattern: bool
    severity: Literal[
        "SAFE",
        "CAUTION",
        "CRITICAL",
    ]
    reasons: list[str]


class ComplianceAssessment(BaseModel):
    approved: bool
    pii_exposure: bool
    sensitive_data_exposure: bool
    cross_border_data_flag: bool
    policy_status: Literal[
        "PASS",
        "BLOCK",
    ]
    reasons: list[str]

    @property
    def decision(self) -> str:
        return self.policy_status



class AutonomousAgentResult(BaseModel):
    decision: Literal[
        "EXECUTE",
        "REJECT",
        "ANALYZE",
    ]
    reason: str
    action: str
    transaction_hash: str | None = None
    block_number: int | str | None = None
    poi_hash: str | None = None
    intent_id: str | None = None
    policy_hash: str | None = None
    risk_score_bps: int | None = None
    security_status: str
    compliance_status: str
    market_summary: str
    reasoning_summary: str
    compliance_evidence_hash: str | None = None
    audit_transaction_hash: str | None = None
    audit_block_number: int | str | None = None
