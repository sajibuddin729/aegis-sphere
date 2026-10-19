import json
from pathlib import Path

import pytest

from aegis_api.services.candidate_intake import (
    CandidateIntakeError,
    CandidateIntakeService,
)
from aegis_api.services.crypto import (
    canonical_hash,
)


def test_valid_candidate_loads(
    tmp_path: Path,
):
    proposal = {
        "update_type": "PROMPT",
        "target_component": "security",
        "title": "Improve wording",
        "problem": "Weak wording",
        "proposed_change": "Clarify replay rejection.",
        "expected_benefit": "Better consistency",
        "risks": ["False positives"],
        "verification_tests": [
            "Replay",
            "Regression"
        ],
        "confidence": 9000
    }

    doc = {
        "candidate_id": "c1",
        "status": "PROPOSED",
        "proposal_hash": canonical_hash(
            proposal
        ),
        "proposal": proposal,
        "deployment_authorized": False
    }

    (
        tmp_path / "c1.json"
    ).write_text(
        json.dumps(doc)
    )

    payload, candidate_hash = (
        CandidateIntakeService(
            candidate_dirs=[
                tmp_path
            ]
        ).load("c1")
    )

    assert payload["candidate_id"] == "c1"
    assert candidate_hash.startswith("0x")


def test_tampered_proposal_rejected(
    tmp_path: Path,
):
    proposal = {
        "x": "safe"
    }

    doc = {
        "candidate_id": "c1",
        "status": "PROPOSED",
        "proposal_hash": canonical_hash(
            proposal
        ),
        "proposal": {
            "x": "tampered"
        },
        "deployment_authorized": False
    }

    (
        tmp_path / "c1.json"
    ).write_text(
        json.dumps(doc)
    )

    with pytest.raises(
        CandidateIntakeError,
        match="PROPOSAL_HASH_MISMATCH",
    ):
        CandidateIntakeService(
            candidate_dirs=[
                tmp_path
            ]
        ).load("c1")
