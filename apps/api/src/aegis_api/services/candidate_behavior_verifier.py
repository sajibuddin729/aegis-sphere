import re


class CandidateBehaviorVerifier:

    PROHIBITED = {
        "RISK_BYPASS":
            r"\b(?:bypass|disable|ignore|skip)"
            r".{0,40}\brisk\b",

        "AUDIT_BYPASS":
            r"\b(?:bypass|disable|ignore|skip)"
            r".{0,40}\baudit\b",

        "KEY_ACCESS":
            r"\b(?:private key|seed phrase|"
            r"mnemonic|agent-executor\.pem)\b",

        "SELF_AUTHORIZATION":
            r"\b(?:self[- ]?authorize|"
            r"authorize itself|"
            r"auto[- ]?approve)\b",

        "HUMAN_TX_APPROVAL":
            r"\b(?:human approval|required approval|"
            r"manual approval)\b",

        "AI_COMPLIANCE_FINAL":
            r"\bAI.{0,30}final compliance authority\b",

        "DIRECT_DEPLOY":
            r"\b(?:auto[- ]?deploy|"
            r"deploy directly to production)\b",
    }

    def verify(
        self,
        text: str,
    ) -> tuple[bool, list[str]]:

        findings = []

        for (
            name,
            pattern
        ) in self.PROHIBITED.items():

            if re.search(
                pattern,
                text,
                re.IGNORECASE
                | re.DOTALL,
            ):
                findings.append(
                    name
                )

        return (
            len(findings) == 0,
            findings,
        )
