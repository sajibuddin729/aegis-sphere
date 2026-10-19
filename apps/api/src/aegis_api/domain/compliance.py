from typing import Literal
from pydantic import BaseModel


class ChinaComplianceResult(BaseModel):
    decision: Literal[
        "PASS",
        "BLOCK",
        "REVIEW",
    ]
    policy_version: str
    triggered_rules: list[str]
    source_refs: list[str]
    pii_detected: bool
    sensitive_identity_detected: bool
    cross_border_flag: bool
    important_data_flag: bool
    aml_pattern_flag: bool
    explanation: str

    @property
    def policy_status(self) -> str:
        return self.decision
