from typing import Literal

from pydantic import (
    BaseModel,
    Field,
)


OracleState = Literal[
    "NORMAL",
    "DEGRADED",
    "EMERGENCY_STOP",
]


class NodeObservation(
    BaseModel
):
    rpc_port: int

    reachable: bool

    block_number: int | None = None

    peer_count: int | None = None

    error: str | None = None


class OracleSnapshot(
    BaseModel
):
    state: OracleState

    reason: str

    unhealthy_nodes: int

    healthy_nodes: int

    min_block: int | None

    max_block: int | None

    height_spread: int | None

    recovery_counter: int

    sequence: int

    timestamp: int

    observations: list[NodeObservation]


class OracleHealth(
    BaseModel
):
    status: str

    state: OracleState

    sequence: int
