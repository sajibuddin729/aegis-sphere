from aegis_api.domain.wallet import (
    TransferDecision,
    TransferRequest,
)

from aegis_api.providers.wallet import (
    AegisWalletProvider,
)

from aegis_api.services.poi import (
    PoIService,
)

from aegis_api.services.risk import (
    RiskService,
)


class AegisActionProvider:
    def __init__(
        self,
        *,
        wallet: AegisWalletProvider,
        risk_service: RiskService,
        poi_service: PoIService,
        asset_symbol: str,
    ):
        self.wallet = wallet
        self.risk_service = risk_service
        self.poi_service = poi_service
        self.asset_symbol = asset_symbol

    async def transfer_asset(
        self,
        request: TransferRequest,
        *,
        leader_agent_id: str,
        model_version: str,
    ) -> TransferDecision:

        spent_result = (
            await self.wallet
                .get_daily_spent()
        )

        spent_today = int(
            spent_result[
                "spentTodayMinor"
            ]
        )

        risk = self.risk_service.evaluate(
            amount_minor=(
                request.amount_minor
            ),
            risk_score_bps=(
                request.risk_score_bps
            ),
            spent_today_minor=(
                spent_today
            ),
        )

        if not risk.approved:
            return TransferDecision(
                decision="REJECT",
                reason=risk.reason,
                poi_hash="",
                intent_id="",
                policy_hash="",
                risk_score_bps=(
                    request.risk_score_bps
                ),
                amount_minor=(
                    request.amount_minor
                ),
                recipient=(
                    request.recipient
                ),
            )

        (
            poi,
            poi_hash,
            intent_id,
            policy_hash,
        ) = (
            self.poi_service
                .build_transfer_poi(
                    recipient=(
                        request.recipient
                    ),
                    amount_minor=(
                        request.amount_minor
                    ),
                    risk_score_bps=(
                        request.risk_score_bps
                    ),
                    risk_class=(
                        risk.risk_class
                    ),
                    purpose=(
                        request.purpose
                    ),
                    asset=(
                        self.asset_symbol
                    ),
                    leader_agent_id=(
                        leader_agent_id
                    ),
                    model_version=(
                        model_version
                    ),
                )
        )

        tx = await self.wallet.transfer(
            recipient=request.recipient,
            amount_minor=(
                request.amount_minor
            ),
            poi_hash=poi_hash,
            intent_id=intent_id,
            policy_hash=policy_hash,
            risk_score_bps=(
                request.risk_score_bps
            ),
        )

        return TransferDecision(
            decision="EXECUTE",
            reason="ON_CHAIN_SUCCESS",
            transaction_hash=(
                tx["transactionHash"]
            ),
            block_number=(
                tx["blockNumber"]
            ),
            poi_hash=poi_hash,
            intent_id=intent_id,
            policy_hash=policy_hash,
            risk_score_bps=(
                request.risk_score_bps
            ),
            amount_minor=(
                request.amount_minor
            ),
            recipient=request.recipient,
        )
