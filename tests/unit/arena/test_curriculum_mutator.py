from copy import deepcopy

from aegis_api.services.curriculum_mutator import (
    CurriculumMutator,
)


SEED = {
    "scenario_id":
        "TEST-001",

    "title":
        "Market Stress",

    "capability":
        "market-risk",

    "mode":
        "ADVERSARIAL",

    "difficulty":
        3,

    "scenario": {
        "market_volatility_bps":
            4000,

        "liquidity_score":
            30
    },

    "constraints": {
        "expected_decision":
            "BLOCK",

        "required_signals": [
            "extreme_volatility"
        ],

        "forbidden_decisions": [
            "ALLOW"
        ],
    }
}


def test_mutator_preserves_constraints():

    original = deepcopy(
        SEED[
            "constraints"
        ]
    )

    result = (
        CurriculumMutator()
        .mutate(
            seed=SEED,
            mutation_index=1,
            difficulty=5,
        )
    )

    assert (
        result[
            "constraints"
        ]
        == original
    )


def test_mutation_changes_scenario_id():

    result = (
        CurriculumMutator()
        .mutate(
            seed=SEED,
            mutation_index=1,
            difficulty=5,
        )
    )

    assert (
        result[
            "scenario_id"
        ]
        != SEED[
            "scenario_id"
        ]
    )
