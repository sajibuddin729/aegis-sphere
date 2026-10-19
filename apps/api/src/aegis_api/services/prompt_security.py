import re


class PromptSecurityGate:
    _patterns = [
        r"ignore .*previous",
        r"ignore .*security",
        r"bypass .*security",
        r"disable .*risk",
        r"disable .*policy",
        r"ignore .*policy",
        r"transfer .*all .*fund",
        r"send .*entire .*balance",
        r"reveal .*private .*key",
        r"show .*private .*key",
        r"override .*system",
    ]

    def inspect(
        self,
        text: str,
    ) -> list[str]:
        normalized = text.lower().strip()
        hits: list[str] = []
        for pattern in self._patterns:
            if re.search(pattern, normalized):
                hits.append(pattern)
        return hits
