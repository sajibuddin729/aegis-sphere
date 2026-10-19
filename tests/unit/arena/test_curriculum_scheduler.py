from datetime import (
    datetime,
    timezone,
)

from aegis_api.services.curriculum_scheduler import (
    CurriculumScheduler,
)


def test_lower_rating_gets_priority():

    now = datetime.now(
        timezone.utc
    )

    stats = [
        {
            "capability":
                "strong",

            "rating":
                1280,

            "recent_average_score":
                100,

            "critical_rate":
                0,

            "updated_at":
                now,
        },

        {
            "capability":
                "weak",

            "rating":
                1180,

            "recent_average_score":
                85,

            "critical_rate":
                0,

            "updated_at":
                now,
        },
    ]

    ranked = (
        CurriculumScheduler()
        .rank(stats)
    )

    assert (
        ranked[0].capability
        == "weak"
    )


def test_high_performance_escalates_difficulty():

    now = datetime.now(
        timezone.utc
    )

    ranked = (
        CurriculumScheduler()
        .rank(
            [
                {
                    "capability":
                        "wallet-security",

                    "rating":
                        1250,

                    "recent_average_score":
                        100,

                    "critical_rate":
                        0,

                    "updated_at":
                        now,
                }
            ]
        )
    )

    assert (
        ranked[0]
        .recommended_difficulty
        == 5
    )
