import hmac
import os
from pathlib import Path
from typing import Any

from fastapi import FastAPI, Header, HTTPException, Request, Response, status
from fastapi.responses import JSONResponse

from aegis_api.services.candidate_intake import CandidateIntakeService, CandidateIntakeError
from aegis_api.services.candidate_schema_verifier import CandidateSchemaVerifier
from aegis_api.services.candidate_static_verifier import CandidateStaticVerifier
from aegis_api.services.candidate_code_guard import CandidateCodeGuard
from aegis_api.services.candidate_behavior_verifier import CandidateBehaviorVerifier
from aegis_api.services.candidate_regression_verifier import CandidateRegressionVerifier
from aegis_api.services.verification_report import VerificationReportStore
from aegis_api.services.verifier_attestation import VerifierAttestationService
from aegis_api.services.candidate_verifier import CandidateVerifier

from bundle_store import BundleStore, BundleIntegrityError
from governance_client import GovernanceClient, GovernanceUnavailable
from governance_coordinator import GovernanceCoordinator, GovernanceCoordinatorError

app = FastAPI(title="Aegis Independent Verifier API", version="1.0.0")

PROJECT_ROOT = Path(__file__).resolve().parents[3]
VERIFICATION_ROOT = Path(
    os.getenv("AEGIS_VERIFICATION_ROOT", str(PROJECT_ROOT / "sandbox" / "verification"))
)
STAGING_DIR = VERIFICATION_ROOT / "staging"
FROZEN_DIR = PROJECT_ROOT / "sandbox" / "frozen_updates" / "verifier"

DB_URL = os.getenv("AEGIS_VERIFIER_DATABASE_URL", "postgresql://aegis:aegis_local_dev_password@127.0.0.1:5433/aegis_memory")
GOV_BRIDGE_URL = os.getenv("AEGIS_GOVERNANCE_BRIDGE_URL", "http://127.0.0.1:9090")
GOV_BRIDGE_KEY = os.getenv("AEGIS_GOVERNANCE_BRIDGE_KEY", "")
VERIFIER_KEY = os.getenv("AEGIS_VERIFIER_KEY", "")

# Initialize components
intake_service = CandidateIntakeService(
    candidate_dirs=[
        PROJECT_ROOT / "sandbox" / "candidate_updates" / "university-v2",
        PROJECT_ROOT / "sandbox" / "candidate_updates" / "university",
        PROJECT_ROOT / "sandbox" / "frozen_updates" / "university",
        VERIFICATION_ROOT / "reference",
    ]
)

verifier_service = CandidateVerifier(
    intake=intake_service,
    schema=CandidateSchemaVerifier(),
    static=CandidateStaticVerifier(),
    code_guard=CandidateCodeGuard(),
    behavior=CandidateBehaviorVerifier(),
    regression=CandidateRegressionVerifier(),
    report_store=VerificationReportStore(VERIFICATION_ROOT / "reports"),
    attestation=VerifierAttestationService(),
    project_root=PROJECT_ROOT,
)

bundle_store = BundleStore(VERIFICATION_ROOT)
gov_client = GovernanceClient(base_url=GOV_BRIDGE_URL, key=GOV_BRIDGE_KEY)
coordinator = GovernanceCoordinator(
    bundle_store=bundle_store,
    governance_client=gov_client,
    staging_dir=STAGING_DIR,
    frozen_dir=FROZEN_DIR,
    database_url=DB_URL,
)


def verify_auth(x_aegis_verifier_key: str | None = Header(None, alias="X-Aegis-Verifier-Key")) -> None:
    if not x_aegis_verifier_key or not VERIFIER_KEY:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="UNAUTHORIZED")
    if not hmac.compare_digest(x_aegis_verifier_key, VERIFIER_KEY):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="UNAUTHORIZED")


@app.get("/health")
async def health() -> dict[str, Any]:
    return {
        "status": "ok",
        "service": "aegis-independent-verifier",
        "port": 8090,
    }


@app.post("/verify/{candidate_id}")
async def verify_candidate(
    candidate_id: str,
    request: Request,
) -> dict[str, Any]:
    verify_auth(request.headers.get("X-Aegis-Verifier-Key"))

    try:
        payload, candidate_hash = intake_service.load(candidate_id)
        source_status = payload.get("status", "UNKNOWN")
    except CandidateIntakeError as exc:
        raise HTTPException(status_code=422, detail=str(exc))

    result = await verifier_service.verify(candidate_id)
    report = result["report"]
    attestation = result["attestation"]

    report_dict = report.model_dump(mode="json")
    attestation_dict = attestation.model_dump(mode="json")

    # Persist bundle
    bundle = bundle_store.save_bundle(
        verification_id=report.verification_id,
        candidate_id=candidate_id,
        candidate_source_status=source_status,
        candidate_hash=candidate_hash,
        report_hash=result["report_hash"],
        attestation_hash=result["attestation_hash"],
        final_decision=report.final_decision,
        report=report_dict,
        attestation=attestation_dict,
    )

    return {
        "verification_id": report.verification_id,
        "candidate_id": candidate_id,
        "candidate_hash": candidate_hash,
        "candidateHash": candidate_hash,
        "candidate_source_status": source_status,
        "final_decision": report.final_decision,
        "decision": report.final_decision,
        "score": report.verification_score,
        "verification_score": report.verification_score,
        "report_hash": result["report_hash"],
        "attestation_hash": result["attestation_hash"],
        "bundle_hash": bundle["bundle_hash"],
        "report": report_dict,
        "attestation": attestation_dict,
    }


@app.post("/govern/{verification_id}")
async def govern_candidate(
    verification_id: str,
    request: Request,
) -> dict[str, Any]:
    verify_auth(request.headers.get("X-Aegis-Verifier-Key"))

    try:
        gov_result = await coordinator.govern(verification_id)
        return gov_result
    except GovernanceUnavailable as exc:
        raise HTTPException(status_code=503, detail="GOVERNANCE_SIGNER_UNAVAILABLE")
    except BundleIntegrityError as exc:
        raise HTTPException(status_code=422, detail="GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")
    except GovernanceCoordinatorError as exc:
        err_msg = str(exc)
        if "SOURCE_CANDIDATE_NOT_GOVERNABLE" in err_msg:
            raise HTTPException(status_code=422, detail="SOURCE_CANDIDATE_NOT_GOVERNABLE")
        if "UPDATE_ALREADY_DECIDED" in err_msg:
            raise HTTPException(status_code=422, detail="AEGIS: UPDATE_ALREADY_DECIDED")
        if "GOVERNANCE_BUNDLE_INTEGRITY_FAILURE" in err_msg:
            raise HTTPException(status_code=422, detail="GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")
        raise HTTPException(status_code=422, detail=err_msg)
    except FileNotFoundError as exc:
        raise HTTPException(status_code=404, detail="VERIFICATION_BUNDLE_NOT_FOUND")
    except Exception as exc:
        err_msg = str(exc)
        if "UPDATE_ALREADY_DECIDED" in err_msg:
            raise HTTPException(status_code=422, detail="AEGIS: UPDATE_ALREADY_DECIDED")
        raise HTTPException(status_code=500, detail=f"INTERNAL_GOVERNANCE_ERROR: {err_msg}")


@app.get("/bundles/{verification_id}")
async def get_bundle(
    verification_id: str,
    request: Request,
) -> dict[str, Any]:
    verify_auth(request.headers.get("X-Aegis-Verifier-Key"))
    try:
        return bundle_store.get_bundle(verification_id)
    except FileNotFoundError:
        raise HTTPException(status_code=404, detail="BUNDLE_NOT_FOUND")
