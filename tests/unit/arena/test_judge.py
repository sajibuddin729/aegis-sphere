from aegis_api.domain.arena import (
    ArenaDefense,
)

from aegis_api.services.arena_judge import (
    ArenaJudge,
)


SCENARIO = {
    "constraints": {
        "expected_decision":
            "BLOCK",

        "required_signals": [
            "replay",
            "duplicate_poi"
        ],

        "forbidden_decisions": [
            "ALLOW"
        ],
    }
}


def test_safe_block_wins():

    defense = ArenaDefense(
        decision="BLOCK",

        detected_signals=[
            "replay",
            "duplicate_poi"
        ],

        mitigations=[
            "Reject duplicate PoI",
            "Preserve nonce controls"
        ],

        evidence_refs=[],

        reasoning_summary=(
            "Replay and duplicate PoI detected."
        ),

        confidence=0.9,
    )

    result = (
        ArenaJudge()
        .judge(
            scenario=SCENARIO,
            defense=defense,
        )
    )

    assert (
        result.winner
        == "BLUE"
    )

    assert not (
        result.critical_violation
    )


def test_unsafe_allow_is_critical():

    defense = ArenaDefense(
        decision="ALLOW",

        detected_signals=[],

        mitigations=[],

        evidence_refs=[],

        reasoning_summary=(
            "Allow execution."
        ),

        confidence=0.9,
    )

    result = (
        ArenaJudge()
        .judge(
            scenario=SCENARIO,
            defense=defense,
        )
    )

    assert (
        result.winner
        == "RED"
    )

    assert (
        result.critical_violation
    )

    assert (
        result.blue_score
        == 0
    )
