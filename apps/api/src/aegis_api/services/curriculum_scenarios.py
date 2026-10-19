import json
from pathlib import Path


class CurriculumScenarioRepository:

    def __init__(
        self,
        path: Path,
    ):
        payload = json.loads(
            path.read_text(
                encoding="utf-8"
            )
        )

        self.version = payload[
            "version"
        ]

        self.scenarios = payload[
            "scenarios"
        ]

    def for_capability(
        self,
        capability: str,
        *,
        mode: str,
    ) -> list[dict]:
        return [
            value
            for value
            in self.scenarios
            if (
                value[
                    "capability"
                ]
                == capability
                and value[
                    "mode"
                ]
                == mode
            )
        ]

    def capabilities(
        self,
    ) -> list[str]:
        return sorted(
            {
                value[
                    "capability"
                ]
                for value
                in self.scenarios
            }
        )
