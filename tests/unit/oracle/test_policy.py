from pathlib import Path
import sys

sys.path.insert(
    0,
    str(
        Path(
            "oracle/monitor/src"
        ).resolve()
    ),
)

from models import (
    NodeObservation,
)

from policy import (
    EmergencyPolicy,
)


def node(
    port,
    *,
    reachable=True,
    height=100,
    peers=3,
):

    return NodeObservation(
        rpc_port=port,
        reachable=reachable,
        block_number=(
            height
            if reachable
            else None
        ),
        peer_count=(
            peers
            if reachable
            else None
        ),
    )


def test_all_healthy_normal():

    state, _ = (
        EmergencyPolicy()
        .evaluate(
            [
                node(20200),
                node(20201),
                node(20202),
                node(20203),
            ]
        )
    )

    assert state == "NORMAL"


def test_one_node_down_degraded():

    state, _ = (
        EmergencyPolicy()
        .evaluate(
            [
                node(20200),
                node(20201),
                node(20202),
                node(
                    20203,
                    reachable=False,
                ),
            ]
        )
    )

    assert state == "DEGRADED"


def test_two_nodes_down_emergency():

    state, _ = (
        EmergencyPolicy()
        .evaluate(
            [
                node(20200),
                node(20201),
                node(
                    20202,
                    reachable=False,
                ),
                node(
                    20203,
                    reachable=False,
                ),
            ]
        )
    )

    assert (
        state
        == "EMERGENCY_STOP"
    )


def test_height_divergence_emergency():

    state, reason = (
        EmergencyPolicy()
        .evaluate(
            [
                node(
                    20200,
                    height=100,
                ),
                node(
                    20201,
                    height=100,
                ),
                node(
                    20202,
                    height=100,
                ),
                node(
                    20203,
                    height=95,
                ),
            ]
        )
    )

    assert (
        state
        == "EMERGENCY_STOP"
    )

    assert (
        reason
        == "BLOCK_HEIGHT_DIVERGENCE"
    )
