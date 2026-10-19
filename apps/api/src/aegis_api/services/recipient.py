import json
from pathlib import Path


class RecipientResolver:
    def __init__(
        self,
        path: Path,
    ):
        with path.open(
            "r",
            encoding="utf-8",
        ) as handle:
            self.records = json.load(handle)

    def prompt_directory(
        self,
    ) -> dict:
        return self.records

    def resolve_address(
        self,
        identifier: str,
    ) -> str | None:
        if not identifier:
            return None
        normalized = identifier.strip().lower()
        # Direct address match
        for record in self.records.values():
            if record["address"].lower() == normalized:
                return record["address"]
        # Key match (e.g. "supplier_d")
        if normalized in self.records:
            return self.records[normalized]["address"]
        # Name match (e.g. "supplier d")
        for record in self.records.values():
            if record["name"].strip().lower() == normalized:
                return record["address"]
        return None

    def approved_address(
        self,
        address: str,
    ) -> bool:
        normalized = address.strip().lower()
        for record in self.records.values():
            if (
                record["approved"]
                and (
                    record["address"].lower() == normalized
                    or record["name"].strip().lower() == normalized
                )
            ):
                return True
        return False
