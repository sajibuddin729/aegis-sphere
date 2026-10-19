import json
from pathlib import Path
from typing import Any
import asyncpg

from bundle_store import BundleStore, BundleIntegrityError
from governance_client import GovernanceClient, GovernanceUnavailable


class GovernanceCoordinatorError(RuntimeError):
    pass


class GovernanceCoordinator:

    def __init__(
        self,
        *,
        bundle_store: BundleStore,
        governance_client: GovernanceClient,
        staging_dir: Path,
        frozen_dir: Path,
        database_url: str,
    ):
        self.bundle_store = bundle_store
        self.governance_client = governance_client
        self.staging_dir = Path(staging_dir)
        self.frozen_dir = Path(frozen_dir)
        self.database_url = database_url
        self.pool: asyncpg.Pool | None = None

        self.staging_dir.mkdir(parents=True, exist_ok=True)
        self.frozen_dir.mkdir(parents=True, exist_ok=True)

    async def get_pool(self) -> asyncpg.Pool:
        if self.pool is None:
            self.pool = await asyncpg.create_pool(self.database_url, min_size=1, max_size=5)
        return self.pool

    async def govern(
        self,
        verification_id: str,
    ) -> dict[str, Any]:
        bundle = self.bundle_store.get_bundle(verification_id)

        # G076: Source-State Rule
        source_status = bundle.get("candidate_source_status")
        if source_status != "PROPOSED":
            raise GovernanceCoordinatorError("SOURCE_CANDIDATE_NOT_GOVERNABLE")

        # G073: Bundle Integrity Rules
        self.bundle_store.verify_integrity(bundle)

        # G075: Determine decision derived from verification, never caller-supplied
        final_decision = bundle.get("final_decision")
        if final_decision == "VERIFIED":
            decision_value = 1
            decision_str = "APPROVED"
        elif final_decision == "REJECTED":
            decision_value = 2
            decision_str = "REJECTED"
        else:
            raise GovernanceCoordinatorError(f"UNKNOWN_FINAL_DECISION: {final_decision}")

        # Submit to dedicated governance bridge on :9090
        record_res = await self.governance_client.record(
            candidate_hash=bundle["candidate_hash"],
            report_hash=bundle["report_hash"],
            attestation_hash=bundle["attestation_hash"],
            verifier_version_hash=bundle["verifier_version_hash"],
            decision_value=decision_value,
        )

        if record_res.get("success") is False or record_res.get("status", 0) != 0 or "error" in record_res:
            try:
                existing = await self.governance_client.get_decision(bundle["candidate_hash"])
                if existing.get("exists") is True:
                    raise GovernanceCoordinatorError("AEGIS: UPDATE_ALREADY_DECIDED")
            except GovernanceCoordinatorError:
                raise
            except Exception:
                pass
            msg = record_res.get("message") or record_res.get("error") or "GOVERNANCE_TX_FAILED"
            raise GovernanceCoordinatorError(f"GOVERNANCE_EXECUTION_FAILED: {msg}")

        tx_hash = record_res.get("transactionHash", "")
        raw_block = record_res.get("blockNumber", 0)
        block_number = int(raw_block, 0) if isinstance(raw_block, str) else int(raw_block)
        from_address = record_res.get("from", "")
        to_address = record_res.get("to", "")

        candidate_hash = bundle["candidate_hash"]
        candidate_id = bundle["candidate_id"]

        # G077: Staging Rule for APPROVED
        if decision_str == "APPROVED":
            staging_status = "STAGED"
            staging_file = self.staging_dir / f"{candidate_hash}.json"
            staging_meta = {
                "candidate_id": candidate_id,
                "candidate_hash": candidate_hash,
                "governance_decision": "APPROVED",
                "governance_tx_hash": tx_hash,
                "governance_block": block_number,
                "staging_authorized": True,
                "production_deployment_authorized": False,
            }
            staging_file.write_text(json.dumps(staging_meta, indent=2, sort_keys=True), encoding="utf-8")

        # G078: Rejected Rule for REJECTED
        else:
            staging_status = "FROZEN"
            frozen_file = self.frozen_dir / f"{candidate_hash}.json"
            frozen_meta = {
                "candidate_id": candidate_id,
                "candidate_hash": candidate_hash,
                "governance_decision": "REJECTED",
                "governance_tx_hash": tx_hash,
                "governance_block": block_number,
                "staging_authorized": False,
                "production_deployment_authorized": False,
            }
            frozen_file.write_text(json.dumps(frozen_meta, indent=2, sort_keys=True), encoding="utf-8")

        # G071 / G097: Record in university_governance_decisions
        pool = await self.get_pool()
        await pool.execute(
            """
            INSERT INTO university_governance_decisions (
                candidate_hash,
                candidate_id,
                verification_id,
                source_status,
                report_hash,
                attestation_hash,
                verifier_version_hash,
                decision,
                governance_tx_hash,
                governance_block_number,
                chain_verifier,
                staging_status
            ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
            ON CONFLICT (candidate_hash) DO NOTHING
            """,
            candidate_hash,
            candidate_id,
            bundle["verification_id"],
            source_status,
            bundle["report_hash"],
            bundle["attestation_hash"],
            bundle["verifier_version_hash"],
            decision_str,
            tx_hash,
            block_number,
            from_address,
            staging_status,
        )

        return {
            "status": 0,
            "decision": decision_str,
            "governance_decision": decision_str,
            "candidate_id": candidate_id,
            "candidate_hash": candidate_hash,
            "candidateHash": candidate_hash,
            "transactionHash": tx_hash,
            "blockNumber": block_number,
            "from": from_address,
            "to": to_address,
            "chain_verifier": from_address,
            "staging_status": staging_status,
        }
