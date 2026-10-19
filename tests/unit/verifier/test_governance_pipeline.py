import json
import pytest
from pathlib import Path
from unittest.mock import AsyncMock, MagicMock

from aegis_api.services.crypto import canonical_hash
from bundle_store import BundleStore, BundleIntegrityError
from governance_coordinator import GovernanceCoordinator, GovernanceCoordinatorError


@pytest.fixture
def tmp_governance_env(tmp_path):
    verification_root = tmp_path / "verification"
    staging_dir = verification_root / "staging"
    frozen_dir = tmp_path / "frozen_updates" / "verifier"
    ref_dir = verification_root / "reference"
    ref_dir.mkdir(parents=True, exist_ok=True)

    policy = {
        "policyVersion": "aegis-verifier-policy-v1",
        "minimumVerificationScore": 100,
        "productionAutoDeployment": False,
    }
    policy_file = ref_dir / "verifier-policy-v1.json"
    policy_file.write_text(json.dumps(policy, sort_keys=True))

    bundle_store = BundleStore(verification_root)

    gov_client = MagicMock()
    gov_client.record = AsyncMock(return_value={
        "success": True,
        "status": 0,
        "transactionHash": "0x1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff",
        "blockNumber": 100,
        "from": "0x108a9272627edc87425c0b0fa673395548b68263",
        "to": "0x40fce2f6cbef3daa8e671549102d386f75a058ba",
    })
    gov_client.get_decision = AsyncMock(return_value={"exists": False})

    coordinator = GovernanceCoordinator(
        bundle_store=bundle_store,
        governance_client=gov_client,
        staging_dir=staging_dir,
        frozen_dir=frozen_dir,
        database_url="postgresql://mock:mock@127.0.0.1:5433/mock",
    )
    # Mock database pool
    mock_pool = MagicMock()
    mock_pool.execute = AsyncMock()
    coordinator.get_pool = AsyncMock(return_value=mock_pool)

    return {
        "verification_root": verification_root,
        "staging_dir": staging_dir,
        "frozen_dir": frozen_dir,
        "policy_file": policy_file,
        "bundle_store": bundle_store,
        "gov_client": gov_client,
        "coordinator": coordinator,
    }


def create_sample_bundle(bundle_store, decision="VERIFIED", source_status="PROPOSED", candidate_id="cand_1"):
    report = {"schema_version": "v1", "score": 100 if decision == "VERIFIED" else 50, "final_decision": decision}
    attestation = {"schema_version": "v1", "decision": decision}
    report_hash = canonical_hash(report)
    attestation_hash = canonical_hash(attestation)
    candidate_hash = "0x" + "a" * 64

    return bundle_store.save_bundle(
        verification_id="verif_test_" + candidate_id,
        candidate_id=candidate_id,
        candidate_source_status=source_status,
        candidate_hash=candidate_hash,
        report_hash=report_hash,
        attestation_hash=attestation_hash,
        final_decision=decision,
        report=report,
        attestation=attestation,
    )


@pytest.mark.asyncio
async def test_verified_to_approved_mapping(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    coord = tmp_governance_env["coordinator"]
    client = tmp_governance_env["gov_client"]

    bundle = create_sample_bundle(store, decision="VERIFIED")
    result = await coord.govern(bundle["verification_id"])

    assert result["decision"] == "APPROVED"
    assert result["staging_status"] == "STAGED"
    client.record.assert_awaited_once()
    _, kwargs = client.record.call_args
    assert kwargs["decision_value"] == 1


@pytest.mark.asyncio
async def test_rejected_to_rejected_mapping(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    coord = tmp_governance_env["coordinator"]
    client = tmp_governance_env["gov_client"]

    bundle = create_sample_bundle(store, decision="REJECTED")
    result = await coord.govern(bundle["verification_id"])

    assert result["decision"] == "REJECTED"
    assert result["staging_status"] == "FROZEN"
    client.record.assert_awaited_once()
    _, kwargs = client.record.call_args
    assert kwargs["decision_value"] == 2


@pytest.mark.asyncio
async def test_frozen_source_refused(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    coord = tmp_governance_env["coordinator"]
    client = tmp_governance_env["gov_client"]

    bundle = create_sample_bundle(store, decision="REJECTED", source_status="FROZEN")
    with pytest.raises(GovernanceCoordinatorError, match="SOURCE_CANDIDATE_NOT_GOVERNABLE"):
        await coord.govern(bundle["verification_id"])

    client.record.assert_not_called()


@pytest.mark.asyncio
async def test_decision_cannot_be_caller_supplied(tmp_governance_env):
    coord = tmp_governance_env["coordinator"]
    import inspect
    sig = inspect.signature(coord.govern)
    # The signature must only accept verification_id (and self)
    params = list(sig.parameters.keys())
    assert params == ["verification_id"]
    assert "decision" not in params
    assert "decision_value" not in params


@pytest.mark.asyncio
async def test_bundle_tamper_rejected(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    coord = tmp_governance_env["coordinator"]
    client = tmp_governance_env["gov_client"]

    bundle = create_sample_bundle(store, decision="VERIFIED")
    bundle_file = store.bundles_dir / f"{bundle['verification_id']}.json"
    content = json.loads(bundle_file.read_text())
    # Tamper report_hash without changing bundle_hash
    content["report_hash"] = "0x" + "f" * 64
    bundle_file.write_text(json.dumps(content))

    with pytest.raises(BundleIntegrityError, match="GOVERNANCE_BUNDLE_INTEGRITY_FAILURE"):
        await coord.govern(bundle["verification_id"])

    client.record.assert_not_called()


@pytest.mark.asyncio
async def test_staging_metadata_never_sets_production_authorization(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    coord = tmp_governance_env["coordinator"]
    staging_dir = tmp_governance_env["staging_dir"]

    bundle = create_sample_bundle(store, decision="VERIFIED", candidate_id="safe_cand")
    await coord.govern(bundle["verification_id"])

    staging_file = staging_dir / f"{bundle['candidate_hash']}.json"
    assert staging_file.exists()
    meta = json.loads(staging_file.read_text())

    assert meta["staging_authorized"] is True
    assert meta["production_deployment_authorized"] is False
    assert meta["governance_decision"] == "APPROVED"


@pytest.mark.asyncio
async def test_rejected_candidate_goes_frozen(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    coord = tmp_governance_env["coordinator"]
    frozen_dir = tmp_governance_env["frozen_dir"]

    bundle = create_sample_bundle(store, decision="REJECTED", candidate_id="bad_cand")
    await coord.govern(bundle["verification_id"])

    frozen_file = frozen_dir / f"{bundle['candidate_hash']}.json"
    assert frozen_file.exists()
    meta = json.loads(frozen_file.read_text())

    assert meta["staging_authorized"] is False
    assert meta["production_deployment_authorized"] is False
    assert meta["governance_decision"] == "REJECTED"


def test_verifier_version_hash_is_actual_policy_document_hash(tmp_governance_env):
    store = tmp_governance_env["bundle_store"]
    policy_file = tmp_governance_env["policy_file"]

    expected_hash = canonical_hash(json.loads(policy_file.read_text()))
    computed_hash = store.compute_policy_hash()

    assert computed_hash == expected_hash
    assert computed_hash.startswith("0x")
    assert len(computed_hash) == 66
