import json

from pathlib import Path

from aegis_api.services.crypto import (
    canonical_hash,
)


class CandidateIntakeError(
    RuntimeError
):
    pass


class CandidateIntakeService:

    REQUIRED_FIELDS = {
        "candidate_id",
        "status",
        "proposal_hash",
        "proposal",
        "deployment_authorized",
    }

    def __init__(
        self,
        *,
        candidate_dirs:
            list[Path],
    ):
        self.candidate_dirs = (
            candidate_dirs
        )

    def load(
        self,
        candidate_id: str,
    ) -> tuple[dict, str]:

        for directory in (
            self.candidate_dirs
        ):

            path = (
                directory
                / f"{candidate_id}.json"
            )

            if path.exists():

                payload = json.loads(
                    path.read_text(
                        encoding="utf-8"
                    )
                )

                missing = (
                    self.REQUIRED_FIELDS
                    - set(
                        payload.keys()
                    )
                )

                if missing:
                    raise CandidateIntakeError(
                        "MISSING_FIELDS:"
                        + ",".join(
                            sorted(
                                missing
                            )
                        )
                    )

                if (
                    payload[
                        "deployment_authorized"
                    ]
                    is not False
                ):
                    raise CandidateIntakeError(
                        "SELF_AUTHORIZED_CANDIDATE"
                    )

                proposal_hash = (
                    canonical_hash(
                        payload[
                            "proposal"
                        ]
                    )
                )

                if (
                    proposal_hash
                    != payload[
                        "proposal_hash"
                    ]
                ):
                    raise CandidateIntakeError(
                        "PROPOSAL_HASH_MISMATCH"
                    )

                candidate_hash = (
                    payload.get("candidate_hash")
                    or canonical_hash(
                        payload
                    )
                )

                return (
                    payload,
                    candidate_hash,
                )

        raise CandidateIntakeError(
            "CANDIDATE_NOT_FOUND"
        )
