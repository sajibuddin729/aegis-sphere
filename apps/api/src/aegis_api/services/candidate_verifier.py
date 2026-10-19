import json
import uuid
from pathlib import Path

from aegis_api.domain.verification import (
    VerificationCheckResult,
    VerificationReport,
)
from aegis_api.services.crypto import (
    canonical_hash,
)


from aegis_api.services.security_invariant_verifier import (
    SecurityInvariantVerifier,
)


class CandidateVerifier:

    VERIFIER_VERSION = (
        "aegis-independent-verifier-v1"
    )

    def __init__(
        self,
        *,
        intake,
        schema,
        static,
        code_guard,
        behavior,
        regression,
        report_store,
        attestation,
        project_root: Path,
        security_invariants: SecurityInvariantVerifier | None = None,
    ):
        self.intake = intake
        self.schema = schema
        self.static = static
        self.code_guard = code_guard
        self.behavior = behavior
        self.regression = regression
        self.report_store = report_store
        self.attestation = attestation
        self.project_root = project_root
        self.security_invariants = security_invariants or SecurityInvariantVerifier()

    async def verify(
        self,
        candidate_id: str,
    ) -> dict:

        verification_id = "verification_" + uuid.uuid4().hex

        payload, candidate_hash = self.intake.load(candidate_id)

        checks = []

        try:
            proposal = self.schema.verify(payload)
            schema_pass = True
            schema_details = {
                "update_type": proposal.update_type
            }
        except Exception as exc:
            schema_pass = False
            schema_details = {
                "error": str(exc)
            }
            proposal = None

        checks.append(
            self._check(
                "schema",
                schema_pass,
                "INFO" if schema_pass else "CRITICAL",
                schema_details,
            )
        )

        if proposal is None:
            return self._finalize(
                verification_id=verification_id,
                candidate_id=candidate_id,
                candidate_hash=candidate_hash,
                checks=checks,
            )

        text = json.dumps(
            proposal.model_dump(mode="json"),
            ensure_ascii=False,
            sort_keys=True,
        )

        secret = self.static.scan_secrets(text)
        checks.append(
            self._check(
                "secret_scan",
                secret.passed,
                "INFO" if secret.passed else "CRITICAL",
                {"findings": secret.findings},
            )
        )

        capability = self.static.scan_capabilities(text)
        checks.append(
            self._check(
                "capability",
                capability.passed,
                "INFO" if capability.passed else "CRITICAL",
                {"findings": capability.findings},
            )
        )

        network = self.static.scan_network(text)
        checks.append(
            self._check(
                "network",
                network.passed,
                "INFO" if network.passed else "HIGH",
                {"findings": network.findings},
            )
        )

        ast_pass, ast_findings = self.code_guard.inspect_data_only(
            proposal.proposed_change
        )
        checks.append(
            self._check(
                "ast",
                ast_pass,
                "INFO" if ast_pass else "HIGH",
                {"findings": ast_findings},
            )
        )

        # Part G1 data-only candidates, therefore imports are prohibited
        import_pass = (
            ast_pass
            and "Import" not in ast_findings
            and "ImportFrom" not in ast_findings
        )
        checks.append(
            self._check(
                "imports",
                import_pass,
                "INFO" if import_pass else "HIGH",
                {"data_only": True},
            )
        )

        signer_pass = (secret.passed and capability.passed)
        checks.append(
            self._check(
                "signer_boundary",
                signer_pass,
                "INFO" if signer_pass else "CRITICAL",
                {
                    "private_key_access": False if signer_pass else "POTENTIAL"
                },
            )
        )

        static_security_pass = (
            secret.passed
            and capability.passed
            and network.passed
        )
        checks.append(
            self._check(
                "static_security",
                static_security_pass,
                "INFO" if static_security_pass else "CRITICAL",
                {},
            )
        )

        behavior_pass, behavior_findings = self.behavior.verify(text)
        checks.append(
            self._check(
                "behavior",
                behavior_pass,
                "INFO" if behavior_pass else "CRITICAL",
                {"findings": behavior_findings},
            )
        )

        regression = await self.regression.run(
            project_root=str(self.project_root)
        )
        checks.append(
            self._check(
                "regression",
                regression["passed"],
                "INFO" if regression["passed"] else "CRITICAL",
                regression,
            )
        )

        invariants_pass, invariants_findings = self.security_invariants.verify(
            proposal
        )
        checks.append(
            self._check(
                "security_invariants",
                invariants_pass,
                "INFO" if invariants_pass else "CRITICAL",
                {"findings": invariants_findings},
            )
        )

        return self._finalize(
            verification_id=verification_id,
            candidate_id=candidate_id,
            candidate_hash=candidate_hash,
            checks=checks,
        )

    def _check(
        self,
        name,
        passed,
        severity,
        details,
    ):
        evidence_hash = canonical_hash(
            {
                "name": name,
                "passed": passed,
                "severity": severity,
                "details": details,
            }
        )

        return VerificationCheckResult(
            name=name,
            passed=passed,
            severity=severity,
            details=details,
            evidence_hash=evidence_hash,
        )

    def _finalize(
        self,
        *,
        verification_id,
        candidate_id,
        candidate_hash,
        checks,
    ):
        required = {
            "schema",
            "secret_scan",
            "ast",
            "imports",
            "capability",
            "network",
            "signer_boundary",
            "static_security",
            "regression",
            "behavior",
            "security_invariants",
        }

        passed_names = {
            item.name
            for item in checks
            if item.passed
        }

        all_pass = required.issubset(passed_names)

        reasons = [
            item.name + ":" + item.severity
            for item in checks
            if not item.passed
        ]

        score = (
            100.0
            if all_pass
            else round(
                100.0
                * len(passed_names & required)
                / len(required),
                2,
            )
        )

        decision = "VERIFIED" if all_pass else "REJECTED"

        report = VerificationReport(
            schema_version="aegis-verification-report-v1",
            verification_id=verification_id,
            candidate_id=candidate_id,
            candidate_hash=candidate_hash,
            verifier_version=self.VERIFIER_VERSION,
            checks=checks,
            verification_score=score,
            final_decision=decision,
            rejection_reasons=reasons,
        )

        report_hash = self.report_store.persist(report)

        attestation, attestation_hash = self.attestation.build(
            verification_id=verification_id,
            candidate_id=candidate_id,
            candidate_hash=candidate_hash,
            report_hash=report_hash,
            decision=decision,
        )

        return {
            "report": report,
            "report_hash": report_hash,
            "attestation": attestation,
            "attestation_hash": attestation_hash,
        }
