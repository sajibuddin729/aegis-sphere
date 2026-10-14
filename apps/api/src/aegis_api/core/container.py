from functools import lru_cache

from aegis_api.clients.bridge import (
    FiscoBridgeClient,
)

from aegis_api.core.settings import (
    get_settings,
)

from aegis_api.providers.actions import (
    AegisActionProvider,
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


@lru_cache
def get_bridge() -> FiscoBridgeClient:
    settings = get_settings()

    return FiscoBridgeClient(
        base_url=(
            settings.AEGIS_BRIDGE_BASE_URL
        ),
        bridge_key=(
            settings.AEGIS_BRIDGE_KEY
        ),
    )


@lru_cache
def get_wallet_provider(
) -> AegisWalletProvider:

    return AegisWalletProvider(
        get_bridge()
    )


@lru_cache
def get_action_provider(
) -> AegisActionProvider:

    settings = get_settings()

    return AegisActionProvider(
        wallet=(
            get_wallet_provider()
        ),

        risk_service=RiskService(
            max_transaction_minor=(
                settings
                .AEGIS_MAX_TRANSACTION_MINOR
            ),

            max_daily_minor=(
                settings
                .AEGIS_MAX_DAILY_MINOR
            ),

            max_risk_bps=(
                settings
                .AEGIS_MAX_RISK_BPS
            ),
        ),

        poi_service=PoIService(
            policy_version=(
                settings
                .AEGIS_POLICY_VERSION
            ),
        ),

        asset_symbol=(
            settings.AEGIS_TOKEN_SYMBOL
        ),
    )
