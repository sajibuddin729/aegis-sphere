from aegis_api.domain.compliance import (
    ChinaComplianceResult,
)
from aegis_api.services.crypto import (
    canonical_hash,
)


def compliance_evidence_hash(
    result: ChinaComplianceResult,
) -> str:
    return canonical_hash(
        result.model_dump(
            mode="json"
        )
    )
