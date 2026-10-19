from datetime import (
    datetime,
    timezone,
)

from aegis_api.domain.curriculum import (
    CapabilityPriority,
)


class CurriculumScheduler:

    def rank(
        self,
        stats: list[dict],
    ) -> list[
        CapabilityPriority
    ]:

        now = datetime.now(
            timezone.utc
        )

        priorities = []

        for item in stats:

            rating = float(
                item["rating"]
            )

            recent = float(
                item[
                    "recent_average_score"
                ]
            )

            critical = float(
                item[
                    "critical_rate"
                ]
            )

            updated = item[
                "updated_at"
            ]

            age_hours = max(
                0.0,

                (
                    now - updated
                ).total_seconds()
                / 3600.0,
            )

            staleness = min(
                1.0,
                age_hours / 168.0,
            )

            rating_weakness = min(
                1.0,
                max(
                    0.0,
                    (
                        1300.0
                        - rating
                    )
                    / 200.0,
                ),
            )

            score_weakness = min(
                1.0,
                max(
                    0.0,
                    (
                        100.0
                        - recent
                    )
                    / 40.0,
                ),
            )

            critical_pressure = min(
                1.0,
                critical * 2.0,
            )

            priority = (
                0.45
                * rating_weakness

                + 0.25
                * score_weakness

                + 0.20
                * critical_pressure

                + 0.10
                * staleness
            )

            if (
                recent >= 95
                and critical == 0
            ):
                difficulty = 5

            elif recent >= 85:
                difficulty = 4

            elif recent >= 70:
                difficulty = 3

            else:
                difficulty = 2

            priorities.append(
                CapabilityPriority(
                    capability=(
                        item[
                            "capability"
                        ]
                    ),

                    rating=rating,

                    recent_average_score=(
                        recent
                    ),

                    critical_rate=(
                        critical
                    ),

                    staleness_score=(
                        round(
                            staleness,
                            4,
                        )
                    ),

                    priority_score=(
                        round(
                            priority,
                            4,
                        )
                    ),

                    recommended_difficulty=(
                        difficulty
                    ),
                )
            )

        return sorted(
            priorities,

            key=lambda value: (
                -value.priority_score,
                value.rating,
                value.capability,
            ),
        )
