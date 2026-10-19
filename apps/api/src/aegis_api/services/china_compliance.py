import json
import re
from pathlib import Path

from aegis_api.domain.compliance import (
    ChinaComplianceResult,
)


class ChinaComplianceEngine:

    EMAIL = re.compile(
        r"\b[A-Za-z0-9._%+-]+"
        r"@[A-Za-z0-9.-]+\."
        r"[A-Za-z]{2,}\b"
    )

    PHONE = re.compile(
        r"(?<!\d)"
        r"(?:\+?86[- ]?)?"
        r"1[3-9]\d{9}"
        r"(?!\d)"
    )

    ID_NUMBER = re.compile(
        r"(?<!\d)"
        r"\d{17}[\dXx]"
        r"(?!\d)"
    )

    BANK_CARD = re.compile(
        r"(?<!\d)"
        r"\d{16,19}"
        r"(?!\d)"
    )

    PRIVATE_KEY = re.compile(
        r"\b(?:private\s*key|"
        r"seed\s*phrase|"
        r"mnemonic)\b",
        re.IGNORECASE,
    )

    CROSS_BORDER = re.compile(
        r"\b("
        r"cross[- ]border|"
        r"outside china|"
        r"overseas|"
        r"境外|"
        r"跨境"
        r")\b",
        re.IGNORECASE,
    )

    IMPORTANT_DATA = re.compile(
        r"\b("
        r"important data|"
        r"重要数据"
        r")\b",
        re.IGNORECASE,
    )

    def __init__(
        self,
        *,
        policy_path: Path,
        rules_path: Path,
    ):
        self.policy = json.loads(
            policy_path.read_text(
                encoding="utf-8"
            )
        )
        self.rules = json.loads(
            rules_path.read_text(
                encoding="utf-8"
            )
        )
        self.policy_version = (
            self.rules[
                "policy_version"
            ]
        )
        self.aml_patterns = [
            value.lower()
            for value in self.rules[
                "rules"
            ][
                "aml_prototype_patterns"
            ]
        ]

    def evaluate(
        self,
        *,
        instruction: str,
        purpose: str = "",
        asset: str | None = None,
        recipient_approved: bool = True,
        cross_border_authorized: bool = False,
        important_data_authorized: bool = False,
    ) -> ChinaComplianceResult:

        combined = (
            instruction
            + "\n"
            + purpose
        )
        lower = combined.lower()

        triggered: list[str] = []
        sources: set[str] = set()

        email = bool(
            self.EMAIL.search(
                combined
            )
        )
        phone = bool(
            self.PHONE.search(
                combined
            )
        )
        id_number = bool(
            self.ID_NUMBER.search(
                combined
            )
        )
        bank_card = bool(
            self.BANK_CARD.search(
                combined
            )
        )

        pii = (
            email
            or phone
            or id_number
            or bank_card
        )

        sensitive_identity = (
            id_number
            or bank_card
        )

        private_key = bool(
            self.PRIVATE_KEY.search(
                combined
            )
        )

        cross_border = bool(
            self.CROSS_BORDER.search(
                combined
            )
        )

        important_data = bool(
            self.IMPORTANT_DATA.search(
                combined
            )
        )

        aml = any(
            pattern in lower
            for pattern in self.aml_patterns
        )

        if pii:
            triggered.append(
                "RAW_PII_IN_FINANCIAL_CONTEXT"
            )
            sources.update(
                {
                    "CN-PIPL",
                    "CN-NDSR",
                }
            )

        if sensitive_identity:
            triggered.append(
                "SENSITIVE_IDENTITY_DATA"
            )
            sources.add(
                "CN-PIPL"
            )

        if private_key:
            triggered.append(
                "PRIVATE_KEY_DISCLOSURE_REQUEST"
            )
            sources.add(
                "CN-CSL"
            )

        if (
            cross_border
            and pii
            and not cross_border_authorized
        ):
            triggered.append(
                "CROSS_BORDER_PII_NOT_AUTHORIZED"
            )
            sources.update(
                {
                    "CN-PIPL",
                    "CN-CROSSBORDER",
                }
            )

        if (
            important_data
            and not important_data_authorized
        ):
            triggered.append(
                "IMPORTANT_DATA_NOT_AUTHORIZED"
            )
            sources.update(
                {
                    "CN-DSL",
                    "CN-NDSR",
                    "CN-NDSRA-2026",
                }
            )

        if aml:
            triggered.append(
                "AML_SUSPICIOUS_INSTRUCTION_PATTERN"
            )
            sources.add(
                "CN-AML"
            )

        if (
            asset is not None
            and asset
            not in {
                "ACNY",
                "AGIX",
            }
        ):
            triggered.append(
                "UNAPPROVED_ASSET"
            )

        if not recipient_approved:
            triggered.append(
                "UNAPPROVED_RECIPIENT"
            )

        decision = (
            "PASS"
            if not triggered
            else "BLOCK"
        )

        return ChinaComplianceResult(
            decision=decision,
            policy_version=(
                self.policy_version
            ),
            triggered_rules=(
                triggered
            ),
            source_refs=sorted(
                sources
            ),
            pii_detected=pii,
            sensitive_identity_detected=(
                sensitive_identity
            ),
            cross_border_flag=(
                cross_border
            ),
            important_data_flag=(
                important_data
            ),
            aml_pattern_flag=aml,
            explanation=(
                "No blocking rule triggered."
                if decision == "PASS"
                else "Transaction blocked by "
                "versioned automated "
                "compliance policy."
            ),
        )
