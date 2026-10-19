from dataclasses import dataclass


@dataclass(frozen=True)
class PortfolioComplianceResult:
    approved: bool
    status: str
    reasons: list[str]


class PortfolioComplianceService:

    APPROVED_ASSETS = {
        "ACNY",
        "AGIX",
    }

    def evaluate(
        self,
        *,
        source_asset: str,
        target_asset: str,
        target_agix_bps: int,
    ) -> PortfolioComplianceResult:

        reasons: list[str] = []

        if (
            source_asset
            not in self.APPROVED_ASSETS
        ):
            reasons.append(
                "UNAPPROVED_SOURCE_ASSET"
            )

        if (
            target_asset
            not in self.APPROVED_ASSETS
        ):
            reasons.append(
                "UNAPPROVED_TARGET_ASSET"
            )

        if (
            target_agix_bps > 4000
        ):
            reasons.append(
                "GROWTH_ASSET_LIMIT_EXCEEDED"
            )

        approved = (
            len(reasons) == 0
        )

        return PortfolioComplianceResult(
            approved=approved,

            status=(
                "PASS"
                if approved
                else "BLOCK"
            ),

            reasons=reasons,
        )
