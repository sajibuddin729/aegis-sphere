import json
import uuid

from pathlib import Path

from aegis_api.domain.candidate_update import (
    CandidateUpdateProposal,
)

from aegis_api.services.candidate_policy import (
    CandidateUpdatePolicy,
)

from aegis_api.services.crypto import (
    canonical_hash,
)


class CandidateUpdateService:

    def __init__(
        self,
        *,
        repository,
        policy:
            CandidateUpdatePolicy,

        proposed_dir: Path,
        frozen_dir: Path,
    ):
        self.repository = repository
        self.policy = policy

        self.proposed_dir = (
            proposed_dir
        )

        self.frozen_dir = (
            frozen_dir
        )

        self.proposed_dir.mkdir(
            parents=True,
            exist_ok=True,
        )

        self.frozen_dir.mkdir(
            parents=True,
            exist_ok=True,
        )

    async def persist(
        self,
        *,
        source_match_id: str,
        proposal:
            CandidateUpdateProposal,
    ) -> tuple[str, str]:

        candidate_id = (
            "candidate_"
            + uuid.uuid4().hex
        )

        payload = (
            proposal.model_dump(
                mode="json"
            )
        )

        proposal_hash = (
            canonical_hash(
                payload
            )
        )

        policy_result = (
            self.policy.evaluate(
                proposal
            )
        )

        status = (
            policy_result.status
        )

        if status == "FROZEN":
            destination = (
                self.frozen_dir
            )

        else:
            destination = (
                self.proposed_dir
            )

        document = {
            "candidate_id":
                candidate_id,

            "source_match_id":
                source_match_id,

            "status":
                status,

            "policy_result":
                status,

            "frozen_reason":
                policy_result.reason,

            "proposal_hash":
                proposal_hash,

            "proposal":
                payload,

            "deployment_authorized":
                False
        }

        (
            destination
            / f"{candidate_id}.json"
        ).write_text(
            json.dumps(
                document,
                indent=2,
                sort_keys=True,
            ),
            encoding="utf-8",
        )

        await self.repository.save_candidate(
            candidate_id=(
                candidate_id
            ),

            source_match_id=(
                source_match_id
            ),

            proposal=proposal,

            proposal_hash=(
                proposal_hash
            ),

            status=status,

            policy_result=(
                status
            ),

            frozen_reason=(
                policy_result.reason
            ),
        )

        return (
            candidate_id,
            status,
        )
