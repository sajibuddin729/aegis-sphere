from pydantic import (
    BaseModel,
    Field,
)


class RetrievalRequest(BaseModel):
    query: str = Field(
        min_length=3,
        max_length=4000,
    )
    top_k: int = Field(
        default=5,
        ge=1,
        le=8,
    )


class RetrievalHit(BaseModel):
    rank: int
    score: float
    chunk_id: str
    source_id: str
    title: str
    authority: str
    text_sha256: str
    source_snapshot_sha256: str
    text: str


class RetrievalResult(BaseModel):
    query: str
    model: str
    retrieval_mode: str
    hits: list[RetrievalHit]


class EmbeddingRequest(BaseModel):
    texts: list[str] = Field(
        min_length=1,
        max_length=16,
    )


class EmbeddingResult(BaseModel):
    model: str
    dimension: int
    embeddings: list[list[float]]

