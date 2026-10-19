from models import (
    NodeObservation,
)


class EmergencyPolicy:

    def evaluate(
        self,
        observations: list[NodeObservation],
    ) -> tuple[
        str,
        str,
    ]:

        healthy = [
            node
            for node
            in observations
            if node.reachable
        ]

        unhealthy_count = (
            len(observations)
            - len(healthy)
        )

        # Two unavailable/divergent validators
        # exceed the tolerated single-fault
        # assumption of the 4-validator setup.
        if unhealthy_count >= 2:

            return (
                "EMERGENCY_STOP",
                "TWO_OR_MORE_NODES_UNAVAILABLE",
            )

        if not healthy:

            return (
                "EMERGENCY_STOP",
                "NO_HEALTHY_VALIDATORS",
            )

        heights = [
            node.block_number

            for node in healthy

            if node.block_number
            is not None
        ]

        if len(heights) >= 2:

            spread = (
                max(heights)
                - min(heights)
            )

            if spread >= 3:

                return (
                    "EMERGENCY_STOP",
                    "BLOCK_HEIGHT_DIVERGENCE",
                )

            if spread > 0:

                return (
                    "DEGRADED",
                    "MINOR_HEIGHT_SKEW",
                )

        if unhealthy_count == 1:

            return (
                "DEGRADED",
                "SINGLE_NODE_UNAVAILABLE",
            )

        peer_counts = [
            node.peer_count

            for node in healthy

            if node.peer_count
            is not None
        ]

        if (
            peer_counts
            and min(peer_counts) < 2
        ):

            return (
                "DEGRADED",
                "LOW_PEER_CONNECTIVITY",
            )

        return (
            "NORMAL",
            "ALL_VALIDATORS_HEALTHY",
        )
