from typing import Any
from pydantic import (
    BaseModel,
    ConfigDict,
    field_validator,
)


def assert_no_float(value: Any) -> None:
    if isinstance(value, float):
        raise ValueError("FLOAT_NOT_ALLOWED_IN_CANONICAL_DATA")
    if isinstance(value, dict):
        for v in value.values():
            assert_no_float(v)
    elif isinstance(value, list):
        for item in value:
            assert_no_float(item)


def sanitize_evidence_dict(item: Any) -> Any:
    if isinstance(item, float):
        # Deterministic representation: prefer BPS/int or exact string
        if item.is_integer():
            return int(item)
        s = f"{item:.4f}".rstrip("0")
        if s.endswith("."):
            s = s[:-1]
        return s
    elif isinstance(item, dict):
        return {str(k): sanitize_evidence_dict(v) for k, v in item.items()}
    elif isinstance(item, list):
        return [sanitize_evidence_dict(v) for v in item]
    return item


class AegisEvidence(BaseModel):
    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    intent: dict
    marketState: dict
    proposal: dict
    securityAssessment: dict
    chinaComplianceAssessment: dict
    regulatoryEvidence: dict
    riskAssessment: dict
    leaderDecision: dict

    @field_validator(
        "intent",
        "marketState",
        "proposal",
        "securityAssessment",
        "chinaComplianceAssessment",
        "regulatoryEvidence",
        "riskAssessment",
        "leaderDecision",
        mode="before",
    )
    @classmethod
    def sanitize_and_validate(cls, v: Any) -> dict:
        sanitized = sanitize_evidence_dict(v) if isinstance(v, dict) else v
        assert_no_float(sanitized)
        return sanitized
