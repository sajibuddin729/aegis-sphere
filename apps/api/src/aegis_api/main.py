from fastapi import FastAPI

from aegis_api.api.routes import (
    router,
)


app = FastAPI(
    title="Aegis-Sphere API",
    version="0.1.0",
    description=(
        "Application API for the "
        "Aegis-Sphere autonomous "
        "on-chain financial agent wallet."
    ),
)

app.include_router(router)


@app.get("/")
async def root():
    return {
        "service": "Aegis-Sphere",
        "status": "running",
    }
