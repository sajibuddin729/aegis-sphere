from fastapi import (
    APIRouter,
    HTTPException,
)

from aegis_api.clients.bridge import (
    BridgeError,
)

from aegis_api.core.container import (
    get_action_provider,
    get_bridge,
    get_wallet_provider,
)

from aegis_api.domain.wallet import (
    TransferDecision,
    TransferRequest,
)


router = APIRouter(
    prefix="/api/v1"
)


@router.get("/health")
async def health():
    bridge = get_bridge()

    try:
        chain = await bridge.health()

    except BridgeError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "status": "degraded",
                "bridge": str(exc),
            },
        ) from exc

    return {
        "status": "ok",
        "service": "aegis-api",
        "blockchain": chain,
    }


@router.get("/wallet")
async def wallet():
    try:
        return (
            await get_wallet_provider()
                .get_state()
        )

    except BridgeError as exc:
        raise HTTPException(
            status_code=502,
            detail=exc.payload
            or {"message": str(exc)},
        ) from exc


@router.get(
    "/transactions/{tx_hash}"
)
async def transaction(
    tx_hash: str,
):
    try:
        return await get_bridge().transaction(
            tx_hash
        )

    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code or 502
            ),
            detail=exc.payload
            or {"message": str(exc)},
        ) from exc


@router.get("/poi/{poi_hash}")
async def poi(
    poi_hash: str,
):
    try:
        return await get_bridge().poi(
            poi_hash
        )

    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code or 502
            ),
            detail=exc.payload
            or {"message": str(exc)},
        ) from exc


@router.post(
    "/actions/transfer",
    response_model=TransferDecision,
)
async def transfer(
    request: TransferRequest,
):
    try:
        decision = (
            await get_action_provider()
                .transfer_asset(
                    request,
                    leader_agent_id=(
                        "manual-api-agent"
                    ),
                    model_version=(
                        "deterministic-v1"
                    ),
                )
        )

        if decision.decision == "REJECT":
            raise HTTPException(
                status_code=422,
                detail=decision.model_dump(),
            )

        return decision

    except HTTPException:
        raise

    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code or 502
            ),
            detail=exc.payload
            or {"message": str(exc)},
        ) from exc
