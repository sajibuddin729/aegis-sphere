import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)

from aegis_api.domain.arena import (
    ArenaAttack,
    ArenaCritique,
    ArenaDefense,
)


RED_SYSTEM = """
You are the Aegis University Red-Team Agent.

Your job is to generate a SAFE SIMULATION of an
adversarial strategy against the supplied synthetic
scenario.

Rules:
- Simulation only.
- No real target.
- No real private credentials.
- No malware.
- No destructive commands.
- No executable exploit code.
- Describe attack logic at a high level.
- You cannot access production systems.
- Return only the required structured result.
"""


BLUE_SYSTEM = """
You are the Aegis University Blue-Team Defender.

Analyze the supplied synthetic scenario and Red-Team
attack.

Defend mandatory:
- financial risk controls,
- Proof-of-Intent replay protection,
- compliance gates,
- audit-before-execution,
- signer isolation,
- portfolio reserve limits,
- PBFT safety,
- update authorization boundaries.

You have no transaction authority.

Return only the required structured result.
"""


CRITIC_SYSTEM = """
You are the Aegis University Blue-Team Critic.

Challenge the defender's response.

Identify:
- missed attack signals,
- unsafe assumptions,
- contradictory controls,
- incorrect allow decisions,
- weak mitigations.

You cannot execute anything.

Return only the required structured result.
"""


LEADER_SYSTEM = """
You are the Aegis University Blue-Team Leader.

Produce the FINAL simulated defense after reviewing:
- scenario,
- Red-Team attack,
- initial defender response,
- critic review,
- approved semantic memories.

Mandatory safety controls cannot be treated as optional.

You have no production execution authority.

Return only the required structured result.
"""


class ArenaAgents:

    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def red_attack(
        self,
        scenario: dict,
    ) -> ArenaAttack:

        return await self.llm.structured(
            system_prompt=RED_SYSTEM,

            user_prompt=json.dumps(
                scenario,
                ensure_ascii=False,
                sort_keys=True,
                default=str,
            ),

            response_model=(
                ArenaAttack
            ),

            temperature=0.2,
        )

    async def defend(
        self,
        *,
        scenario: dict,
        attack: ArenaAttack,
        memories: list[dict],
    ) -> ArenaDefense:

        return await self.llm.structured(
            system_prompt=BLUE_SYSTEM,

            user_prompt=json.dumps(
                {
                    "scenario":
                        scenario,

                    "attack":
                        attack.model_dump(),

                    "approved_memories":
                        memories,
                },

                ensure_ascii=False,
                sort_keys=True,
                default=str,
            ),

            response_model=(
                ArenaDefense
            ),

            temperature=0.1,
        )

    async def critique(
        self,
        *,
        scenario: dict,
        attack: ArenaAttack,
        defense: ArenaDefense,
    ) -> ArenaCritique:

        return await self.llm.structured(
            system_prompt=CRITIC_SYSTEM,

            user_prompt=json.dumps(
                {
                    "scenario":
                        scenario,

                    "attack":
                        attack.model_dump(),

                    "defense":
                        defense.model_dump(),
                },

                ensure_ascii=False,
                sort_keys=True,
                default=str,
            ),

            response_model=(
                ArenaCritique
            ),

            temperature=0.0,
        )

    async def final_defense(
        self,
        *,
        scenario: dict,
        attack: ArenaAttack,
        initial_defense: ArenaDefense,
        critique: ArenaCritique,
        memories: list[dict],
    ) -> ArenaDefense:

        return await self.llm.structured(
            system_prompt=LEADER_SYSTEM,

            user_prompt=json.dumps(
                {
                    "scenario":
                        scenario,

                    "attack":
                        attack.model_dump(),

                    "initial_defense":
                        initial_defense
                        .model_dump(),

                    "critic":
                        critique.model_dump(),

                    "approved_memories":
                        memories,
                },

                ensure_ascii=False,
                sort_keys=True,
                default=str,
            ),

            response_model=(
                ArenaDefense
            ),

            temperature=0.0,
        )
