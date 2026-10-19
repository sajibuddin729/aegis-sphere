import re
from aegis_api.domain.agent import (
    ComplianceAssessment,
)


class ComplianceService:
    _email = re.compile(
        r"\b[A-Za-z0-9._%+-]+"
        r"@[A-Za-z0-9.-]+\."
        r"[A-Za-z]{2,}\b"
    )
    _phone = re.compile(
        r"\b\d{10,15}\b"
    )

    def evaluate(
        self,
        *,
        purpose: str,
        asset: str,
        recipient_approved: bool,
    ) -> ComplianceAssessment:
        reasons: list[str] = []
        pii = bool(
            self._email.search(purpose)
            or self._phone.search(purpose)
        )
        if pii:
            reasons.append(
                "PII_IN_TRANSACTION_PURPOSE"
            )

        if asset != "ACNY":
            reasons.append(
                "UNAPPROVED_ASSET"
            )

        if not recipient_approved:
            reasons.append(
                "RECIPIENT_NOT_APPROVED"
            )

        approved = len(reasons) == 0
        return ComplianceAssessment(
            approved=approved,
            pii_exposure=pii,
            sensitive_data_exposure=False,
            cross_border_data_flag=False,
            policy_status=(
                "PASS" if approved else "BLOCK"
            ),
            reasons=reasons,
        )
