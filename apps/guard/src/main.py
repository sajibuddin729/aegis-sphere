import os
import secrets
from fastapi import (
    FastAPI,
    Header,
    HTTPException,
)
from models import (
    GuardRequest,
    GuardResult,
)
from guard_engine import (
    QwenGuardEngine,
)

GUARD_KEY = os.environ[
    "AEGIS_GUARD_KEY"
]

MODEL_NAME = os.getenv(
    "AEGIS_GUARD_MODEL",
    "Qwen/Qwen3Guard-Gen-0.6B",
)

app = FastAPI(
    title="Aegis Safety Guard",
    version="0.1.0",
)

engine = QwenGuardEngine(
    MODEL_NAME
)


def verify_key(
    provided: str | None,
):
    if (
        provided is None
        or not secrets.compare_digest(
            provided,
            GUARD_KEY,
        )
    ):
        raise HTTPException(
            status_code=401,
            detail="UNAUTHORIZED",
        )


@app.get("/health")
async def health():
    return {
        "status": "ok",
        "model": MODEL_NAME,
        "service": "aegis-safety-guard",
    }


@app.post(
    "/guard/check",
    response_model=GuardResult,
)
async def check(
    request: GuardRequest,
    x_aegis_guard_key: str | None = Header(
        default=None
    ),
):
    verify_key(
        x_aegis_guard_key
    )

    try:
        result = engine.classify(
            request.content
        )
    except Exception as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error": "GUARD_FAILURE",
                "message": str(exc),
            },
        ) from exc

    return GuardResult(
        **result
    )
