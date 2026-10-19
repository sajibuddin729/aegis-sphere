import re
from dataclasses import dataclass
from typing import Any, Union

from aegis_api.domain.candidate_update import (
    CandidateUpdateProposal,
    CandidateUpdateProposalV2,
)


@dataclass(frozen=True)
class CandidatePolicyResult:
    status: str
    reason: str | None


class CandidateUpdatePolicy:

    DANGEROUS_PATTERNS = [
        r"\bself[- ]?authorize\b",
        r"\bauto[- ]?deploy\b",
        r"\bdisable .*guard\b",
        r"\bremove .*guard\b",
        r"\bbypass .*audit\b",
        r"\bskip .*audit\b",
        r"\bdisable .*risk\b",
        r"\bbypass .*risk\b",
        r"\bprivate key\b",
        r"\bagent-executor\.pem\b",
        r"\bFISCO_ACCOUNT_PEM\b",
        r"\bAEGIS_BRIDGE_KEY\b",
    ]

    def evaluate(
        self,
        proposal: Union[CandidateUpdateProposal, CandidateUpdateProposalV2, Any],
    ) -> CandidatePolicyResult:
        # Check verification tests
        verification_tests = getattr(proposal, "verification_tests", [])
        if len(verification_tests) < 2:
            return CandidatePolicyResult(
                status="FROZEN",
                reason="INSUFFICIENT_VERIFICATION_TESTS",
            )

        # Check risks
        risks = getattr(proposal, "risks", [])
        if len(risks) == 0:
            return CandidatePolicyResult(
                status="FROZEN",
                reason="NO_RISK_ASSESSMENT",
            )

        # For V2 proposals
        if isinstance(proposal, CandidateUpdateProposalV2) or getattr(proposal, "schema_version", "") == "aegis-candidate-v2":
            rollback_plan = getattr(proposal, "rollback_plan", "")
            if not rollback_plan or not str(rollback_plan).strip():
                return CandidatePolicyResult(
                    status="FROZEN",
                    reason="NO_ROLLBACK_PLAN",
                )

            security_invariants = getattr(proposal, "security_invariants", [])
            if len(security_invariants) < 3:
                return CandidatePolicyResult(
                    status="FROZEN",
                    reason="INSUFFICIENT_SECURITY_INVARIANTS",
                )

            if getattr(proposal, "deployment_authorized", False) is True:
                return CandidatePolicyResult(
                    status="FROZEN",
                    reason="PROHIBITED_SELF_AUTHORIZATION",
                )

        combined = (
            str(getattr(proposal, "problem", ""))
            + "\n"
            + str(getattr(proposal, "proposed_change", ""))
            + "\n"
            + str(getattr(proposal, "expected_benefit", ""))
        )

        for pattern in self.DANGEROUS_PATTERNS:
            if re.search(pattern, combined, re.IGNORECASE):
                return CandidatePolicyResult(
                    status="FROZEN",
                    reason="PROHIBITED_AUTONOMY_OR_SECURITY_CHANGE",
                )

        return CandidatePolicyResult(
            status="PROPOSED",
            reason=None,
        )
