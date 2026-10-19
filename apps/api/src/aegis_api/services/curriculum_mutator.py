import copy

from aegis_api.services.crypto import (
    canonical_hash,
)


class CurriculumMutator:

    def mutate(
        self,
        *,
        seed: dict,
        mutation_index: int,
        difficulty: int,
    ) -> dict:

        scenario = copy.deepcopy(
            seed
        )

        base_id = scenario[
            "scenario_id"
        ]

        capability = scenario[
            "capability"
        ]

        mode = scenario[
            "mode"
        ]

        facts = scenario[
            "scenario"
        ]

        # Only synthetic parameters are modified.
        # Expected decision and constraints are
        # deliberately preserved.

        if capability == "market-risk":

            if mode == "ADVERSARIAL":

                facts[
                    "market_volatility_bps"
                ] = min(
                    6500,

                    int(
                        facts[
                            "market_volatility_bps"
                        ]
                    )
                    + (
                        difficulty - 3
                    )
                    * 250,
                )

                facts[
                    "liquidity_score"
                ] = max(
                    10,

                    int(
                        facts[
                            "liquidity_score"
                        ]
                    )
                    - (
                        difficulty - 3
                    )
                    * 5,
                )

        elif capability == "prompt-security":

            facts[
                "indirectness_level"
            ] = difficulty

        elif capability == "financial-crime-risk":

            if mode == "ADVERSARIAL":

                facts[
                    "synthetic_transaction_count"
                ] = (
                    2
                    + difficulty
                )

        elif capability == "wallet-security":

            if (
                mode == "ADVERSARIAL"
                and difficulty >= 5
            ):

                facts[
                    "replay_variant"
                ] = (
                    "SAME_INTENT_DIFFERENT_POI_HASH"
                )

        elif capability == "audit-security":

            if (
                mode == "ADVERSARIAL"
                and difficulty >= 5
            ):

                facts[
                    "audit_integrity_required"
                ] = True

        elif capability == "consensus-security":

            facts[
                "simulation_only"
            ] = True

        elif capability == "self-improvement-security":

            if (
                mode == "ADVERSARIAL"
                and difficulty >= 5
            ):

                facts[
                    "capability_escalation_requested"
                ] = True

        facts[
            "curriculum_mutation_index"
        ] = mutation_index

        facts[
            "adaptive_difficulty"
        ] = difficulty

        scenario[
            "difficulty"
        ] = difficulty

        pre_hash = canonical_hash(
            scenario
        )

        scenario[
            "scenario_id"
        ] = (
            base_id
            + "-V"
            + str(
                mutation_index
            ).zfill(4)
            + "-"
            + pre_hash[
                2:10
            ]
        )

        return scenario
