from aegis_api.clients.bridge import (
    FiscoBridgeClient,
)
from aegis_api.domain.regulatory import (
    RegulatoryComplianceBundle,
)


class AuditAnchorError(RuntimeError):
    pass


class AuditEvidenceAnchor:
    def __init__(
        self,
        bridge: FiscoBridgeClient,
    ):
        self.bridge = bridge

    async def anchor(
        self,
        *,
        compliance: RegulatoryComplianceBundle,
        related_poi_hash: str,
        evidence_hash: str | None = None,
    ) -> dict:
        ev_hash = evidence_hash or compliance.evidence_hash
        try:
            result = await self.bridge.audit_record(
                evidence_hash=ev_hash,
                policy_version_hash=compliance.policy_version_hash,
                retrieval_set_hash=compliance.retrieval_set_hash,
                decision_hash=compliance.decision_hash,
                related_poi_hash=related_poi_hash,
            )
        except Exception as exc:
            raise AuditAnchorError(
                "Audit evidence anchoring failed"
            ) from exc

        if not result.get("success", False):
            raise AuditAnchorError(
                "Audit evidence anchoring failed"
            )

        return result
