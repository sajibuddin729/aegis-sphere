from typing import Literal

from pydantic import (
    BaseModel,
    Field,
)


class CapabilityPriority(BaseModel):

    capability: str

    rating: float

    recent_average_score: float

    critical_rate: float

    staleness_score: float

    priority_score: float

    recommended_difficulty: int


class CurriculumRunRequest(BaseModel):

    steps: int = Field(
        default=14,
        ge=1,
        le=50,
    )


class RegressionEventResult(BaseModel):

    capability: str

    baseline_score: float

    observed_score: float

    delta: float

    severity: Literal[
        "HIGH",
        "CRITICAL",
    ]


class CurriculumRunResult(BaseModel):

    run_id: str

    requested_steps: int
    completed_steps: int

    average_blue_score: float

    critical_violations: int

    regressions: (
        list[RegressionEventResult]
    )

    control_scenarios: int
    adversarial_scenarios: int

    weakest_capability_before: str
    weakest_capability_after: str

    candidate_shortlist: (
        list[str]
    )

    quarantined_memories: int
