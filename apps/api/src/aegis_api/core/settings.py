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

    AEGIS_POLICY_VERSION: str = "aegis-effective-financial-policy-v1"
    AEGIS_EFFECTIVE_POLICY_PATH: str = (
        "data/policies/effective/aegis-effective-financial-policy-v1.json"
    )

    AEGIS_LLM_BASE_URL: str = "http://127.0.0.1:8081/v1"
    AEGIS_LLM_MODEL: str = "Qwen3-8B-GGUF"
    AEGIS_LEADER_AGENT_ID: str = "aegis-main-financial-agent"
    AEGIS_LEADER_MODEL_VERSION: str = "qwen3-8b-q4km-v1"
    AEGIS_MARKET_DATA_MODE: str = "SIMULATED"

    AEGIS_PORTFOLIO_POLICY_VERSION: str = (
        "aegis-portfolio-policy-v1"
    )
    AEGIS_PORTFOLIO_MAX_TRADE_NOTIONAL: int = 50_000
    AEGIS_PORTFOLIO_MAX_DAILY_NOTIONAL: int = 200_000
    AEGIS_PORTFOLIO_MAX_RISK_BPS: int = 2_500
    AEGIS_PORTFOLIO_DEFAULT_MIN_ACNY_BPS: int = 6_500
    AEGIS_PORTFOLIO_MAX_AGIX_BPS: int = 4_000

    AEGIS_GUARD_BASE_URL: str = "http://127.0.0.1:8082"
    AEGIS_GUARD_KEY: str = Field(min_length=32)
    AEGIS_GUARD_REQUIRED: bool = True
    AEGIS_CN_POLICY_PATH: str = (
        "data/policies/china/policy-set-v1.json"
    )
    AEGIS_CN_RULES_PATH: str = (
        "data/policies/china/rules/wallet-rules-v1.json"
    )

    AEGIS_RAG_BASE_URL: str = "http://127.0.0.1:8083"
    AEGIS_RAG_KEY: str = Field(min_length=32)
    AEGIS_RAG_REQUIRED: bool = True
    AEGIS_COMPLIANCE_EVIDENCE_DIR: str = (
        "apps/api/runtime/compliance-evidence"
    )

    AEGIS_MEMORY_DATABASE_URL: str
    AEGIS_UNIVERSITY_MEMORY_TOP_K: int = 5
    AEGIS_UNIVERSITY_MEMORY_MIN_SCORE: int = 85
    AEGIS_UNIVERSITY_PASS_SCORE: int = 80



@lru_cache
def get_settings() -> Settings:
    return Settings()
