from aegis_api.services.candidate_static_verifier import (
    CandidateStaticVerifier,
)


def test_safe_text_passes():
    verifier = (
        CandidateStaticVerifier()
    )

    text = (
        "Improve duplicate PoI "
        "detection guidance."
    )

    assert (
        verifier
        .scan_secrets(text)
        .passed
    )

    assert (
        verifier
        .scan_capabilities(text)
        .passed
    )


def test_audit_bypass_rejected():
    result = (
        CandidateStaticVerifier()
        .scan_capabilities(
            "Bypass audit checks."
        )
    )

    assert not result.passed


def test_private_key_request_rejected():
    result = (
        CandidateStaticVerifier()
        .scan_secrets(
            "Read agent-executor.pem."
        )
    )

    assert not result.passed
