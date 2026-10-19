from typing import Literal, Union
from pydantic import BaseModel, Field


class CandidateRiskAssessment(BaseModel):
    risk_id: str
    description: str
    severity: Literal[
        "LOW",
        "MEDIUM",
        "HIGH",
        "CRITICAL",
    ]
    mitigation: str


class CandidateVerificationTest(BaseModel):
    test_id: str
    description: str
    expected_result: str


class CandidateUpdateProposal(BaseModel):
    update_type: Literal[
        "PROMPT",
        "POLICY",
        "STRATEGY",
        "CODE",
    ]
    target_component: str
    title: str
    problem: str
    proposed_change: str
    expected_benefit: str
    risks: list[str]
    verification_tests: list[str]
    confidence: float = Field(ge=0, le=1)


class CandidateUpdateProposalV2(BaseModel):
    schema_version: Literal[
        "aegis-candidate-v2"
    ] = "aegis-candidate-v2"
    update_type: Literal[
        "PROMPT",
        "POLICY",
        "STRATEGY",
        "CODE",
    ]
    target_component: str
    title: str
    problem: str
    proposed_change: str
    expected_benefit: str
    risks: list[CandidateRiskAssessment]
    verification_tests: list[Union[CandidateVerificationTest, str]]
    rollback_plan: str
    security_invariants: list[str]
    confidence: float = Field(ge=0, le=1, default=0.95)
    deployment_authorized: bool = False
