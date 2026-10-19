from datetime import (
    datetime,
    timezone,
)

from aegis_api.services.memory_utility import (
    MemoryUtilityPolicy,
)


class MemoryMaintenanceService:

    def __init__(
        self,
        *,
        memory_service,
        policy:
            MemoryUtilityPolicy,
    ):
        self.memory = (
            memory_service
        )

        self.policy = policy

    async def refresh(
        self,
    ) -> dict:

        await self.memory.start()

        rows = await self.memory.pool.fetch(
            """
            SELECT
                memory_id,
                utility_score,
                retrieval_count,
                successful_reuse_count,
                created_at
            FROM university_memories
            """
        )

        quarantined = 0

        now = datetime.now(
            timezone.utc
        )

        for row in rows:

            age_days = max(
                0.0,

                (
                    now
                    - row[
                        "created_at"
                    ]
                ).total_seconds()
                / 86400.0,
            )

            (
                effective,
                should_quarantine,
            ) = self.policy.calculate(
                original_utility=float(
                    row[
                        "utility_score"
                    ]
                ),

                age_days=(
                    age_days
                ),

                retrieval_count=int(
                    row[
                        "retrieval_count"
                    ]
                ),

                successful_reuse_count=int(
                    row[
                        "successful_reuse_count"
                    ]
                ),
            )

            await self.memory.pool.execute(
                """
                UPDATE university_memories

                SET
                    effective_utility_score=$2,

                    quarantined=
                        CASE
                            WHEN $3
                            THEN TRUE
                            ELSE quarantined
                        END,

                    approved_for_reuse=
                        CASE
                            WHEN $3
                            THEN FALSE
                            ELSE approved_for_reuse
                        END,

                    quarantine_reason=
                        CASE
                            WHEN $3
                            THEN 'LOW_EFFECTIVE_UTILITY'
                            ELSE quarantine_reason
                        END

                WHERE memory_id=$1
                """,

                row[
                    "memory_id"
                ],

                effective,
                should_quarantine,
            )

            if should_quarantine:
                quarantined += 1

        return {
            "memories_checked":
                len(rows),

            "quarantined":
                quarantined,
        }
