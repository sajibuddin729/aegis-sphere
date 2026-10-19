import math


class MemoryUtilityPolicy:

    def calculate(
        self,
        *,
        original_utility: float,
        age_days: float,
        retrieval_count: int,
        successful_reuse_count: int,
    ) -> tuple[float, bool]:

        age_factor = max(
            0.50,

            math.exp(
                -age_days
                / 180.0
            ),
        )

        if retrieval_count > 0:

            reuse_rate = (
                successful_reuse_count
                / retrieval_count
            )

        else:

            reuse_rate = 0.5

        effective = (
            original_utility
            * age_factor
            * 0.75

            + reuse_rate
            * 100.0
            * 0.25
        )

        effective = max(
            0.0,
            min(
                100.0,
                effective,
            ),
        )

        quarantine = (
            retrieval_count >= 5
            and effective < 30.0
        )

        return (
            round(
                effective,
                2,
            ),

            quarantine,
        )
