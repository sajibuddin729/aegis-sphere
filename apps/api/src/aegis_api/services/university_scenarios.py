import json
from pathlib import Path

from aegis_api.services.crypto import (
    canonical_hash,
)


class UniversityScenarioRepository:
    def __init__(
        self,
        path: Path,
    ):
        payload = json.loads(
            path.read_text(
                encoding="utf-8"
            )
        )
        self.version = payload["version"]
        self.scenarios = {
            item["scenario_id"]: item
            for item in payload["scenarios"]
        }

    def get(
        self,
        scenario_id: str,
    ) -> dict:
        if scenario_id not in self.scenarios:
            raise KeyError(scenario_id)
        return self.scenarios[scenario_id]

    def list(self):
        return list(self.scenarios.values())

    def hash(
        self,
        scenario: dict,
    ) -> str:
        return canonical_hash(scenario)
