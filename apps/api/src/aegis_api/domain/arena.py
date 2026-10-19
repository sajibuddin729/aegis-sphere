from typing import Literal

from pydantic import (
    BaseModel,
    Field,
    field_validator,
)


class ArenaAttack(BaseModel):

    attack_summary: str

    targeted_control: str

    attack_signals: list[str]

    attacker_objectives: list[str]

    severity: Literal[
        "LOW",
        "MEDIUM",
        "HIGH",
        "CRITICAL",
    ]

    simulation_only: bool = True


class ArenaDefense(BaseModel):

    decision: Literal[
        "ALLOW",
        "BLOCK",
        "HOLD",
        "ESCALATE",
        "EMERGENCY_STOP",
    ]

    detected_signals: list[str]

    mitigations: list[str]

    evidence_refs: list[str]

    reasoning_summary: str

    confidence: float = Field(
        ge=0,
        le=1,
    )

    @field_validator("confidence", mode="before")
    @classmethod
    def normalize_confidence(cls, v):
        if isinstance(v, (int, float)) and v > 1.0:
            return v / 100.0
        return v


class ArenaCritique(BaseModel):

    defense_is_sufficient: bool

    missed_signals: list[str]

    contradictions: list[str]

    recommended_decision: Literal[
        "ALLOW",
        "BLOCK",
        "HOLD",
        "ESCALATE",
        "EMERGENCY_STOP",
    ]

    critique_summary: str


class ArenaJudgement(BaseModel):

    blue_score: float
    red_score: float

    winner: Literal[
        "BLUE",
        "RED",
        "DRAW",
    ]

    signal_coverage: float

    correct_decision: bool

    critical_violation: bool

    weaknesses: list[str]

    strengths: list[str]


class EloUpdate(BaseModel):

    red_before: float
    red_after: float

    blue_before: float
    blue_after: float


class ArenaMatchResult(BaseModel):

    match_id: str

    scenario_id: str
    capability: str

    attack: ArenaAttack

    initial_defense: ArenaDefense

    blue_critique: ArenaCritique

    final_defense: ArenaDefense

    judgement: ArenaJudgement

    elo: EloUpdate

    transcript_hash: str

    memory_admitted: bool

    candidate_id: str | None = None

    candidate_status: str | None = None


class ArenaLeagueResult(BaseModel):

    league_id: str

    matches: int

    blue_wins: int
    red_wins: int
    draws: int

    critical_violations: int

    average_blue_score: float

    match_ids: list[str]
