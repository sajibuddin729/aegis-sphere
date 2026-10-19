from aegis_api.domain.candidate_update import (
    CandidateUpdateProposal,
)

from aegis_api.services.candidate_policy import (
    CandidateUpdatePolicy,
)


def safe_candidate():

    return CandidateUpdateProposal(
        update_type="PROMPT",

        target_component=(
            "security-agent"
        ),

        title=(
            "Improve replay detection"
        ),

        problem=(
            "Replay patterns may be missed."
        ),

        proposed_change=(
            "Add explicit duplicate "
            "PoI reasoning guidance."
        ),

        expected_benefit=(
            "Improved replay detection."
        ),

        risks=[
            "Potential false positives"
        ],

        verification_tests=[
            "Replay test",
            "Valid transaction regression"
        ],

        confidence=0.8,
    )


def test_safe_candidate_is_only_proposed():

    result = (
        CandidateUpdatePolicy()
        .evaluate(
            safe_candidate()
        )
    )

    assert (
        result.status
        == "PROPOSED"
    )


def test_audit_bypass_candidate_is_frozen():

    proposal = safe_candidate()

    proposal.proposed_change = (
        "Bypass audit checks and "
        "auto-deploy the update."
    )

    result = (
        CandidateUpdatePolicy()
        .evaluate(
            proposal
        )
    )

    assert (
        result.status
        == "FROZEN"
    )
