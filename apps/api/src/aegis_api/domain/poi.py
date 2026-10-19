from typing import Annotated, Literal
from pydantic import (
    BaseModel,
    ConfigDict,
    StringConstraints,
)

Hex32 = Annotated[
    str,
    StringConstraints(
        pattern=r"^0x[0-9a-fA-F]{64}$"
    ),
]

Address = Annotated[
    str,
    StringConstraints(
        pattern=r"^0x[0-9a-fA-F]{40}$"
    ),
]


class AegisPoI(BaseModel):
    model_config = ConfigDict(
        frozen=True,
        extra="forbid",
    )

    intentId: Hex32
    userRequestHash: Hex32
    leaderAgentId: str
    modelVersion: str
    action: str
    asset: str
    amount: int
    recipient: Address
    riskScore: int
    riskClass: str
    policyVersion: str
    policyHash: Hex32
    evidenceHash: Hex32
    explanationHash: Hex32
    timestamp: int
    expiry: int
    nonce: Hex32
    decision: str
