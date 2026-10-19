import asyncio
import os
import secrets

from contextlib import (
    asynccontextmanager,
)

from fastapi import (
    FastAPI,
    Header,
    HTTPException,
)

from models import (
    OracleHealth,
)

from policy import (
    EmergencyPolicy,
)

from probe import (
    FiscoNodeProbe,
)

from state_machine import (
    OracleStateMachine,
)


ORACLE_KEY = os.environ[
    "AEGIS_ORACLE_KEY"
]

POLL_SECONDS = float(
    os.getenv(
        "AEGIS_ORACLE_POLL_SECONDS",
        "2",
    )
)

RECOVERY_WINDOWS = int(
    os.getenv(
        "AEGIS_ORACLE_RECOVERY_WINDOWS",
        "3",
    )
)


CERT = os.environ[
    "AEGIS_FISCO_SDK_CERT"
]

KEY = os.environ[
    "AEGIS_FISCO_SDK_KEY"
]


probe = FiscoNodeProbe(
    cert=CERT,
    key=KEY,
)


machine = OracleStateMachine(
    policy=EmergencyPolicy(),

    recovery_windows=(
        RECOVERY_WINDOWS
    ),
)


async def monitor_loop():

    while True:

        observations = (
            await probe
            .observe_all()
        )

        machine.update(
            observations
        )

        await asyncio.sleep(
            POLL_SECONDS
        )


@asynccontextmanager
async def lifespan(app):

    task = asyncio.create_task(
        monitor_loop()
    )

    try:
        yield

    finally:
        task.cancel()


app = FastAPI(
    title="Aegis Emergency Oracle",
    lifespan=lifespan,
)


def authorize(
    supplied: str | None,
):

    if (
        supplied is None
        or not secrets.compare_digest(
            supplied,
            ORACLE_KEY,
        )
    ):

        raise HTTPException(
            status_code=401,
            detail="UNAUTHORIZED",
        )


@app.get(
    "/health",
    response_model=OracleHealth,
)
async def health():

    snapshot = (
        machine.last_snapshot
    )

    if snapshot is None:

        return OracleHealth(
            status="initializing",

            state=(
                "EMERGENCY_STOP"
            ),

            sequence=0,
        )

    return OracleHealth(
        status="ok",

        state=snapshot.state,

        sequence=(
            snapshot.sequence
        ),
    )


@app.get("/oracle/state")
async def state(
    x_aegis_oracle_key: str | None = Header(default=None),
):

    authorize(
        x_aegis_oracle_key
    )

    snapshot = (
        machine.last_snapshot
    )

    if snapshot is None:

        raise HTTPException(
            status_code=503,

            detail={
                "error":
                    "ORACLE_INITIALIZING"
            },
        )

    return snapshot
