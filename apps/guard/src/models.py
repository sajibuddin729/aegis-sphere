from pydantic import BaseModel, Field


class GuardRequest(BaseModel):
    content: str = Field(
        min_length=1,
        max_length=10000,
    )
    context: str = Field(
        default="financial",
        max_length=100,
    )


class GuardResult(BaseModel):
    safety: str
    categories: list[str]
    refusal: str | None = None
    blocked: bool
    raw_label: str
