from dataclasses import dataclass


@dataclass(frozen=True)
class AutonomousRisk:
    score_bps: int
    risk_class: str
    components: dict[str, int]


class AutonomousRiskService:
    def __init__(
        self,
        *,
        max_transaction_minor: int,
    ):
        self.max_transaction_minor = (
            max_transaction_minor
        )

    def score(
        self,
        *,
        amount_minor: int,
        market_snapshot: dict,
        recipient_approved: bool,
        security_severity: str,
    ) -> AutonomousRisk:
        base = 150
        amount_component = min(
            800,
            int(
                (amount_minor / self.max_transaction_minor)
                * 800
            ),
        )
        volatility_component = min(
            1200,
            int(
                market_snapshot["volatility_bps"] / 2
            ),
        )
        liquidity_score = int(
            market_snapshot["liquidity_score"]
        )
        liquidity_component = min(
            600,
            max(
                0,
                (70 - liquidity_score) * 15,
            ),
        )
        recipient_component = (
            0 if recipient_approved else 800
        )
        security_component = {
            "SAFE": 0,
            "CAUTION": 600,
            "CRITICAL": 5000,
        }.get(security_severity, 2000)

        components = {
            "base": base,
            "amount": amount_component,
            "market_volatility": volatility_component,
            "liquidity": liquidity_component,
            "recipient": recipient_component,
            "security": security_component,
        }

        total = min(
            10000,
            sum(components.values()),
        )

        if total <= 750:
            risk_class = "LOW"
        elif total <= 1800:
            risk_class = "MEDIUM"
        else:
            risk_class = "HIGH"

        return AutonomousRisk(
            score_bps=total,
            risk_class=risk_class,
            components=components,
        )
