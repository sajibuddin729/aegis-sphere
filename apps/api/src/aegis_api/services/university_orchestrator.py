import uuid

from aegis_api.domain.university import (
    UniversityEpisodeResult,
)
from aegis_api.services.crypto import (
    canonical_hash,
)
from aegis_api.services.university_agents import (
    UniversityAgent,
)
from aegis_api.services.university_evaluator import (
    UniversityEvaluator,
)

SPECIALISTS = [
    "MARKET_RESEARCH_AGENT",
    "ASSET_AGENT",
    "SECURITY_AGENT",
    "COMPLIANCE_AGENT",
    "RESEARCH_AGENT",
]


class UniversityOrchestrator:
    def __init__(
        self,
        *,
        llm,
        scenarios,
        repository,
        memory,
        evaluator: UniversityEvaluator,
        evolution_agent,
        pass_score: float,
        memory_min_score: float,
    ):
        self.llm = llm
        self.scenarios = scenarios
        self.repository = repository
        self.memory = memory
        self.evaluator = evaluator
        self.evolution_agent = (
            evolution_agent
        )
        self.pass_score = (
            pass_score
        )
        self.memory_min_score = (
            memory_min_score
        )

    async def run(
        self,
        scenario_id: str,
    ) -> UniversityEpisodeResult:
        scenario = (
            self.scenarios.get(scenario_id)
        )
        scenario_hash = (
            self.scenarios.hash(scenario)
        )

        await self.repository.ensure_scenario(
            scenario=scenario,
            scenario_hash=(
                scenario_hash
            ),
        )

        episode_id = (
            "episode_" + uuid.uuid4().hex
        )

        await self.repository.create_episode(
            episode_id=(
                episode_id
            ),
            scenario_id=(
                scenario_id
            ),
            mode=(
                scenario["mode"]
            ),
        )

        memory_query = (
            scenario["title"]
            + " "
            + str(scenario["scenario"])
        )

        memories = await self.memory.recall(
            query=(
                memory_query
            ),
            category=(
                scenario["category"]
            ),
        )

        outputs = {}
        turn_index = 0

        for role in SPECIALISTS:
            agent = UniversityAgent(
                llm=self.llm,
                role=role,
            )

            input_payload = {
                "scenario": scenario["scenario"],
                "constraints": scenario["constraints"],
                "previous_reports": {
                    key: value.model_dump()
                    for key, value in outputs.items()
                },
                "memory_ids": [
                    item["memory_id"]
                    for item in memories
                ],
            }

            response = await agent.run(
                scenario=(
                    scenario
                ),
                previous_reports=(
                    {
                        key: value.model_dump()
                        for key, value in outputs.items()
                    }
                ),
                recalled_memories=(
                    memories
                ),
            )

            outputs[role] = response

            await self.repository.add_turn(
                episode_id=(
                    episode_id
                ),
                turn_index=(
                    turn_index
                ),
                role=role,
                input_hash=(
                    canonical_hash(
                        input_payload
                    )
                ),
                output_hash=(
                    canonical_hash(
                        response.model_dump(
                            mode="json"
                        )
                    )
                ),
                output=(
                    response.model_dump(
                        mode="json"
                    )
                ),
            )

            turn_index += 1

        leader = UniversityAgent(
            llm=self.llm,
            role="LEADER_AGENT",
        )

        leader_response = (
            await leader.run(
                scenario=scenario,
                previous_reports={
                    key: value.model_dump()
                    for key, value in outputs.items()
                },
                recalled_memories=(
                    memories
                ),
            )
        )
        outputs["LEADER_AGENT"] = leader_response

        critic = UniversityAgent(
            llm=self.llm,
            role="CRITIC_AGENT",
        )

        critic_response = (
            await critic.run(
                scenario=scenario,
                previous_reports={
                    key: value.model_dump()
                    for key, value in outputs.items()
                },
                recalled_memories=(
                    memories
                ),
            )
        )
        outputs["CRITIC_AGENT"] = critic_response

        evaluation = (
            self.evaluator.evaluate(
                scenario=scenario,
                outputs=outputs,
            )
        )

        evaluation_hash = (
            canonical_hash(
                evaluation.model_dump(
                    mode="json"
                )
            )
        )

        transcript_hash = (
            canonical_hash(
                {
                    role: result.model_dump(
                        mode="json"
                    )
                    for role, result in outputs.items()
                }
            )
        )

        await self.repository.save_evaluation(
            episode_id=(
                episode_id
            ),
            evaluation=(
                evaluation.model_dump(
                    mode="json"
                )
            ),
            evaluator_hash=(
                evaluation_hash
            ),
        )

        memories_promoted = 0
        passed = (
            evaluation.total_score >= self.pass_score
            and not evaluation.critical_violation
        )

        if (
            evaluation.total_score >= self.memory_min_score
            and not evaluation.critical_violation
        ):
            lesson = (
                "Scenario: "
                + scenario["title"]
                + "\nFinal decision: "
                + leader_response.recommendation
                + "\nKey findings: "
                + "; ".join(
                    leader_response.key_findings
                )
                + "\nCritic findings: "
                + "; ".join(
                    critic_response.key_findings
                )
            )

            await self.memory.promote_memory(
                source_episode_id=(
                    episode_id
                ),
                category=(
                    scenario["category"]
                ),
                content=lesson,
                utility_score=(
                    evaluation.total_score
                ),
                metadata={
                    "scenario_id": scenario_id,
                    "evaluation_hash": evaluation_hash,
                },
            )

            memories_promoted = 1

        candidate = (
            await self.evolution_agent.propose(
                scenario=scenario,
                outputs={
                    key: value.model_dump()
                    for key, value in outputs.items()
                },
                evaluation=(
                    evaluation.model_dump()
                ),
            )
        )

        outcome = {
            "passed": passed,
            "candidate_update_preview": candidate.model_dump(
                mode="json"
            ),
            "memories_promoted": memories_promoted,
        }

        await self.repository.complete(
            episode_id=(
                episode_id
            ),
            final_decision=(
                leader_response.recommendation
            ),
            final_score=(
                evaluation.total_score
            ),
            critical_violation=(
                evaluation.critical_violation
            ),
            transcript_hash=(
                transcript_hash
            ),
            outcome=outcome,
        )

        return UniversityEpisodeResult(
            episode_id=(
                episode_id
            ),
            scenario_id=(
                scenario_id
            ),
            mode=(
                scenario["mode"]
            ),
            final_decision=(
                leader_response.recommendation
            ),
            final_score=(
                evaluation.total_score
            ),
            passed=passed,
            critical_violation=(
                evaluation.critical_violation
            ),
            transcript_hash=(
                transcript_hash
            ),
            memories_promoted=(
                memories_promoted
            ),
            candidate_updates_created=0,
            agent_outputs=outputs,
            evaluation=evaluation,
        )
