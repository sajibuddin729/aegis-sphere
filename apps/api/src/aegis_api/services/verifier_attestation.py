import secrets
import time

from aegis_api.domain.verification import (
    VerificationAttestation,
)

from aegis_api.services.crypto import (
    canonical_hash,
)


class VerifierAttestationService:

    VERIFIER_VERSION = (
        "aegis-independent-verifier-v1"
    )

    def build(
        self,
        *,
        verification_id: str,
        candidate_id: str,
        candidate_hash: str,
        report_hash: str,
        decision: str,
    ):

        attestation = (
            VerificationAttestation(
                schema_version=(
                    "aegis-verifier-"
                    "attestation-v1"
                ),

                verification_id=(
                    verification_id
                ),

                candidate_id=(
                    candidate_id
                ),

                candidate_hash=(
                    candidate_hash
                ),

                report_hash=(
                    report_hash
                ),

                verifier_version=(
                    self
                    .VERIFIER_VERSION
                ),

                decision=decision,

                attestation_nonce=(
                    "0x"
                    + secrets.token_hex(
                        32
                    )
                ),

                timestamp=int(
                    time.time()
                ),
            )
        )

        attestation_hash = (
            canonical_hash(
                attestation
                .model_dump(
                    mode="json"
                )
            )
        )

        return (
            attestation,
            attestation_hash,
        )
