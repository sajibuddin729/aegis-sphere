import pytest
from pathlib import Path

from aegis_api.domain.candidate_update import (
    CandidateRiskAssessment,
    CandidateUpdateProposalV2,
    CandidateVerificationTest,
)
from aegis_api.services.candidate_policy import (
    CandidateUpdatePolicy,
)
from aegis_api.services.candidate_schema_verifier import (
    CandidateSchemaVerifier,
)
from aegis_api.services.crypto import canonical_hash
from bundle_store import BundleStore, BundleIntegrityError
from governance_coordinator import GovernanceCoordinator, GovernanceCoordinatorError
from unittest.mock import AsyncMock, MagicMock


def sample_valid_v2_proposal():
    return CandidateUpdateProposalV2(
        schema_version="aegis-candidate-v2",
        update_type="POLICY",
        target_component="intent_validation",
        title="Valid Intent Replay Hardening",
        problem="Need hardening against replay attacks",
        proposed_change="Reject duplicate intentIds",
        expected_benefit="Prevent replay attacks",
        risks=[
            CandidateRiskAssessment(
                risk_id="RISK-01",
                description="Potential false positive",
                severity="LOW",
                mitigation="Whitelisting",
            )
        ],
        verification_tests=[
            CandidateVerificationTest(
                test_id="TEST-01",
                description="Test replay",
                expected_result="Rejection",
            ),
            CandidateVerificationTest(
                test_id="TEST-02",
                description="Test valid intent",
                expected_result="Acceptance",
            ),
        ],
        rollback_plan="Revert policy to previous version",
        security_invariants=["INV-01", "INV-02", "INV-03"],
        confidence=0.95,
        deployment_authorized=False,
    )


def test_candidate_v2_requires_risk_assessment():
    schema = CandidateSchemaVerifier()
    payload = {
        "proposal": {
            "schema_version": "aegis-candidate-v2",
            "update_type": "POLICY",
            "target_component": "intent_validation",
            "title": "Title",
            "problem": "Problem",
            "proposed_change": "Change",
            "expected_benefit": "Benefit",
            "risks": [],
            "verification_tests": ["Test 1", "Test 2"],
            "rollback_plan": "Revert",
            "security_invariants": ["INV-01", "INV-02", "INV-03"],
        }
    }
    with pytest.raises(ValueError, match="RISK_ASSESSMENT_REQUIRED"):
        schema.verify(payload)


def test_candidate_v2_requires_rollback_plan():
    schema = CandidateSchemaVerifier()
    payload = {
        "proposal": {
            "schema_version": "aegis-candidate-v2",
            "update_type": "POLICY",
            "target_component": "intent_validation",
            "title": "Title",
            "problem": "Problem",
            "proposed_change": "Change",
            "expected_benefit": "Benefit",
            "risks": [{"risk_id": "R1", "description": "D", "severity": "LOW", "mitigation": "M"}],
            "verification_tests": ["Test 1", "Test 2"],
            "rollback_plan": "",
            "security_invariants": ["INV-01", "INV-02", "INV-03"],
        }
    }
    with pytest.raises(ValueError, match="ROLLBACK_PLAN_REQUIRED"):
        schema.verify(payload)


def test_candidate_v2_requires_invariant_ids():
    schema = CandidateSchemaVerifier()
    payload = {
        "proposal": {
            "schema_version": "aegis-candidate-v2",
            "update_type": "POLICY",
            "target_component": "intent_validation",
            "title": "Title",
            "problem": "Problem",
            "proposed_change": "Change",
            "expected_benefit": "Benefit",
            "risks": [{"risk_id": "R1", "description": "D", "severity": "LOW", "mitigation": "M"}],
            "verification_tests": ["Test 1", "Test 2"],
            "rollback_plan": "Revert immediately",
            "security_invariants": ["INV-01", "INV-02"],
        }
    }
    with pytest.raises(ValueError, match="SECURITY_INVARIANTS_REQUIRED"):
        schema.verify(payload)


def test_code_candidate_requires_stronger_sandbox():
    schema = CandidateSchemaVerifier()
    payload = {
        "proposal": {
            "schema_version": "aegis-candidate-v2",
            "update_type": "CODE",
            "target_component": "solidity_contract",
            "title": "Code Update",
            "problem": "Problem",
            "proposed_change": "function execute() {}",
            "expected_benefit": "Benefit",
            "risks": [{"risk_id": "R1", "description": "D", "severity": "LOW", "mitigation": "M"}],
            "verification_tests": ["Test 1", "Test 2"],
            "rollback_plan": "Revert",
            "security_invariants": ["INV-01", "INV-02", "INV-03"],
        }
    }
    with pytest.raises(ValueError, match="CODE_SANDBOX_REQUIRED"):
        schema.verify(payload)


def test_unsafe_candidate_frozen():
    policy = CandidateUpdatePolicy()
    proposal = sample_valid_v2_proposal()
    proposal.proposed_change = "Bypass audit and disable risk controls for latency"

    result = policy.evaluate(proposal)
    assert result.status == "FROZEN"
    assert result.reason == "PROHIBITED_AUTONOMY_OR_SECURITY_CHANGE"


def setup_tmp_store(tmp_path):
    ref_dir = tmp_path / "reference"
    ref_dir.mkdir(parents=True, exist_ok=True)
    (ref_dir / "verifier-policy-v1.json").write_text('{"policyVersion": "v1"}')
    return BundleStore(tmp_path)


def test_rejected_candidate_immutable(tmp_path):
    store = setup_tmp_store(tmp_path)
    report = {"schema_version": "v1", "score": 0, "final_decision": "REJECTED"}
    attestation = {"schema_version": "v1", "decision": "REJECTED"}

    bundle = store.save_bundle(
        verification_id="verif_rej_01",
        candidate_id="cand_bad",
        candidate_source_status="PROPOSED",
        candidate_hash="0x" + "b" * 64,
        report_hash=canonical_hash(report),
        attestation_hash=canonical_hash(attestation),
        final_decision="REJECTED",
        report=report,
        attestation=attestation,
    )

    loaded = store.get_bundle("verif_rej_01")
    assert loaded["final_decision"] == "REJECTED"
    assert loaded["bundle_hash"] == bundle["bundle_hash"]


def test_rejected_candidate_cannot_be_rewritten(tmp_path):
    store = setup_tmp_store(tmp_path)
    report = {"schema_version": "v1", "score": 0, "final_decision": "REJECTED"}
    attestation = {"schema_version": "v1", "decision": "REJECTED"}

    bundle = store.save_bundle(
        verification_id="verif_rej_02",
        candidate_id="cand_bad2",
        candidate_source_status="PROPOSED",
        candidate_hash="0x" + "c" * 64,
        report_hash=canonical_hash(report),
        attestation_hash=canonical_hash(attestation),
        final_decision="REJECTED",
        report=report,
        attestation=attestation,
    )

    # Attempt to tamper with the bundle file
    bundle_file = store.bundles_dir / f"{bundle['verification_id']}.json"
    import json
    data = json.loads(bundle_file.read_text())
    data["final_decision"] = "VERIFIED"
    bundle_file.write_text(json.dumps(data))

    tampered = store.get_bundle(bundle["verification_id"])
    with pytest.raises(BundleIntegrityError, match="GOVERNANCE_BUNDLE_INTEGRITY_FAILURE"):
        store.verify_integrity(tampered)


@pytest.mark.asyncio
async def test_verified_candidate_governance_decision_derives_automatically(tmp_path):
    store = setup_tmp_store(tmp_path)
    staging_dir = tmp_path / "staging"
    frozen_dir = tmp_path / "frozen"

    report = {"schema_version": "v1", "score": 100, "final_decision": "VERIFIED"}
    attestation = {"schema_version": "v1", "decision": "VERIFIED"}
    candidate_hash = "0x" + "d" * 64

    bundle = store.save_bundle(
        verification_id="verif_auto_01",
        candidate_id="cand_auto",
        candidate_source_status="PROPOSED",
        candidate_hash=candidate_hash,
        report_hash=canonical_hash(report),
        attestation_hash=canonical_hash(attestation),
        final_decision="VERIFIED",
        report=report,
        attestation=attestation,
    )

    client = MagicMock()
    client.record = AsyncMock(return_value={
        "success": True,
        "status": 0,
        "transactionHash": "0x" + "e" * 64,
        "blockNumber": 200,
        "from": "0x108a9272627edc87425c0b0fa673395548b68263",
        "to": "0x40fce2f6cbef3daa8e671549102d386f75a058ba",
    })
    client.get_decision = AsyncMock(return_value={"exists": False})

    coordinator = GovernanceCoordinator(
        bundle_store=store,
        governance_client=client,
        staging_dir=staging_dir,
        frozen_dir=frozen_dir,
        database_url="postgresql://mock:mock@127.0.0.1:5433/mock",
    )
    mock_pool = MagicMock()
    mock_pool.execute = AsyncMock()
    coordinator.get_pool = AsyncMock(return_value=mock_pool)

    # Call govern without passing any decision parameter
    res = await coordinator.govern(bundle["verification_id"])

    assert res["decision"] == "APPROVED"
    assert res["staging_status"] == "STAGED"
    client.record.assert_awaited_once()
    _, kwargs = client.record.call_args
    assert kwargs["decision_value"] == 1
    assert (staging_dir / f"{candidate_hash}.json").exists()
