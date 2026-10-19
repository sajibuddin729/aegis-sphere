from typing import Any

from aegis_api.clients.bridge import (
    FiscoBridgeClient,
)


class AegisWalletProvider:
    def __init__(
        self,
        bridge: FiscoBridgeClient,
    ):
        self.bridge = bridge

    async def get_state(
        self,
    ) -> dict[str, Any]:
        return await self.bridge.wallet_state()

    async def get_balance(
        self,
    ) -> dict[str, Any]:
        return await self.bridge.wallet_balance()

    async def get_daily_spent(
        self,
    ) -> dict[str, Any]:
        return await self.bridge.spent_today()

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

        return await self.bridge.transfer(
            recipient=recipient,
            amount_minor=amount_minor,
            poi_hash=poi_hash,
            intent_id=intent_id,
            policy_hash=policy_hash,
            risk_score_bps=risk_score_bps,
            expiry=expiry,
        )
