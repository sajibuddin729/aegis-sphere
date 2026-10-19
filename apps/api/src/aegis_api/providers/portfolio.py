from typing import Any

from aegis_api.clients.bridge import (
    FiscoBridgeClient,
)


class AegisPortfolioProvider:

    def __init__(
        self,
        bridge: FiscoBridgeClient,
    ):
        self.bridge = bridge

    async def get_state(
        self,
    ) -> dict[str, Any]:

        return await self.bridge.portfolio_state()

    async def rebalance(
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

        return await self.bridge.portfolio_rebalance(
            direction=direction,
            amount_in_minor=amount_in_minor,
            min_amount_out_minor=min_amount_out_minor,
            poi_hash=poi_hash,
            intent_id=intent_id,
            policy_hash=policy_hash,
            risk_score_bps=risk_score_bps,
            expiry=expiry,
        )
