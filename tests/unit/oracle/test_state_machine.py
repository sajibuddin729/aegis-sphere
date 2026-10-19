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

from state_machine import (
    OracleStateMachine,
)


def healthy():

    return [
        NodeObservation(
            rpc_port=20200+i,
            reachable=True,
            block_number=100,
            peer_count=3,
        )
        for i in range(4)
    ]


def two_down():

    values = healthy()

    values[2] = NodeObservation(
        rpc_port=20202,
        reachable=False,
    )

    values[3] = NodeObservation(
        rpc_port=20203,
        reachable=False,
    )

    return values


def test_recovery_requires_three_windows():

    machine = OracleStateMachine(
        policy=EmergencyPolicy(),
        recovery_windows=3,
    )

    first = machine.update(
        two_down()
    )

    assert (
        first.state
        == "EMERGENCY_STOP"
    )

    one = machine.update(
        healthy()
    )

    two = machine.update(
        healthy()
    )

    assert (
        one.state
        == "EMERGENCY_STOP"
    )

    assert (
        two.state
        == "EMERGENCY_STOP"
    )

    three = machine.update(
        healthy()
    )

    assert three.state == "NORMAL"
