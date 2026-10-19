from aegis_api.domain.portfolio import (
    RebalanceCalculation,
)


class PortfolioCalculationError(
    RuntimeError
):
    pass


class PortfolioRebalanceCalculator:

    def calculate(
        self,
        *,
        state: dict,
        target_agix_bps: int,
        min_acny_reserve_bps: int,
    ) -> RebalanceCalculation:

        total = int(
            state[
                "totalValueAcnyMinor"
            ]
        )

        balance_acny = int(
            state[
                "balanceAcnyMinor"
            ]
        )

        balance_agix = int(
            state[
                "balanceAgixMinor"
            ]
        )

        price = int(
            state[
                "priceAgixInAcny"
            ]
        )

        current_acny_bps = int(
            state[
                "acnyAllocationBps"
            ]
        )

        current_agix_bps = int(
            state[
                "agixAllocationBps"
            ]
        )

        target_acny_bps = (
            10000
            - target_agix_bps
        )

        if (
            target_acny_bps
            < min_acny_reserve_bps
        ):
            raise PortfolioCalculationError(
                "MIN_RESERVE_VIOLATION"
            )

        target_agix_value = (
            total
            * target_agix_bps
            // 10000
        )

        current_agix_value = (
            balance_agix
            * price
        )

        delta = (
            target_agix_value
            - current_agix_value
        )

        if delta == 0:

            return RebalanceCalculation(
                direction="NONE",

                amount_in_minor=0,

                min_amount_out_minor=0,

                current_acny_bps=(
                    current_acny_bps
                ),

                current_agix_bps=(
                    current_agix_bps
                ),

                target_acny_bps=(
                    target_acny_bps
                ),

                target_agix_bps=(
                    target_agix_bps
                ),

                projected_acny_bps=(
                    current_acny_bps
                ),

                projected_agix_bps=(
                    current_agix_bps
                ),

                notional_acny_minor=0,
            )

        if delta > 0:

            # ACNY -> AGIX
            #
            # Quantize to the exact
            # pool price to avoid losing
            # value through integer division.

            amount_acny = (
                delta // price
            ) * price

            if amount_acny <= 0:
                raise PortfolioCalculationError(
                    "REBALANCE_BELOW_MINIMUM"
                )

            amount_agix = (
                amount_acny
                // price
            )

            projected_agix_value = (
                current_agix_value
                + amount_acny
            )

            projected_acny = (
                balance_acny
                - amount_acny
            )

            direction = (
                "ACNY_TO_AGIX"
            )

            amount_in = (
                amount_acny
            )

            min_out = (
                amount_agix
            )

            notional = (
                amount_acny
            )

        else:

            # AGIX -> ACNY

            required_acny = (
                -delta
            )

            amount_agix = (
                required_acny
                // price
            )

            if amount_agix <= 0:
                raise PortfolioCalculationError(
                    "REBALANCE_BELOW_MINIMUM"
                )

            amount_acny = (
                amount_agix
                * price
            )

            projected_agix_value = (
                current_agix_value
                - amount_acny
            )

            projected_acny = (
                balance_acny
                + amount_acny
            )

            direction = (
                "AGIX_TO_ACNY"
            )

            amount_in = (
                amount_agix
            )

            min_out = (
                amount_acny
            )

            notional = (
                amount_acny
            )

        projected_total = (
            projected_acny
            + projected_agix_value
        )

        projected_acny_bps = (
            projected_acny
            * 10000
            // projected_total
        )

        projected_agix_bps = (
            projected_agix_value
            * 10000
            // projected_total
        )

        if (
            projected_acny_bps
            < min_acny_reserve_bps
        ):
            raise PortfolioCalculationError(
                "PROJECTED_RESERVE_VIOLATION"
            )

        return RebalanceCalculation(
            direction=direction,

            amount_in_minor=(
                amount_in
            ),

            min_amount_out_minor=(
                min_out
            ),

            current_acny_bps=(
                current_acny_bps
            ),

            current_agix_bps=(
                current_agix_bps
            ),

            target_acny_bps=(
                target_acny_bps
            ),

            target_agix_bps=(
                target_agix_bps
            ),

            projected_acny_bps=(
                projected_acny_bps
            ),

            projected_agix_bps=(
                projected_agix_bps
            ),

            notional_acny_minor=(
                notional
            ),
        )
