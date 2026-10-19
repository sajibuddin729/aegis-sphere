import json
from datetime import (
    datetime,
    timezone,
)
from pathlib import Path

from aegis_api.clients.rag import (
    RagClient,
)
from aegis_api.domain.compliance import (
    ChinaComplianceResult,
)
from aegis_api.domain.regulatory import (
    ComplianceEvidencePackage,
    RegulatoryComplianceBundle,
    RegulatoryEvidenceRef,
)
from aegis_api.services.compliance_reasoner import (
    ComplianceReasoner,
)
from aegis_api.services.crypto import (
    canonical_hash,
    sha256_text,
)


class RegulatoryEvidenceUnavailable(RuntimeError):
    pass


class RegulatoryCompliancePipeline:
    RULE_QUERIES = {
        "RAW_PII_IN_FINANCIAL_CONTEXT": (
            "personal information processing protection network data security"
        ),
        "SENSITIVE_IDENTITY_DATA": (
            "sensitive personal information identity data protection"
        ),
        "CROSS_BORDER_PII_NOT_AUTHORIZED": (
            "cross-border personal information overseas data transfer requirements"
        ),
        "IMPORTANT_DATA_NOT_AUTHORIZED": (
            "important data network data security risk assessment"
        ),
        "AML_SUSPICIOUS_INSTRUCTION_PATTERN": (
            "anti-money laundering suspicious transaction monitoring obligations"
        ),
        "PRIVATE_KEY_DISCLOSURE_REQUEST": (
            "cybersecurity credential security protection"
        ),
        "UNAPPROVED_ASSET": (
            "financial data security risk management"
        ),
        "UNAPPROVED_RECIPIENT": (
            "financial transaction security risk management"
        ),
    }

    def __init__(
        self,
        *,
        rag: RagClient,
        reasoner: ComplianceReasoner,
        evidence_dir: Path,
        effective_policy_hash: str | None = None,
    ):
        self.rag = rag
        self.reasoner = reasoner
        self.evidence_dir = evidence_dir
        self.effective_policy_hash = effective_policy_hash
        self.evidence_dir.mkdir(
            parents=True,
            exist_ok=True,
        )

    async def assess(
        self,
        *,
        instruction: str,
        purpose: str,
        asset: str,
        recipient_approved: bool,
        risk_score_bps: int,
        deterministic_result: ChinaComplianceResult,
    ) -> RegulatoryComplianceBundle:
        query = self._query(deterministic_result)

        retrieval = await self.rag.retrieve(
            query=query,
            top_k=5,
        )

        hits = retrieval.get("hits", [])
        if not hits:
            raise RegulatoryEvidenceUnavailable(
                "No regulatory evidence retrieved"
            )

        normalized_context = {
            "asset": asset,
            "recipient_approved": recipient_approved,
            "risk_score_bps": risk_score_bps,
            "triggered_rules": deterministic_result.triggered_rules,
        }

        reasoning = await self.reasoner.analyze(
            deterministic_result=deterministic_result.model_dump(
                mode="json"
            ),
            retrieval=retrieval,
            normalized_context=normalized_context,
        )

        evidence_refs = [
            RegulatoryEvidenceRef(
                rank=hit["rank"],
                score=int(round(float(hit.get("score", 0.0)) * 10000)),
                source_id=hit["source_id"],
                chunk_id=hit["chunk_id"],
                title=hit["title"],
                text_sha256=hit["text_sha256"],
                source_snapshot_sha256=hit["source_snapshot_sha256"],
            )
            for hit in hits
        ]

        # Deterministic engine remains the final authority.
        final_decision = deterministic_result.decision

        package = ComplianceEvidencePackage(
            schema_version="aegis-compliance-evidence-v1",
            policy_version=deterministic_result.policy_version,
            instruction_hash=sha256_text(instruction),
            purpose_hash=sha256_text(purpose),
            asset=asset,
            recipient_approved=recipient_approved,
            risk_score_bps=risk_score_bps,
            deterministic_decision=deterministic_result.decision,
            triggered_rules=sorted(deterministic_result.triggered_rules),
            deterministic_source_refs=sorted(deterministic_result.source_refs),
            retrieval_query=query,
            retrieval_model=retrieval["model"],
            retrieval_mode=retrieval["retrieval_mode"],
            retrieval_hits=evidence_refs,
            reasoner_assessment=reasoning.assessment,
            reasoner_source_ids=sorted(reasoning.applicable_source_ids),
            reasoner_chunk_ids=sorted(reasoning.applicable_chunk_ids),
            reasoner_summary=reasoning.summary,
            final_decision=final_decision,
            created_at_utc=datetime.now(timezone.utc).isoformat(),
        )

        retrieval_identity = [
            {
                "source_id": item.source_id,
                "chunk_id": item.chunk_id,
                "text_sha256": item.text_sha256,
                "source_snapshot_sha256": item.source_snapshot_sha256,
            }
            for item in evidence_refs
        ]

        if not self.effective_policy_hash:
            raise RuntimeError("EFFECTIVE_POLICY_HASH_REQUIRED")
        policy_version_hash = self.effective_policy_hash
        retrieval_set_hash = canonical_hash(
            {"retrieval_hits": retrieval_identity}
        )
        decision_hash = canonical_hash(
            {
                "final_decision": final_decision,
                "deterministic_decision": package.deterministic_decision,
                "triggered_rules": package.triggered_rules,
                "reasoner_assessment": package.reasoner_assessment,
                "risk_score_bps": package.risk_score_bps,
            }
        )
        evidence_hash = canonical_hash(
            package.model_dump(mode="json")
        )

        self._persist(
            evidence_hash,
            package,
        )

        return RegulatoryComplianceBundle(
            final_decision=final_decision,
            evidence_hash=evidence_hash,
            policy_version_hash=policy_version_hash,
            retrieval_set_hash=retrieval_set_hash,
            decision_hash=decision_hash,
            package=package,
        )

    def load_evidence(
        self,
        evidence_hash: str,
    ) -> dict:
        path = self.evidence_dir / (
            evidence_hash.removeprefix("0x") + ".json"
        )
        if not path.exists():
            raise FileNotFoundError(evidence_hash)

        payload = json.loads(
            path.read_text(encoding="utf-8")
        )
        actual = canonical_hash(payload)
        if actual != evidence_hash:
            raise RuntimeError(
                "COMPLIANCE_EVIDENCE_HASH_MISMATCH"
            )

        return payload

    def _persist(
        self,
        evidence_hash: str,
        package: ComplianceEvidencePackage,
    ) -> None:
        path = self.evidence_dir / (
            evidence_hash.removeprefix("0x") + ".json"
        )
        path.write_text(
            json.dumps(
                package.model_dump(mode="json"),
                ensure_ascii=False,
                indent=2,
                sort_keys=True,
            ),
            encoding="utf-8",
        )

    def _query(
        self,
        result: ChinaComplianceResult,
    ) -> str:
        queries = [
            self.RULE_QUERIES[rule]
            for rule in result.triggered_rules
            if rule in self.RULE_QUERIES
        ]

        if queries:
            return " ; ".join(sorted(set(queries)))

        return (
            "financial AI transaction network data security "
            "personal information risk management and security obligations"
        )
