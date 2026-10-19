import json

import asyncpg


class CurriculumRepository:

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

        self.pool = (
            await asyncpg
            .create_pool(
                self.database_url,

                min_size=1,
                max_size=5,
            )
        )

    async def capability_stats(
        self,
    ) -> list[dict]:

        await self.start()

        rows = await self.pool.fetch(
            """
            SELECT
                r.capability,
                r.rating,
                r.matches,
                r.updated_at,

                COALESCE(
                    recent.avg_score,
                    0
                ) AS recent_average_score,

                COALESCE(
                    recent.critical_rate,
                    0
                ) AS critical_rate

            FROM university_agent_ratings r

            LEFT JOIN LATERAL (
                SELECT
                    AVG(x.blue_score)
                        AS avg_score,

                    AVG(
                        CASE
                            WHEN x.critical_violation
                            THEN 1.0
                            ELSE 0.0
                        END
                    ) AS critical_rate

                FROM (
                    SELECT
                        blue_score,
                        critical_violation

                    FROM university_arena_matches

                    WHERE capability =
                        r.capability

                    ORDER BY
                        created_at DESC

                    LIMIT 5
                ) x
            ) recent
            ON TRUE

            WHERE
                r.agent_role =
                    'BLUE_TEAM'

            ORDER BY
                r.capability
            """
        )

        return [
            dict(row)
            for row
            in rows
        ]

    async def ensure_baseline(
        self,
        *,
        capability: str,
        rating: float,
        score: float,
        critical_rate: float,
        baseline_hash: str,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO
            university_capability_baselines (
                capability,
                baseline_rating,
                baseline_score,
                baseline_critical_rate,
                baseline_hash
            )
            VALUES (
                $1,$2,$3,$4,$5
            )
            ON CONFLICT (capability)
            DO NOTHING
            """,

            capability,
            rating,
            score,
            critical_rate,
            baseline_hash,
        )

    async def baseline(
        self,
        capability: str,
    ) -> dict:

        await self.start()

        row = await self.pool.fetchrow(
            """
            SELECT *
            FROM university_capability_baselines
            WHERE capability=$1
            """,

            capability,
        )

        return dict(row)

    async def create_run(
        self,
        *,
        run_id: str,
        steps: int,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO
            university_curriculum_runs (
                run_id,
                status,
                requested_steps
            )
            VALUES (
                $1,
                'RUNNING',
                $2
            )
            """,

            run_id,
            steps,
        )

    async def save_variant(
        self,
        *,
        variant_id: str,
        base_scenario_id: str,
        capability: str,
        mode: str,
        difficulty: int,
        mutation_index: int,
        variant_hash: str,
        scenario: dict,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO
            university_scenario_variants (
                variant_id,
                base_scenario_id,
                capability,
                mode,
                difficulty,
                mutation_index,
                variant_hash,
                scenario
            )
            VALUES (
                $1,$2,$3,$4,$5,$6,$7,$8::jsonb
            )
            ON CONFLICT (variant_id)
            DO NOTHING
            """,

            variant_id,
            base_scenario_id,
            capability,
            mode,
            difficulty,
            mutation_index,
            variant_hash,
            json.dumps(
                scenario
            ),
        )

    async def save_step(
        self,
        *,
        step_id: str,
        run_id: str,
        step_index: int,
        capability: str,
        priority_score: float,
        mode: str,
        difficulty: int,
        variant_id: str,
        match_id: str,
        blue_score: float,
        winner: str,
        critical_violation: bool,
        regression_detected: bool,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO
            university_curriculum_steps (
                step_id,
                run_id,
                step_index,
                capability,
                priority_score,
                mode,
                difficulty,
                variant_id,
                match_id,
                blue_score,
                winner,
                critical_violation,
                regression_detected
            )
            VALUES (
                $1,$2,$3,$4,$5,$6,$7,
                $8,$9,$10,$11,$12,$13
            )
            """,

            step_id,
            run_id,
            step_index,
            capability,
            priority_score,
            mode,
            difficulty,
            variant_id,
            match_id,
            blue_score,
            winner,
            critical_violation,
            regression_detected,
        )

    async def save_regression(
        self,
        *,
        event_id: str,
        run_id: str,
        step_id: str,
        capability: str,
        baseline_score: float,
        observed_score: float,
        score_delta: float,
        critical_violation: bool,
        severity: str,
        details: dict,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO
            university_regression_events (
                event_id,
                run_id,
                step_id,
                capability,
                baseline_score,
                observed_score,
                score_delta,
                critical_violation,
                severity,
                details
            )
            VALUES (
                $1,$2,$3,$4,$5,$6,$7,
                $8,$9,$10::jsonb
            )
            """,

            event_id,
            run_id,
            step_id,
            capability,
            baseline_score,
            observed_score,
            score_delta,
            critical_violation,
            severity,
            json.dumps(
                details
            ),
        )

    async def quarantine_match_memories(
        self,
        *,
        match_id: str,
        reason: str,
    ) -> int:

        await self.start()

        result = await self.pool.execute(
            """
            UPDATE university_memories

            SET
                approved_for_reuse=FALSE,
                quarantined=TRUE,
                quarantine_reason=$2

            WHERE
                metadata->>'source_match_id'
                = $1
            """,

            match_id,
            reason,
        )

        return int(
            result.split()[-1]
        )

    async def save_snapshot(
        self,
        *,
        run_id: str,
        capability: str,
        rating: float,
        recent_average_score: float,
        critical_rate: float,
        priority_score: float,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO
            university_capability_snapshots (
                run_id,
                capability,
                rating,
                recent_average_score,
                critical_rate,
                priority_score
            )
            VALUES (
                $1,$2,$3,$4,$5,$6
            )
            ON CONFLICT (run_id, capability)
            DO UPDATE SET
                rating=$3,
                recent_average_score=$4,
                critical_rate=$5,
                priority_score=$6
            """,

            run_id,
            capability,
            rating,
            recent_average_score,
            critical_rate,
            priority_score,
        )

    async def candidate_shortlist(
        self,
        *,
        limit: int = 5,
    ) -> list[str]:

        await self.start()

        rows = await self.pool.fetch(
            """
            SELECT
                c.candidate_id

            FROM university_candidate_updates c

            LEFT JOIN university_arena_matches m
            ON
                m.match_id =
                c.source_match_id

            WHERE
                c.status =
                'PROPOSED'

            ORDER BY
                COALESCE(
                    m.blue_score,
                    100
                ) ASC,
                c.created_at DESC

            LIMIT $1
            """,

            limit,
        )

        return [
            row[
                "candidate_id"
            ]
            for row
            in rows
        ]

    async def complete_run(
        self,
        *,
        run_id: str,
        completed_steps: int,
        average_blue_score: float,
        critical_violations: int,
        regression_count: int,
        summary: dict,
    ):

        await self.start()

        await self.pool.execute(
            """
            UPDATE university_curriculum_runs

            SET
                status='COMPLETED',
                completed_steps=$2,
                average_blue_score=$3,
                critical_violations=$4,
                regression_count=$5,
                summary=$6::jsonb,
                completed_at=NOW()

            WHERE run_id=$1
            """,

            run_id,
            completed_steps,
            average_blue_score,
            critical_violations,
            regression_count,
            json.dumps(
                summary
            ),
        )
