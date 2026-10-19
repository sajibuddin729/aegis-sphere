from typing import Union
from aegis_api.domain.candidate_update import (
    CandidateUpdateProposal,
    CandidateUpdateProposalV2,
)


class CandidateSchemaVerifier:

    ALLOWED_TYPES = {
        "PROMPT",
        "POLICY",
        "STRATEGY",
    }

    def verify(
        self,
        payload: dict,
    ) -> Union[CandidateUpdateProposal, CandidateUpdateProposalV2]:
        raw_proposal = payload.get("proposal", {})
        schema_version = raw_proposal.get("schema_version")

        if schema_version == "aegis-candidate-v2":
            proposal = CandidateUpdateProposalV2.model_validate(raw_proposal)

            if proposal.update_type == "CODE":
                raise ValueError("CODE_SANDBOX_REQUIRED")

            if proposal.update_type not in self.ALLOWED_TYPES:
                raise ValueError("UPDATE_TYPE_NOT_SUPPORTED")

            if len(proposal.risks) == 0:
                raise ValueError("RISK_ASSESSMENT_REQUIRED")

            if len(proposal.verification_tests) < 2:
                raise ValueError("VERIFICATION_TESTS_REQUIRED")

            if not proposal.rollback_plan or not proposal.rollback_plan.strip():
                raise ValueError("ROLLBACK_PLAN_REQUIRED")

            if len(proposal.security_invariants) < 3:
                raise ValueError("SECURITY_INVARIANTS_REQUIRED")

            return proposal

        # Fallback to V1
        proposal = CandidateUpdateProposal.model_validate(raw_proposal)

        if proposal.update_type == "CODE":
            raise ValueError("CODE_SANDBOX_REQUIRED")

        if proposal.update_type not in self.ALLOWED_TYPES:
            raise ValueError("UPDATE_TYPE_NOT_SUPPORTED_IN_G1")

        if len(proposal.risks) == 0:
            raise ValueError("RISK_ASSESSMENT_REQUIRED")

        if len(proposal.verification_tests) < 2:
            raise ValueError("VERIFICATION_TESTS_REQUIRED")

        return proposal
