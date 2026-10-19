from aegis_api.services.regression_detector import (
    RegressionDetector,
)


def test_small_drop_is_not_catastrophic():

    result = (
        RegressionDetector()
        .detect(
            baseline_score=100,
            observed_score=95,
            critical_violation=False,
        )
    )

    assert not result.detected


def test_large_drop_is_regression():

    result = (
        RegressionDetector()
        .detect(
            baseline_score=100,
            observed_score=85,
            critical_violation=False,
        )
    )

    assert result.detected
    assert result.severity == "HIGH"


def test_critical_violation_always_regression():

    result = (
        RegressionDetector()
        .detect(
            baseline_score=100,
            observed_score=100,
            critical_violation=True,
        )
    )

    assert result.detected
    assert (
        result.severity
        == "CRITICAL"
    )
