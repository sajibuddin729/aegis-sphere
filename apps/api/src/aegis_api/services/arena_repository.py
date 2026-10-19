import json

import asyncpg


class ArenaRepository:

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

        self.pool = await asyncpg.create_pool(
            self.database_url,
            min_size=1,
            max_size=5,
        )

    async def rating(
        self,
        *,
        agent_role: str,
        capability: str,
    ) -> float:

        await self.start()

        row = await self.pool.fetchrow(
            """
            INSERT INTO university_agent_ratings (
                agent_role,
                capability
            )
            VALUES ($1,$2)
            ON CONFLICT (
                agent_role,
                capability
            )
            DO UPDATE SET
                agent_role =
                    EXCLUDED.agent_role
            RETURNING rating
            """,

            agent_role,
            capability,
        )

        return float(
            row["rating"]
        )

    async def save_match(
        self,
        *,
        match_id: str,
        scenario_id: str,
        scenario_hash: str,
        capability: str,
        red_output_hash: str,
        blue_output_hash: str,
        critic_output_hash: str,
        blue_score: float,
        red_score: float,
        winner: str,
        critical_violation: bool,
        transcript_hash: str,
        outcome: dict,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO university_arena_matches (
                match_id,
                scenario_id,
                scenario_hash,
                capability,
                red_agent,
                blue_agent,
                red_output_hash,
                blue_output_hash,
                critic_output_hash,
                blue_score,
                red_score,
                winner,
                critical_violation,
                transcript_hash,
                outcome
            )
            VALUES (
                $1,$2,$3,$4,
                'RED_TEAM',
                'BLUE_TEAM',
                $5,$6,$7,$8,$9,$10,
                $11,$12,$13::jsonb
            )
            """,

            match_id,
            scenario_id,
            scenario_hash,
            capability,

            red_output_hash,
            blue_output_hash,
            critic_output_hash,

            blue_score,
            red_score,

            winner,
            critical_violation,

            transcript_hash,

            json.dumps(
                outcome,
                default=str,
            ),
        )

    async def update_rating(
        self,
        *,
        agent_role: str,
        capability: str,
        rating: float,
        result: str,
    ):

        await self.start()

        win = (
            1
            if result == "WIN"
            else 0
        )

        loss = (
            1
            if result == "LOSS"
            else 0
        )

        draw = (
            1
            if result == "DRAW"
            else 0
        )

        await self.pool.execute(
            """
            UPDATE university_agent_ratings
            SET
                rating=$3,
                matches=matches+1,
                wins=wins+$4,
                losses=losses+$5,
                draws=draws+$6,
                updated_at=NOW()
            WHERE
                agent_role=$1
                AND capability=$2
            """,

            agent_role,
            capability,
            rating,
            win,
            loss,
            draw,
        )

    async def ratings(
        self,
    ) -> list[dict]:

        await self.start()

        rows = await self.pool.fetch(
            """
            SELECT
                agent_role,
                capability,
                rating,
                matches,
                wins,
                losses,
                draws
            FROM university_agent_ratings
            ORDER BY
                capability,
                rating DESC
            """
        )

        return [
            {
                "agent_role": row["agent_role"],
                "capability": row["capability"],
                "rating": float(row["rating"]),
                "matches": row["matches"],
                "wins": row["wins"],
                "losses": row["losses"],
                "draws": row["draws"],
            }
            for row
            in rows
        ]

    async def memory_admission(
        self,
        *,
        admission_id: str,
        match_id: str,
        content_hash: str,
        score: float,
        critical_violation: bool,
        admitted: bool,
        rejection_reason:
            str | None,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO university_memory_admissions (
                admission_id,
                match_id,
                content_hash,
                score,
                critical_violation,
                admitted,
                rejection_reason
            )
            VALUES (
                $1,$2,$3,$4,$5,$6,$7
            )
            """,

            admission_id,
            match_id,
            content_hash,
            score,
            critical_violation,
            admitted,
            rejection_reason,
        )

    async def save_candidate(
        self,
        *,
        candidate_id: str,
        source_match_id: str,
        proposal,
        proposal_hash: str,
        status: str,
        policy_result: str,
        frozen_reason:
            str | None,
    ):

        await self.start()

        await self.pool.execute(
            """
            INSERT INTO university_candidate_updates (
                candidate_id,
                source_match_id,
                update_type,
                target_component,
                title,
                proposal,
                proposal_hash,
                status,
                policy_result,
                frozen_reason
            )
            VALUES (
                $1,$2,$3,$4,$5,
                $6::jsonb,$7,$8,$9,$10
            )
            """,

            candidate_id,
            source_match_id,

            proposal.update_type,
            proposal.target_component,
            proposal.title,

            json.dumps(
                proposal.model_dump(
                    mode="json"
                ),
                default=str,
            ),

            proposal_hash,
            status,
            policy_result,
            frozen_reason,
        )
