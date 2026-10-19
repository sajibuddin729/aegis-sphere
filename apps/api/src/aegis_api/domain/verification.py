from typing import Literal

from pydantic import (
    BaseModel,
    ConfigDict,
    Field,
)


class VerificationCheckResult(BaseModel):
    name: str
    passed: bool
    severity: Literal[
        "INFO",
        "LOW",
        "MEDIUM",
        "HIGH",
        "CRITICAL",
    ]
    details: dict
    evidence_hash: str


class VerificationReport(BaseModel):
    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    schema_version: str
    verification_id: str
    candidate_id: str
    candidate_hash: str
    verifier_version: str
    checks: list[VerificationCheckResult]
    verification_score: float
    final_decision: Literal[
        "VERIFIED",
        "REJECTED",
    ]
    rejection_reasons: list[str]


class VerificationAttestation(BaseModel):
    schema_version: str
    verification_id: str
    candidate_id: str
    candidate_hash: str
    report_hash: str
    verifier_version: str
    decision: Literal[
        "VERIFIED",
        "REJECTED",
    ]
    attestation_nonce: str
    timestamp: int
