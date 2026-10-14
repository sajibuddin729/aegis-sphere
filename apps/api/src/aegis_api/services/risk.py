from dataclasses import dataclass


@dataclass(frozen=True)
class RiskAssessment:
    approved: bool
    risk_class: str
    reason: str


class RiskService:
    def __init__(
        self,
        *,
        max_transaction_minor: int,
        max_daily_minor: int,
        max_risk_bps: int,
    ):
        self.max_transaction_minor = (
            max_transaction_minor
        )

        self.max_daily_minor = (
            max_daily_minor
        )

        self.max_risk_bps = (
            max_risk_bps
        )

    def evaluate(
        self,
        *,
        amount_minor: int,
        risk_score_bps: int,
        spent_today_minor: int,
    ) -> RiskAssessment:

        if (
            amount_minor
            > self.max_transaction_minor
        ):
            return RiskAssessment(
                approved=False,
                risk_class="CRITICAL",
                reason="TX_LIMIT_EXCEEDED",
            )

        if (
            risk_score_bps
            > self.max_risk_bps
        ):
            return RiskAssessment(
                approved=False,
                risk_class="HIGH",
                reason="RISK_TOO_HIGH",
            )

        if (
            spent_today_minor
            + amount_minor
            > self.max_daily_minor
        ):
            return RiskAssessment(
                approved=False,
                risk_class="HIGH",
                reason="DAILY_LIMIT_EXCEEDED",
            )

        if risk_score_bps <= 500:
            risk_class = "LOW"

        elif risk_score_bps <= 1500:
            risk_class = "MEDIUM"

        else:
            risk_class = "HIGH"

        return RiskAssessment(
            approved=True,
            risk_class=risk_class,
            reason="RISK_ACCEPTABLE",
        )
