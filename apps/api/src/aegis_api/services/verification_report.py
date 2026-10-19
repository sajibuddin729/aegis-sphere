import json

from pathlib import Path

from aegis_api.services.crypto import (
    canonical_hash,
)


class VerificationReportStore:

    def __init__(
        self,
        directory: Path,
    ):
        self.directory = (
            directory
        )

        self.directory.mkdir(
            parents=True,
            exist_ok=True,
        )

    def persist(
        self,
        report,
    ) -> str:

        payload = (
            report.model_dump(
                mode="json"
            )
        )

        report_hash = (
            canonical_hash(
                payload
            )
        )

        path = (
            self.directory
            / (
                report_hash
                .removeprefix("0x")
                + ".json"
            )
        )

        path.write_text(
            json.dumps(
                payload,
                indent=2,
                sort_keys=True,
            ),
            encoding="utf-8",
        )

        return report_hash
