import json
from pathlib import Path
from typing import Any

from aegis_api.services.crypto import canonical_hash


class BundleIntegrityError(RuntimeError):
    pass


class BundleStore:

    def __init__(self, verification_root: Path):
        self.verification_root = Path(verification_root)
        self.bundles_dir = self.verification_root / "bundles"
        self.policy_file = self.verification_root / "reference" / "verifier-policy-v1.json"
        self.bundles_dir.mkdir(parents=True, exist_ok=True)

    def compute_policy_hash(self) -> str:
        if not self.policy_file.exists():
            raise FileNotFoundError(f"Verifier policy file not found at {self.policy_file}")
        policy = json.loads(self.policy_file.read_text(encoding="utf-8"))
        return canonical_hash(policy)

    def save_bundle(
        self,
        *,
        verification_id: str,
        candidate_id: str,
        candidate_source_status: str,
        candidate_hash: str,
        report_hash: str,
        attestation_hash: str,
        final_decision: str,
        report: dict[str, Any],
        attestation: dict[str, Any],
    ) -> dict[str, Any]:
        verifier_version_hash = self.compute_policy_hash()

        bundle_content = {
            "verification_id": verification_id,
            "candidate_id": candidate_id,
            "candidate_source_status": candidate_source_status,
            "candidate_hash": candidate_hash,
            "report_hash": report_hash,
            "attestation_hash": attestation_hash,
            "verifier_version_hash": verifier_version_hash,
            "final_decision": final_decision,
            "report": report,
            "attestation": attestation,
        }

        bundle_hash = canonical_hash(bundle_content)
        bundle_content["bundle_hash"] = bundle_hash

        bundle_path = self.bundles_dir / f"{verification_id}.json"
        bundle_path.write_text(
            json.dumps(bundle_content, indent=2, sort_keys=True, ensure_ascii=False),
            encoding="utf-8",
        )

        return bundle_content

    def get_bundle(self, verification_id: str) -> dict[str, Any]:
        bundle_path = self.bundles_dir / f"{verification_id}.json"
        if not bundle_path.exists():
            raise FileNotFoundError(f"Bundle not found: {verification_id}")
        return json.loads(bundle_path.read_text(encoding="utf-8"))

    def verify_integrity(self, bundle: dict[str, Any]) -> None:
        try:
            expected_bundle_hash = bundle.get("bundle_hash")
            if not expected_bundle_hash:
                raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")

            # Check 1: report_hash matches canonical_hash of report object
            report = bundle.get("report")
            if not isinstance(report, dict) or canonical_hash(report) != bundle.get("report_hash"):
                raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")

            # Check 2: attestation_hash matches canonical_hash of attestation object
            attestation = bundle.get("attestation")
            if not isinstance(attestation, dict) or canonical_hash(attestation) != bundle.get("attestation_hash"):
                raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")

            # Check 3: verifier_version_hash matches actual policy document canonical hash
            current_policy_hash = self.compute_policy_hash()
            if current_policy_hash != bundle.get("verifier_version_hash"):
                raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")

            # Check 4: candidate_hash presence and format
            cand_hash = bundle.get("candidate_hash")
            if not cand_hash or not isinstance(cand_hash, str) or not cand_hash.startswith("0x") or len(cand_hash) != 66:
                raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")

            # Check 5: bundle_hash recomputation
            bundle_content = {k: v for k, v in bundle.items() if k != "bundle_hash"}
            recomputed = canonical_hash(bundle_content)
            if recomputed != expected_bundle_hash:
                raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE")

        except BundleIntegrityError:
            raise
        except Exception as exc:
            raise BundleIntegrityError("GOVERNANCE_BUNDLE_INTEGRITY_FAILURE") from exc
