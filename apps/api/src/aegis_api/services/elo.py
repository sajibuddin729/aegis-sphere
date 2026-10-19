from aegis_api.domain.arena import (
    EloUpdate,
)


class EloEngine:

    def __init__(
        self,
        *,
        k_factor: float = 32.0,
    ):
        self.k_factor = (
            k_factor
        )

    def update(
        self,
        *,
        red_rating: float,
        blue_rating: float,
        winner: str,
    ) -> EloUpdate:

        expected_red = (
            1.0
            / (
                1.0
                + 10.0 ** (
                    (
                        blue_rating
                        - red_rating
                    )
                    / 400.0
                )
            )
        )

        expected_blue = (
            1.0
            - expected_red
        )

        if winner == "RED":
            red_result = 1.0
            blue_result = 0.0

        elif winner == "BLUE":
            red_result = 0.0
            blue_result = 1.0

        else:
            red_result = 0.5
            blue_result = 0.5

        red_after = (
            red_rating
            + self.k_factor
            * (
                red_result
                - expected_red
            )
        )

        blue_after = (
            blue_rating
            + self.k_factor
            * (
                blue_result
                - expected_blue
            )
        )

        return EloUpdate(
            red_before=round(
                red_rating,
                2,
            ),

            red_after=round(
                red_after,
                2,
            ),

            blue_before=round(
                blue_rating,
                2,
            ),

            blue_after=round(
                blue_after,
                2,
            ),
        )
