import json
import asyncpg
from pgvector.asyncpg import (
    register_vector,
)


class UniversityRepository:
    def __init__(
        self,
        database_url: str,
    ):
        self.database_url = (
            database_url
        )
        self.pool = None

    async def start(self):
        if self.pool is not None:
            return

        async def init(conn):
            await register_vector(conn)

        self.pool = await asyncpg.create_pool(
            self.database_url,
            min_size=1,
            max_size=5,
            init=init,
        )

    async def ensure_scenario(
        self,
        *,
        scenario: dict,
        scenario_hash: str,
    ):
        await self.start()
        await self.pool.execute(
            """
            INSERT INTO university_scenarios (
                scenario_id,
                scenario_hash,
                category,
                mode,
                difficulty,
                title,
                scenario,
                constraints
            )
            VALUES (
                $1,$2,$3,$4,$5,$6,
                $7::jsonb,$8::jsonb
            )
            ON CONFLICT (scenario_id) DO UPDATE
            SET scenario_hash = EXCLUDED.scenario_hash,
                scenario = EXCLUDED.scenario,
                constraints = EXCLUDED.constraints
            """,
            scenario["scenario_id"],
            scenario_hash,
            scenario["category"],
            scenario["mode"],
            scenario["difficulty"],
            scenario["title"],
            json.dumps(scenario["scenario"]),
            json.dumps(scenario["constraints"]),
        )

    async def create_episode(
        self,
        *,
        episode_id: str,
        scenario_id: str,
        mode: str,
    ):
        await self.start()
        await self.pool.execute(
            """
            INSERT INTO university_episodes (
                episode_id,
                scenario_id,
                mode,
                status
            )
            VALUES ($1,$2,$3,'RUNNING')
            """,
            episode_id,
            scenario_id,
            mode,
        )

    async def add_turn(
        self,
        *,
        episode_id: str,
        turn_index: int,
        role: str,
        input_hash: str,
        output_hash: str,
        output: dict,
    ):
        await self.start()
        await self.pool.execute(
            """
            INSERT INTO university_agent_turns (
                episode_id,
                turn_index,
                agent_role,
                input_hash,
                output_hash,
                output
            )
            VALUES (
                $1,$2,$3,$4,$5,$6::jsonb
            )
            """,
            episode_id,
            turn_index,
            role,
            input_hash,
            output_hash,
            json.dumps(output),
        )

    async def complete(
        self,
        *,
        episode_id: str,
        final_decision: str,
        final_score: float,
        critical_violation: bool,
        transcript_hash: str,
        outcome: dict,
    ):
        await self.start()
        await self.pool.execute(
            """
            UPDATE university_episodes
            SET status='COMPLETED',
                final_decision=$2,
                final_score=$3,
                critical_violation=$4,
                transcript_hash=$5,
                outcome=$6::jsonb,
                completed_at=NOW()
            WHERE episode_id=$1
            """,
            episode_id,
            final_decision,
            final_score,
            critical_violation,
            transcript_hash,
            json.dumps(outcome),
        )

    async def save_evaluation(
        self,
        *,
        episode_id: str,
        evaluation: dict,
        evaluator_hash: str,
    ):
        await self.start()
        await self.pool.execute(
            """
            INSERT INTO university_evaluations (
                episode_id,
                correctness_score,
                safety_score,
                compliance_score,
                evidence_score,
                cooperation_score,
                efficiency_score,
                total_score,
                critical_violation,
                weaknesses,
                strengths,
                evaluator_hash
            )
            VALUES (
                $1,$2,$3,$4,$5,$6,$7,$8,$9,
                $10::jsonb,$11::jsonb,$12
            )
            """,
            episode_id,
            evaluation["correctness_score"],
            evaluation["safety_score"],
            evaluation["compliance_score"],
            evaluation["evidence_score"],
            evaluation["cooperation_score"],
            evaluation["efficiency_score"],
            evaluation["total_score"],
            evaluation["critical_violation"],
            json.dumps(evaluation["weaknesses"]),
            json.dumps(evaluation["strengths"]),
            evaluator_hash,
        )
