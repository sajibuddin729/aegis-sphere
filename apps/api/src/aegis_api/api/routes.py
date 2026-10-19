from fastapi import (
    APIRouter,
    HTTPException,
    status,
)
from pydantic import BaseModel

from aegis_api.clients.bridge import (
    BridgeError,
)
from aegis_api.clients.guard import (
    GuardUnavailable,
)
from aegis_api.clients.llm import (
    LLMError,
)
from aegis_api.clients.rag import (
    RagUnavailable,
)
from aegis_api.services.regulatory_compliance import (
    RegulatoryEvidenceUnavailable,
)
from aegis_api.services.audit_anchor import (
    AuditAnchorError,
)
from aegis_api.clients.embedding import (
    EmbeddingUnavailable,
)
from aegis_api.domain.university import (
    UniversityEpisodeResult,
    UniversityRunRequest,
)
from aegis_api.domain.arena import (
    ArenaLeagueResult,
    ArenaMatchResult,
)
from aegis_api.domain.agent import (
    AutonomousAgentResult,
    AutonomousRequest,
)
from aegis_api.domain.portfolio import (
    AutonomousPortfolioResult,
)
from aegis_api.domain.curriculum import (
    CurriculumRunRequest,
    CurriculumRunResult,
)
from aegis_api.core.container import (
    get_action_provider,
    get_bridge,
    get_orchestrator,
    get_portfolio_orchestrator,
    get_portfolio_provider,
    get_regulatory_compliance,
    get_university_orchestrator,
    get_wallet_provider,
    get_arena_league,
    get_arena_orchestrator,
    get_arena_repository,
    get_curriculum_engine,
    get_curriculum_repository,
    get_candidate_verifier,
    get_evolution_agent,
)
from aegis_api.services.candidate_intake import (
    CandidateIntakeError,
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


@router.get(
    "/compliance/evidence/{evidence_hash}"
)
async def compliance_evidence(
    evidence_hash: str,
):
    try:
        return (
            get_regulatory_compliance()
            .load_evidence(
                evidence_hash
            )
        )
    except FileNotFoundError as exc:
        raise HTTPException(
            status_code=404,
            detail={
                "error":
                    "EVIDENCE_NOT_FOUND"
            },
        ) from exc
    except RuntimeError as exc:
        raise HTTPException(
            status_code=409,
            detail={
                "error":
                    "EVIDENCE_INTEGRITY_FAILURE"
            },
        ) from exc


@router.get(
    "/audit/evidence/{evidence_hash}"
)
async def audit_evidence(
    evidence_hash: str,
):
    try:
        return await get_bridge() \
            .audit_evidence(
                evidence_hash
            )
    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code
                or 502
            ),
            detail=(
                exc.payload
                or {
                    "message":
                        str(exc)
                }
            ),
        ) from exc


@router.post(
    "/agent/execute",
    response_model=AutonomousAgentResult,
)
async def agent_execute(
    request: AutonomousRequest,
):
    try:
        return await get_orchestrator().execute(
            request.instruction
        )
    except (
        RagUnavailable,
        RegulatoryEvidenceUnavailable,
    ) as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "REGULATORY_EVIDENCE_UNAVAILABLE",
                "message":
                    "Regulatory evidence "
                    "verification is unavailable. "
                    "Financial execution is disabled.",
            },
        ) from exc
    except AuditAnchorError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "AUDIT_ANCHOR_FAILED",
                "message":
                    "Compliance evidence "
                    "could not be anchored. "
                    "Financial execution is disabled.",
            },
        ) from exc
    except GuardUnavailable as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error": "SAFETY_GUARD_UNAVAILABLE",
                "message": (
                    "Independent safety verification is unavailable. "
                    "Financial execution is disabled."
                ),
            },
        ) from exc
    except LLMError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error": "AI_UNAVAILABLE",
                "message": (
                    "Local AI service is unavailable. "
                    "Financial execution is disabled."
                ),
            },
        ) from exc
    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code or 502
            ),
            detail=exc.payload
            or {"message": str(exc)},
        ) from exc


@router.get("/portfolio")
async def portfolio_state():
    try:
        return (
            await get_portfolio_provider()
            .get_state()
        )
    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code or 502
            ),
            detail=(
                exc.payload
                or {
                    "message":
                        str(exc)
                }
            ),
        ) from exc


@router.post(
    "/portfolio/agent/execute",
    response_model=(
        AutonomousPortfolioResult
    ),
)
async def portfolio_agent_execute(
    request: AutonomousRequest,
):
    try:
        return (
            await get_portfolio_orchestrator()
            .execute(
                request.instruction
            )
        )
    except (
        RagUnavailable,
        RegulatoryEvidenceUnavailable,
    ) as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "REGULATORY_EVIDENCE_UNAVAILABLE",
                "message":
                    "Regulatory evidence "
                    "verification is unavailable. "
                    "Financial execution is disabled.",
            },
        ) from exc
    except AuditAnchorError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "AUDIT_ANCHOR_FAILED",
                "message":
                    "Compliance evidence "
                    "could not be anchored. "
                    "Financial execution is disabled.",
            },
        ) from exc
    except GuardUnavailable as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error": "SAFETY_GUARD_UNAVAILABLE",
                "message": (
                    "Independent safety verification is unavailable. "
                    "Financial execution is disabled."
                ),
            },
        ) from exc
    except LLMError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "AI_UNAVAILABLE",
                "message":
                    "Local AI service is "
                    "unavailable. Portfolio "
                    "execution is disabled.",
            },
        ) from exc
    except BridgeError as exc:
        raise HTTPException(
            status_code=(
                exc.status_code or 502
            ),
            detail=exc.payload
            or {"message": str(exc)},
        ) from exc


@router.post(
    "/university/run",
    response_model=UniversityEpisodeResult,
)
async def university_run(
    request: UniversityRunRequest,
):
    try:
        return (
            await get_university_orchestrator()
            .run(
                request.scenario_id
            )
        )
    except KeyError as exc:
        raise HTTPException(
            status_code=404,
            detail={
                "error": "SCENARIO_NOT_FOUND"
            },
        ) from exc
    except (
        LLMError,
        EmbeddingUnavailable,
    ) as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "UNIVERSITY_DEPENDENCY_UNAVAILABLE",
                "message":
                    str(exc),
            },
        ) from exc


@router.post(
    "/university/arena/league/run",
    response_model=ArenaLeagueResult,
)
async def arena_league():
    return await get_arena_league().run_all()


@router.get(
    "/university/arena/ratings",
)
async def arena_ratings():
    return {
        "ratings":
            await get_arena_repository()
            .ratings()
    }


@router.post(
    "/university/arena/{scenario_id}",
    response_model=ArenaMatchResult,
)
async def arena_run(
    scenario_id: str,
):
    try:
        return (
            await get_arena_orchestrator()
            .run(
                scenario_id
            )
        )
    except KeyError as exc:
        raise HTTPException(
            status_code=404,
            detail={
                "error":
                    "ARENA_SCENARIO_NOT_FOUND"
            },
        ) from exc
    except LLMError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error":
                    "UNIVERSITY_DEPENDENCY_UNAVAILABLE",
                "message":
                    str(exc),
            },
        ) from exc


@router.post(
    "/university/curriculum/run",
    response_model=CurriculumRunResult,
)
async def curriculum_run(
    request: CurriculumRunRequest,
):
    try:
        return await get_curriculum_engine().run(
            steps=request.steps
        )
    except LLMError as exc:
        raise HTTPException(
            status_code=503,
            detail={
                "error": "CURRICULUM_AI_UNAVAILABLE"
            },
        ) from exc


@router.get(
    "/university/curriculum/capabilities"
)
async def curriculum_capabilities():
    repository = get_curriculum_repository()
    stats = await repository.capability_stats()
    from aegis_api.services.curriculum_scheduler import (
        CurriculumScheduler,
    )
    ranked = CurriculumScheduler().rank(stats)
    return {
        "capabilities": [
            value.model_dump()
            for value
            in ranked
        ]
    }


@router.post(
    "/verification/candidates/{candidate_id}"
)
async def verify_candidate(
    candidate_id: str,
):
    raise HTTPException(
        status_code=status.HTTP_410_GONE,
        detail="VERIFICATION_MOVED_TO_ISOLATED_SERVICE",
    )


class EvolutionGenerateRequest(BaseModel):
    finding_id: str | None = None
    finding: dict | None = None


@router.post(
    "/university/evolution/generate"
)
async def generate_evolution_candidate(
    request: EvolutionGenerateRequest,
):
    import json
    import uuid
    from pathlib import Path
    from aegis_api.services.candidate_policy import CandidateUpdatePolicy
    from aegis_api.services.crypto import canonical_hash

    finding_id = request.finding_id
    finding_data = request.finding

    if not finding_data:
        if finding_id == "FINDING-INTENT-REPLAY-001":
            finding_data = {
                "finding_id": "FINDING-INTENT-REPLAY-001",
                "source": "curriculum",
                "description": "The current system has demonstrated duplicate PoI hash rejection but has not yet demonstrated on-chain rejection of a reused intentId with a different PoI hash.",
                "severity": "HIGH",
                "required_invariants": [
                    "INV-01", "INV-02", "INV-03", "INV-04", "INV-05", "INV-06", "INV-07", "INV-08", "INV-09", "INV-10"
                ]
            }
        else:
            finding_data = {
                "finding_id": finding_id or "FINDING-GENERAL-001",
                "source": "curriculum",
                "description": "General system hardening recommendation.",
                "severity": "MEDIUM",
                "required_invariants": [
                    "INV-01", "INV-02", "INV-03", "INV-04", "INV-05", "INV-06", "INV-07", "INV-08", "INV-09", "INV-10"
                ]
            }
    elif not finding_id:
        finding_id = finding_data.get("finding_id", "FINDING-CUSTOM-001")

    agent = get_evolution_agent()
    proposal = await agent.propose_v2(finding=finding_data)

    policy = CandidateUpdatePolicy()
    policy_result = policy.evaluate(proposal)

    candidate_id = "candidate_ai_" + uuid.uuid4().hex[:16]
    proposal_dict = proposal.model_dump(mode="json")
    proposal_hash = canonical_hash(proposal_dict)

    doc_base = {
        "candidate_id": candidate_id,
        "generated_by": "AEGIS_EVOLUTION_AGENT",
        "source_finding_id": finding_id,
        "source_status": policy_result.status,
        "status": policy_result.status,
        "policy_result": policy_result.status,
        "frozen_reason": policy_result.reason,
        "proposal_hash": proposal_hash,
        "proposal": proposal_dict,
        "deployment_authorized": False,
    }
    candidate_hash = canonical_hash(doc_base)
    doc_base["candidate_hash"] = candidate_hash

    # Save to university-v2 directory
    out_dir = Path("sandbox/candidate_updates/university-v2")
    out_dir.mkdir(parents=True, exist_ok=True)
    (out_dir / f"{candidate_id}.json").write_text(
        json.dumps(doc_base, indent=2, sort_keys=True),
        encoding="utf-8"
    )

    return {
        "candidate_id": candidate_id,
        "generated_by": "AEGIS_EVOLUTION_AGENT",
        "source_finding_id": finding_id,
        "source_status": policy_result.status,
        "status": policy_result.status,
        "policy_result": policy_result.status,
        "frozen_reason": policy_result.reason,
        "proposal_hash": proposal_hash,
        "candidate_hash": candidate_hash,
        "candidateHash": candidate_hash,
        "deployment_authorized": False,
        "risks": proposal_dict.get("risks", []),
        "verification_tests": proposal_dict.get("verification_tests", []),
        "proposal": proposal_dict,
    }






