from aegis_api.services.autonomous_risk import (
    AutonomousRiskService,
)


def test_normal_risk_is_below_onchain_limit():
    service = AutonomousRiskService(
        max_transaction_minor=50_000
    )
    result = service.score(
        amount_minor=2_000,
        market_snapshot={
            "volatility_bps": 600,
            "liquidity_score": 88,
        },
        recipient_approved=True,
        security_severity="SAFE",
    )
    assert result.score_bps < 2500


def test_critical_security_is_high_risk():
    service = AutonomousRiskService(
        max_transaction_minor=50_000
    )
    result = service.score(
        amount_minor=2_000,
        market_snapshot={
            "volatility_bps": 600,
            "liquidity_score": 88,
        },
        recipient_approved=True,
        security_severity="CRITICAL",
    )
    assert result.score_bps > 2500
