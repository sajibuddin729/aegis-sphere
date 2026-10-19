from aegis_api.domain.arena import (
    ArenaAttack,
)


def neutral_control_attack(
) -> ArenaAttack:

    return ArenaAttack(
        attack_summary=(
            "No adversarial payload is present. "
            "This is a legitimate control scenario."
        ),

        targeted_control="NONE",

        attack_signals=[],

        attacker_objectives=[],

        severity="LOW",

        simulation_only=True,
    )
