import re
from decimal import Decimal


class PlanValidationError(RuntimeError):
    pass


class PlanValidator:
    _amount_pattern = re.compile(
        r"(\d+(?:\.\d+)?)\s*ACNY",
        re.IGNORECASE,
    )

    def validate_amount(
        self,
        *,
        original_request: str,
        planned_minor: int | None,
    ) -> None:
        match = self._amount_pattern.search(original_request)
        if match is None:
            return

        value = Decimal(match.group(1))
        expected = int(value * Decimal("100"))
        if planned_minor != expected:
            raise PlanValidationError(
                "AI_PLAN_AMOUNT_MISMATCH"
            )
