from dataclasses import dataclass


@dataclass(frozen=True)
class RegressionDecision:

    detected: bool

    severity: str | None

    delta: float


class RegressionDetector:

    def detect(
        self,
        *,
        baseline_score: float,
        observed_score: float,
        critical_violation: bool,
    ) -> RegressionDecision:

        delta = round(
            observed_score
            - baseline_score,
            2,
        )

        if critical_violation:

            return RegressionDecision(
                detected=True,
                severity="CRITICAL",
                delta=delta,
            )

        if delta <= -20:

            return RegressionDecision(
                detected=True,
                severity="CRITICAL",
                delta=delta,
            )

        if delta <= -10:

            return RegressionDecision(
                detected=True,
                severity="HIGH",
                delta=delta,
            )

        return RegressionDecision(
            detected=False,
            severity=None,
            delta=delta,
        )
