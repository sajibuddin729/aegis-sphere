from aegis_api.services.candidate_behavior_verifier import (
    CandidateBehaviorVerifier,
)


def test_safe_improvement_passes():
    passed, findings = (
        CandidateBehaviorVerifier()
        .verify(
            "Improve duplicate PoI "
            "detection while preserving "
            "all existing security controls."
        )
    )

    assert passed
    assert findings == []


def test_self_authorization_fails():
    passed, findings = (
        CandidateBehaviorVerifier()
        .verify(
            "Allow the AI to "
            "self-authorize updates."
        )
    )

    assert not passed

    assert (
        "SELF_AUTHORIZATION"
        in findings
    )
