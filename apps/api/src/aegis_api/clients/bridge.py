from typing import Any

import httpx


class BridgeError(RuntimeError):
    def __init__(
        self,
        message: str,
        *,
        status_code: int | None = None,
        payload: dict[str, Any] | None = None,
    ):
        super().__init__(message)

        self.status_code = status_code
        self.payload = payload or {}


class FiscoBridgeClient:
    def __init__(
        self,
        *,
        base_url: str,
        bridge_key: str,
        timeout_seconds: float = 20.0,
    ):
        self._base_url = base_url.rstrip("/")

        self._headers = {
            "X-Aegis-Bridge-Key": bridge_key,
        }

        self._timeout = timeout_seconds

    async def health(self) -> dict[str, Any]:
        return await self._request(
            "GET",
            "/health",
            authenticated=False,
        )

    async def wallet_state(
        self,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            "/wallet/state",
        )

    async def wallet_balance(
        self,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            "/wallet/balance",
        )

    async def spent_today(
        self,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            "/risk/spent",
        )

    async def poi(
        self,
        poi_hash: str,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            f"/poi/{poi_hash}",
        )

    async def transaction(
        self,
        tx_hash: str,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            f"/transaction/{tx_hash}",
        )

    async def transfer(
        self,
        *,
        recipient: str,
        amount_minor: int,
        poi_hash: str,
        intent_id: str,
        policy_hash: str,
        risk_score_bps: int,
        expiry: int | None = None,
    ) -> dict[str, Any]:

        payload: dict[str, Any] = {
            "recipient": recipient,
            "amountMinor": str(amount_minor),
            "poiHash": poi_hash,
            "intentId": intent_id,
            "policyHash": policy_hash,
            "riskScoreBps": str(risk_score_bps),
        }
        if expiry is not None:
            payload["expiry"] = expiry

        return await self._request(
            "POST",
            "/wallet/transfer",
            json=payload,
        )

    async def portfolio_state(
        self,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            "/portfolio/state",
        )

    async def portfolio_rebalance(
        self,
        *,
        direction: str,
        amount_in_minor: int,
        min_amount_out_minor: int,
        poi_hash: str,
        intent_id: str,
        policy_hash: str,
        risk_score_bps: int,
        expiry: int | None = None,
    ) -> dict[str, Any]:
        payload: dict[str, Any] = {
            "direction": direction,
            "amountInMinor": str(amount_in_minor),
            "minAmountOutMinor": str(min_amount_out_minor),
            "poiHash": poi_hash,
            "intentId": intent_id,
            "policyHash": policy_hash,
            "riskScoreBps": str(risk_score_bps),
        }
        if expiry is not None:
            payload["expiry"] = expiry

        return await self._request(
            "POST",
            "/portfolio/rebalance",
            json=payload,
        )

    async def audit_record(
        self,
        *,
        evidence_hash: str,
        policy_version_hash: str,
        retrieval_set_hash: str,
        decision_hash: str,
        related_poi_hash: str,
    ) -> dict[str, Any]:
        return await self._request(
            "POST",
            "/audit/evidence",
            json={
                "evidenceHash": evidence_hash,
                "policyVersionHash": policy_version_hash,
                "retrievalSetHash": retrieval_set_hash,
                "decisionHash": decision_hash,
                "relatedPoIHash": related_poi_hash,
            },
        )

    async def audit_evidence(
        self,
        evidence_hash: str,
    ) -> dict[str, Any]:
        return await self._request(
            "GET",
            f"/audit/evidence/{evidence_hash}",
        )

    async def _request(
        self,
        method: str,
        path: str,
        *,
        json: dict[str, Any] | None = None,
        authenticated: bool = True,
    ) -> dict[str, Any]:

        headers = (
            self._headers
            if authenticated
            else {}
        )

        try:
            async with httpx.AsyncClient(
                timeout=self._timeout,
            ) as client:

                response = await client.request(
                    method,
                    self._base_url + path,
                    headers=headers,
                    json=json,
                )

        except httpx.RequestError as exc:
            raise BridgeError(
                f"FISCO bridge unavailable: {exc}"
            ) from exc

        try:
            payload = response.json()
        except ValueError:
            payload = {
                "raw": response.text
            }

        if response.status_code >= 400:
            raise BridgeError(
                payload.get(
                    "message",
                    payload.get(
                        "error",
                        "Bridge request failed",
                    ),
                ),
                status_code=response.status_code,
                payload=payload,
            )

        return payload
