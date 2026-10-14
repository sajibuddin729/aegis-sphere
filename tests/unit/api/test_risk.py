from aegis_api.services.risk import (
    RiskService,
)


def service():
    return RiskService(
        max_transaction_minor=50_000,
        max_daily_minor=200_000,
        max_risk_bps=2_500,
    )


def test_safe_transaction_allowed():
    result = service().evaluate(
        amount_minor=1_500,
        risk_score_bps=600,
        spent_today_minor=1_000,
    )

    assert result.approved is True


def test_high_risk_rejected():
    result = service().evaluate(
        amount_minor=1_000,
        risk_score_bps=3_000,
        spent_today_minor=0,
    )

    assert result.approved is False
    assert result.reason == "RISK_TOO_HIGH"


def test_single_limit_rejected():
    result = service().evaluate(
        amount_minor=60_000,
        risk_score_bps=500,
        spent_today_minor=0,
    )

    assert result.approved is False
    assert (
        result.reason
        == "TX_LIMIT_EXCEEDED"
    )


def test_daily_limit_rejected():
    result = service().evaluate(
        amount_minor=10_000,
        risk_score_bps=500,
        spent_today_minor=195_000,
    )

    assert result.approved is False
    assert (
        result.reason
        == "DAILY_LIMIT_EXCEEDED"
    )
