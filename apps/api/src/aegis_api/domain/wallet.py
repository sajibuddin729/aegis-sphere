from typing import Literal

from pydantic import BaseModel, Field, field_validator


def validate_address(value: str) -> str:
    if (
        not value.startswith("0x")
        or len(value) != 42
    ):
        raise ValueError(
            "Expected 0x-prefixed 20-byte address"
        )

    try:
        int(value[2:], 16)
    except ValueError as exc:
        raise ValueError(
            "Address must contain hexadecimal characters only"
        ) from exc

    return value.lower()


class TransferRequest(BaseModel):
    recipient: str

    amount_minor: int = Field(
        gt=0,
        description="Amount in ACNY minor units",
    )

    risk_score_bps: int = Field(
        ge=0,
        le=10_000,
    )

    purpose: str = Field(
        min_length=1,
        max_length=160,
    )

    @field_validator("recipient")
    @classmethod
    def recipient_is_valid(
        cls,
        value: str,
    ) -> str:
        return validate_address(value)


class TransferDecision(BaseModel):
    decision: Literal["EXECUTE", "REJECT"]

    reason: str

    transaction_hash: str | None = None

    block_number: int | str | None = None

    poi_hash: str

    intent_id: str

    policy_hash: str

    risk_score_bps: int

    amount_minor: int

    recipient: str


class WalletState(BaseModel):
    wallet: str
    owner: str
    agentExecutor: str
    localSigner: str
    paused: bool
    balanceMinor: str
    spentTodayMinor: str
