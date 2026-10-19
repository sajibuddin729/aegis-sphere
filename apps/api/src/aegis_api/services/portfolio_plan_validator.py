import re
from decimal import Decimal

from aegis_api.domain.portfolio import (
    PortfolioAIPlan,
)


class PortfolioPlanValidationError(
    RuntimeError
):
    pass


class PortfolioPlanValidator:

    _agix_pattern = re.compile(
        r"AGIX[^0-9]{0,30}"
        r"(\d+(?:\.\d+)?)%",
        re.IGNORECASE,
    )

    _reserve_pattern = re.compile(
        r"(?:at least|minimum|keep)"
        r"[^0-9]{0,30}"
        r"(\d+(?:\.\d+)?)%"
        r"[^.]{0,40}"
        r"ACNY",
        re.IGNORECASE,
    )

    def validate(
        self,
        *,
        original_request: str,
        plan: PortfolioAIPlan,
    ) -> None:

        if (
            plan.target_acny_bps
            + plan.target_agix_bps
            != 10000
        ):
            raise PortfolioPlanValidationError(
                "ALLOCATION_SUM_INVALID"
            )

        target_match = (
            self._agix_pattern.search(
                original_request
            )
        )

        if target_match:

            expected = int(
                Decimal(
                    target_match.group(1)
                )
                * Decimal("100")
            )

            if (
                plan.target_agix_bps
                != expected
            ):
                raise PortfolioPlanValidationError(
                    "AI_TARGET_ALLOCATION_MISMATCH"
                )

        reserve_match = (
            self._reserve_pattern.search(
                original_request
            )
        )

        if reserve_match:

            expected_reserve = int(
                Decimal(
                    reserve_match.group(1)
                )
                * Decimal("100")
            )

            if (
                plan
                .min_acny_reserve_bps
                < expected_reserve
            ):
                raise PortfolioPlanValidationError(
                    "AI_RESERVE_REQUIREMENT_MISMATCH"
                )

        if (
            plan.target_agix_bps
            > 4000
        ):
            raise PortfolioPlanValidationError(
                "AGIX_CONCENTRATION_LIMIT"
            )

        if (
            plan.target_acny_bps
            < plan.min_acny_reserve_bps
        ):
            raise PortfolioPlanValidationError(
                "MIN_ACNY_RESERVE_VIOLATION"
            )
