import os
import secrets
from pathlib import Path
from fastapi import (
    FastAPI,
    Header,
    HTTPException,
)
from models import (
    EmbeddingRequest,
    EmbeddingResult,
    RetrievalRequest,
    RetrievalResult,
)
from retriever import (
    RegulatoryRetriever,
)

PROJECT_ROOT = (
    Path(__file__)
    .resolve()
    .parents[3]
)
RAG_KEY = os.environ["AEGIS_RAG_KEY"]
MODEL_NAME = os.getenv(
    "AEGIS_RAG_MODEL",
    "BAAI/bge-m3",
)
DEVICE = os.getenv(
    "AEGIS_RAG_DEVICE",
    "mps",
)

retriever = RegulatoryRetriever(
    project_root=PROJECT_ROOT,
    model_name=MODEL_NAME,
    device=DEVICE,
)

app = FastAPI(
    title="Aegis Regulatory RAG",
    version="0.1.0",
)


def authorize(
    supplied: str | None,
):
    if supplied is None or not secrets.compare_digest(
        supplied,
        RAG_KEY,
    ):
        raise HTTPException(
            status_code=401,
            detail="UNAUTHORIZED",
        )


@app.get("/health")
async def health():
    return {
        "status": "ok",
        "service": "aegis-regulatory-rag",
        "model": MODEL_NAME,
        "device": DEVICE,
        "chunks": len(retriever.records),
        "dimension": int(
            retriever.vectors.shape[1]
        ),
    }


@app.post(
    "/retrieve",
    response_model=RetrievalResult,
)
async def retrieve(
    request: RetrievalRequest,
    x_aegis_rag_key: str | None = Header(
        default=None
    ),
):
    authorize(x_aegis_rag_key)
    hits = retriever.retrieve(
        query=request.query,
        top_k=request.top_k,
    )
    return RetrievalResult(
        query=request.query,
        model=MODEL_NAME,
        retrieval_mode="dense",
        hits=hits,
    )


@app.post(
    "/embed",
    response_model=EmbeddingResult,
)
async def embed(
    request: EmbeddingRequest,
    x_aegis_rag_key: str | None = Header(
        default=None
    ),
):
    authorize(x_aegis_rag_key)
    vectors = retriever.embed(
        request.texts
    )
    return EmbeddingResult(
        model=MODEL_NAME,
        dimension=len(vectors[0]),
        embeddings=vectors,
    )

