import httpx


class GovernanceUnavailable(RuntimeError):
    pass


class GovernanceClient:

    def __init__(
        self,
        *,
        base_url: str,
        key: str,
    ):
        self.base_url = base_url.rstrip("/")
        self.key = key

    async def record(
        self,
        *,
        candidate_hash: str,
        report_hash: str,
        attestation_hash: str,
        verifier_version_hash: str,
        decision_value: int,
    ) -> dict:
        try:
            async with httpx.AsyncClient(timeout=30.0) as client:
                res = await client.post(
                    f"{self.base_url}/governance/decision",
                    headers={
                        "X-Aegis-Governance-Key": self.key,
                        "Content-Type": "application/json",
                    },
                    json={
                        "candidateHash": candidate_hash,
                        "reportHash": report_hash,
                        "attestationHash": attestation_hash,
                        "verifierVersionHash": verifier_version_hash,
                        "decisionValue": decision_value,
                    },
                )
                if res.status_code == 422:
                    return res.json()
                res.raise_for_status()
                return res.json()
        except (httpx.ConnectError, httpx.TimeoutException, httpx.NetworkError) as exc:
            raise GovernanceUnavailable(f"GOVERNANCE_SIGNER_UNAVAILABLE: {exc}") from exc
        except httpx.HTTPStatusError as exc:
            if exc.response.status_code == 503:
                raise GovernanceUnavailable(f"GOVERNANCE_SIGNER_UNAVAILABLE: {exc}") from exc
            raise

    async def get_decision(
        self,
        candidate_hash: str,
    ) -> dict:
        try:
            async with httpx.AsyncClient(timeout=30.0) as client:
                res = await client.get(
                    f"{self.base_url}/governance/decision/{candidate_hash}",
                    headers={
                        "X-Aegis-Governance-Key": self.key,
                    },
                )
                res.raise_for_status()
                return res.json()
        except (httpx.ConnectError, httpx.TimeoutException, httpx.NetworkError) as exc:
            raise GovernanceUnavailable(f"GOVERNANCE_SIGNER_UNAVAILABLE: {exc}") from exc
