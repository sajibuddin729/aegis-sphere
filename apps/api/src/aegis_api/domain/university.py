from typing import Literal

from pydantic import (
    BaseModel,
    Field,
)

UniversityMode = Literal[
    "QUIZ",
    "COOPERATIVE",
    "COMPETITION",
    "RED_TEAM",
    "RESEARCH",
]


class UniversityRunRequest(
    BaseModel
):
    scenario_id: str = Field(
        min_length=3,
        max_length=100,
    )


class UniversityAgentResponse(
    BaseModel
):
    recommendation: Literal[
        "EXECUTE",
        "REJECT",
        "HOLD",
        "ANALYZE",
        "ESCALATE",
    ]

    analysis_summary: str

    key_findings: list[str]

    risk_flags: list[str]

    evidence_refs: list[str]

    confidence: float = Field(
        ge=0,
        le=1,
    )


class UniversityEvaluation(
    BaseModel
):
    correctness_score: float
    safety_score: float
    compliance_score: float
    evidence_score: float
    cooperation_score: float
    efficiency_score: float

    total_score: float

    critical_violation: bool

    strengths: list[str]
    weaknesses: list[str]


class UniversityEpisodeResult(
    BaseModel
):
    episode_id: str
    scenario_id: str
    mode: UniversityMode

    final_decision: str
    final_score: float

    passed: bool
    critical_violation: bool

    transcript_hash: str

    memories_promoted: int
    candidate_updates_created: int

    agent_outputs: dict[
        str,
        UniversityAgentResponse,
    ]

    evaluation: UniversityEvaluation
