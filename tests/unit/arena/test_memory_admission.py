from aegis_api.domain.arena import (
    ArenaDefense,
    ArenaJudgement,
)

from aegis_api.services.memory_admission import (
    ArenaMemoryAdmissionPolicy,
)


SCENARIO = {
    "title":
        "Replay Defense",

    "capability":
        "wallet-security",
}


def defense():

    return ArenaDefense(
        decision="BLOCK",

        detected_signals=[
            "replay",
            "duplicate_poi"
        ],

        mitigations=[
            "reject duplicate PoI",
            "preserve replay protection"
        ],

        evidence_refs=[
            "PoIRegistry"
        ],

        reasoning_summary=(
            "Duplicate PoI must be blocked."
        ),

        confidence=0.95,
    )


def judgement(
    *,
    score=95,
    critical=False,
    winner="BLUE",
):

    return ArenaJudgement(
        blue_score=score,

        red_score=(
            100 - score
        ),

        winner=winner,

        signal_coverage=1.0,

        correct_decision=True,

        critical_violation=critical,

        weaknesses=[],

        strengths=[
            "SAFE"
        ],
    )


def test_safe_blue_lesson_is_admitted():

    result = (
        ArenaMemoryAdmissionPolicy()
        .evaluate(
            scenario=SCENARIO,

            defense=defense(),

            judgement=(
                judgement()
            ),
        )
    )

    assert result.admitted


def test_critical_match_never_enters_memory():

    result = (
        ArenaMemoryAdmissionPolicy()
        .evaluate(
            scenario=SCENARIO,

            defense=defense(),

            judgement=(
                judgement(
                    critical=True
                )
            ),
        )
    )

    assert not result.admitted


def test_red_team_loss_not_stored():

    result = (
        ArenaMemoryAdmissionPolicy()
        .evaluate(
            scenario=SCENARIO,

            defense=defense(),

            judgement=(
                judgement(
                    score=55,
                    winner="RED"
                )
            ),
        )
    )

    assert not result.admitted
