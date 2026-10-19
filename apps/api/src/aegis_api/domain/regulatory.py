from typing import Literal

from pydantic import (
    BaseModel,
    ConfigDict,
)


class RegulatoryEvidenceRef(BaseModel):
    rank: int
    score: int  # Stored in BPS (no float in canonical data)
    source_id: str
    chunk_id: str
    title: str
    text_sha256: str
    source_snapshot_sha256: str


class ComplianceReasoning(BaseModel):
    assessment: Literal[
        "SUPPORTS_PASS",
        "SUPPORTS_BLOCK",
        "UNCERTAIN",
    ]
    applicable_source_ids: list[str]
    applicable_chunk_ids: list[str]
    summary: str


class ComplianceEvidencePackage(BaseModel):
    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    schema_version: str
    policy_version: str

    instruction_hash: str
    purpose_hash: str

    asset: str
    recipient_approved: bool

    risk_score_bps: int

    deterministic_decision: str
    triggered_rules: list[str]
    deterministic_source_refs: list[str]

    retrieval_query: str
    retrieval_model: str
    retrieval_mode: str
    retrieval_hits: list[RegulatoryEvidenceRef]

    reasoner_assessment: str
    reasoner_source_ids: list[str]
    reasoner_chunk_ids: list[str]
    reasoner_summary: str

    final_decision: str
    created_at_utc: str


class RegulatoryComplianceBundle(BaseModel):
    final_decision: str
    evidence_hash: str
    policy_version_hash: str
    retrieval_set_hash: str
    decision_hash: str
    package: ComplianceEvidencePackage

    @property
    def reasoner_summary(self) -> str:
        return self.package.reasoner_summary

    @property
    def reasoner_source_ids(self) -> list[str]:
        return self.package.reasoner_source_ids

    @property
    def evidence_ids(self) -> list[str]:
        return self.package.reasoner_source_ids
