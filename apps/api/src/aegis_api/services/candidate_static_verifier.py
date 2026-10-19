import re

from dataclasses import dataclass


@dataclass(frozen=True)
class StaticCheck:

    passed: bool
    findings: list[str]


class CandidateStaticVerifier:

    SECRET_PATTERNS = [
        r"BEGIN PRIVATE KEY",
        r"agent-executor\.pem",
        r"FISCO_ACCOUNT_PEM",
        r"AEGIS_BRIDGE_KEY",
        r"\bmnemonic\b",
        r"\bseed phrase\b",
    ]

    SECURITY_BYPASS = [
        r"\bdisable .*risk\b",
        r"\bbypass .*risk\b",
        r"\bskip .*audit\b",
        r"\bbypass .*audit\b",
        r"\bdisable .*guard\b",
        r"\bremove .*guard\b",
        r"\bself[- ]?authorize\b",
        r"\bauto[- ]?deploy\b",
        r"\bdirect.*sign\b",
    ]

    NETWORK_PATTERNS = [
        r"https?://",
        r"\bcurl\b",
        r"\bwget\b",
        r"\bsocket\b",
        r"\brequests\.",
        r"\bhttpx\.",
    ]

    def scan_secrets(
        self,
        text: str,
    ) -> StaticCheck:

        findings = [
            pattern

            for pattern
            in self.SECRET_PATTERNS

            if re.search(
                pattern,
                text,
                re.IGNORECASE,
            )
        ]

        return StaticCheck(
            passed=not findings,
            findings=findings,
        )

    def scan_capabilities(
        self,
        text: str,
    ) -> StaticCheck:

        findings = [
            pattern

            for pattern
            in self.SECURITY_BYPASS

            if re.search(
                pattern,
                text,
                re.IGNORECASE,
            )
        ]

        return StaticCheck(
            passed=not findings,
            findings=findings,
        )

    def scan_network(
        self,
        text: str,
    ) -> StaticCheck:

        findings = [
            pattern

            for pattern
            in self.NETWORK_PATTERNS

            if re.search(
                pattern,
                text,
                re.IGNORECASE,
            )
        ]

        return StaticCheck(
            passed=not findings,
            findings=findings,
        )
