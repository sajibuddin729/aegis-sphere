import json
from pathlib import Path
from typing import Any


class MarketDataService:
    def __init__(
        self,
        path: Path,
    ):
        self.path = path

    def snapshot(
        self,
    ) -> dict[str, Any]:
        with self.path.open(
            "r",
            encoding="utf-8",
        ) as handle:
            return json.load(handle)
