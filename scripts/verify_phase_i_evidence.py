#!/usr/bin/env python3
import hashlib
import json
import sys
from pathlib import Path

# Add src to sys.path
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "apps" / "api" / "src"))

from aegis_api.services.crypto import canonical_bytes, canonical_hash, user_request_hash


def verify():
    root = Path(__file__).resolve().parents[1]
    runtime_dir = root / "docs" / "evidence" / "phase-i" / "runtime"
    
    # 1. Verify User Request Hash
    rebalance_data = json.loads((runtime_dir / "final-rebalance.json").read_text())
    rebalance_poi = json.loads((runtime_dir / "final-rebalance-poi.json").read_text())
    rebalance_evidence = json.loads((runtime_dir / "final-rebalance-evidence.json").read_text())
    
    raw_instruction = rebalance_evidence["intent"]["rawInstruction"]
    expected_user_req_hash = "0x" + hashlib.sha256(raw_instruction.encode("utf-8")).hexdigest()
    assert rebalance_poi["userRequestHash"] == expected_user_req_hash, f"User request hash mismatch: {rebalance_poi['userRequestHash']} != {expected_user_req_hash}"
    print("USER_REQUEST_HASH=PASS")

    # 2. Verify Policy Hash
    policy_manifest_path = root / "data" / "policies" / "effective" / "aegis-effective-financial-policy-v1.json"
    manifest_bytes = policy_manifest_path.read_bytes()
    expected_policy_hash = "0x" + hashlib.sha256(manifest_bytes).hexdigest()
    assert rebalance_poi["policyHash"] == expected_policy_hash, f"Policy hash mismatch: {rebalance_poi['policyHash']} != {expected_policy_hash}"
    print("POLICY_HASH=PASS")

    # 3. Verify Evidence Hash
    computed_evidence_hash = canonical_hash(rebalance_evidence)
    assert rebalance_poi["evidenceHash"] == computed_evidence_hash, f"Evidence hash mismatch: {rebalance_poi['evidenceHash']} != {computed_evidence_hash}"
    assert rebalance_data["compliance_evidence_hash"] == computed_evidence_hash
    print("EVIDENCE_HASH=PASS")

    # 4. Verify PoI Hash
    computed_poi_hash = canonical_hash(rebalance_poi)
    assert rebalance_data["poi_hash"] == computed_poi_hash, f"PoI hash mismatch: {rebalance_data['poi_hash']} != {computed_poi_hash}"
    print("POI_HASH=PASS")

    # Also verify on-chain transfer
    transfer_data = json.loads((runtime_dir / "final-transfer.json").read_text())
    transfer_poi_onchain = json.loads((runtime_dir / "onchain-poi.json").read_text())
    assert transfer_data["poi_hash"] == transfer_poi_onchain["poiHash"]
    assert transfer_data["intent_id"] == transfer_poi_onchain["intentId"]
    assert transfer_data["policy_hash"] == transfer_poi_onchain["policyHash"]
    assert transfer_data["policy_hash"] == expected_policy_hash

    # Verify audit ordering
    audit_evidence = json.loads((runtime_dir / "onchain-audit-evidence.json").read_text())
    assert audit_evidence["relatedPoIHash"] == transfer_data["poi_hash"]
    assert int(audit_evidence["blockNumber"]) < int(transfer_data["block_number"])
    print("AUDIT_ORDERING=PASS (Audit Block < Financial Block)")
    print("ALL_PHASE_I_CRYPTOGRAPHIC_VERIFICATIONS=PASS")


if __name__ == "__main__":
    verify()
