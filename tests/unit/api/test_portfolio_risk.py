from aegis_api.services.portfolio_risk import (
    PortfolioRiskService,
)


def test_normal_rebalance_under_limit():

    service = PortfolioRiskService(
        max_trade_notional=50000
    )

    result = service.score(
        trade_notional=40000,

        allocation_shift_bps=400,

        market_snapshot={
            "volatility_bps": 600,
            "liquidity_score": 88,
        },

        security_severity="SAFE",
    )

    assert result.score_bps < 2500


def test_critical_security_is_rejected():

    service = PortfolioRiskService(
        max_trade_notional=50000
    )

    result = service.score(
        trade_notional=10000,

        allocation_shift_bps=100,

        market_snapshot={
            "volatility_bps": 600,
            "liquidity_score": 88,
        },

        security_severity="CRITICAL",
    )

    assert result.score_bps > 2500
