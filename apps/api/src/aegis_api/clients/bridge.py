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
    ) -> dict[str, Any]:

        return await self._request(
            "POST",
            "/wallet/transfer",
            json={
                "recipient": recipient,
                "amountMinor": str(amount_minor),
                "poiHash": poi_hash,
                "intentId": intent_id,
                "policyHash": policy_hash,
                "riskScoreBps": str(risk_score_bps),
            },
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
