from dataclasses import dataclass


@dataclass(frozen=True)
class PortfolioRiskResult:
    score_bps: int
    risk_class: str
    components: dict[str, int]


class PortfolioRiskService:

    def __init__(
        self,
        *,
        max_trade_notional: int,
    ):
        self.max_trade_notional = (
            max_trade_notional
        )

    def score(
        self,
        *,
        trade_notional: int,
        allocation_shift_bps: int,
        market_snapshot: dict,
        security_severity: str,
    ) -> PortfolioRiskResult:

        base = 150

        trade_size = min(
            800,
            int(
                trade_notional
                / self.max_trade_notional
                * 800
            ),
        )

        volatility = min(
            1200,
            int(
                market_snapshot[
                    "volatility_bps"
                ]
                / 2
            ),
        )

        liquidity_score = int(
            market_snapshot[
                "liquidity_score"
            ]
        )

        liquidity = min(
            600,
            max(
                0,
                (
                    70
                    - liquidity_score
                )
                * 15,
            ),
        )

        allocation_shift = min(
            600,
            allocation_shift_bps
            // 5,
        )

        security = {
            "SAFE": 0,
            "CAUTION": 600,
            "CRITICAL": 5000,
        }.get(
            security_severity,
            2000,
        )

        components = {
            "base": base,
            "trade_size": trade_size,
            "market_volatility": volatility,
            "liquidity": liquidity,
            "allocation_shift": allocation_shift,
            "security": security,
        }

        score = min(
            10000,
            sum(
                components.values()
            ),
        )

        if score <= 750:
            risk_class = "LOW"

        elif score <= 1800:
            risk_class = "MEDIUM"

        else:
            risk_class = "HIGH"

        return PortfolioRiskResult(
            score_bps=score,
            risk_class=risk_class,
            components=components,
        )
