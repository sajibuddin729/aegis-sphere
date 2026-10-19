import time

from models import (
    OracleSnapshot,
)


class OracleStateMachine:

    def __init__(
        self,
        *,
        policy,
        recovery_windows: int,
    ):
        self.policy = policy

        self.recovery_windows = (
            recovery_windows
        )

        self.state = (
            "EMERGENCY_STOP"
        )

        self.recovery_counter = 0

        self.sequence = 0

        self.last_snapshot = None

    def update(
        self,
        observations,
    ) -> OracleSnapshot:

        proposed_state, reason = (
            self.policy.evaluate(
                observations
            )
        )

        if (
            self.state
            == "EMERGENCY_STOP"
            and proposed_state
            != "EMERGENCY_STOP"
        ):

            self.recovery_counter += 1

            if (
                self.recovery_counter
                >= self.recovery_windows
            ):

                self.state = (
                    proposed_state
                )

                self.recovery_counter = 0

            else:

                proposed_state = (
                    "EMERGENCY_STOP"
                )

                reason = (
                    "RECOVERY_STABILITY_WINDOW"
                )

        elif (
            proposed_state
            == "EMERGENCY_STOP"
        ):

            self.state = (
                "EMERGENCY_STOP"
            )

            self.recovery_counter = 0

        else:

            self.state = (
                proposed_state
            )

            self.recovery_counter = 0

        self.sequence += 1

        healthy = [
            node
            for node
            in observations
            if node.reachable
        ]

        heights = [
            node.block_number

            for node in healthy

            if node.block_number
            is not None
        ]

        snapshot = OracleSnapshot(
            state=self.state,

            reason=reason,

            unhealthy_nodes=(
                len(observations)
                - len(healthy)
            ),

            healthy_nodes=(
                len(healthy)
            ),

            min_block=(
                min(heights)
                if heights
                else None
            ),

            max_block=(
                max(heights)
                if heights
                else None
            ),

            height_spread=(
                max(heights)
                - min(heights)

                if heights
                else None
            ),

            recovery_counter=(
                self.recovery_counter
            ),

            sequence=(
                self.sequence
            ),

            timestamp=int(
                time.time()
            ),

            observations=(
                observations
            ),
        )

        self.last_snapshot = snapshot

        return snapshot
