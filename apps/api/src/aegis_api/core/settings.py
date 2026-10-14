from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_prefix="",
        case_sensitive=True,
        extra="ignore",
    )

    AEGIS_ENV: str = "development"

    AEGIS_API_HOST: str = "127.0.0.1"
    AEGIS_API_PORT: int = 8000

    AEGIS_BRIDGE_BASE_URL: str = "http://127.0.0.1:9080"
    AEGIS_BRIDGE_KEY: str = Field(min_length=32)

    AEGIS_TOKEN_SYMBOL: str = "ACNY"
    AEGIS_TOKEN_DECIMALS: int = 2

    AEGIS_MAX_TRANSACTION_MINOR: int = 50_000
    AEGIS_MAX_DAILY_MINOR: int = 200_000
    AEGIS_MAX_RISK_BPS: int = 2_500

    AEGIS_POLICY_VERSION: str = "aegis-wallet-policy-v1"


@lru_cache
def get_settings() -> Settings:
    return Settings()
