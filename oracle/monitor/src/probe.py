import asyncio
import json
import os
import ssl

from models import (
    NodeObservation,
)


class FiscoNodeProbe:

    PORTS = [
        20200,
        20201,
        20202,
        20203,
    ]

    def __init__(
        self,
        *,
        cert: str,
        key: str,
        ca: str | None = None,
    ):
        self.cert = cert
        self.key = key
        self.ca = ca

    async def observe_all(
        self,
    ) -> list[NodeObservation]:

        return await asyncio.gather(
            *[
                self.observe(port)
                for port
                in self.PORTS
            ]
        )

    async def observe(
        self,
        port: int,
    ) -> NodeObservation:

        try:

            block = await self._rpc(
                port,
                "getBlockNumber",
            )

            peers = await self._rpc(
                port,
                "getGroupPeers",
            )

            result = peers.get(
                "result",
                []
            )

            peer_count = (
                len(result)
                if isinstance(
                    result,
                    list,
                )
                else None
            )

            block_result = (
                block.get(
                    "result"
                )
            )

            if isinstance(
                block_result,
                str,
            ):
                block_number = int(
                    block_result,
                    16,
                )

            else:
                block_number = int(
                    block_result
                )

            return NodeObservation(
                rpc_port=port,

                reachable=True,

                block_number=(
                    block_number
                ),

                peer_count=(
                    peer_count
                ),
            )

        except Exception as exc:

            return NodeObservation(
                rpc_port=port,

                reachable=False,

                error=(
                    exc.__class__
                    .__name__
                ),
            )

    async def _rpc(
        self,
        port: int,
        method: str,
    ) -> dict:

        payload = json.dumps(
            {
                "jsonrpc":
                    "2.0",

                "method":
                    method,

                "params":
                    ["group0"],

                "id":
                    1,
            }
        )

        process = await asyncio.create_subprocess_exec(
            "curl",
            "-k",
            "-sS",
            "--max-time",
            "2",

            "--cert",
            self.cert,

            "--key",
            self.key,

            "-H",
            "Content-Type: application/json",

            "-X",
            "POST",

            "--data",
            payload,

            f"https://127.0.0.1:{port}",

            stdout=(
                asyncio.subprocess.PIPE
            ),

            stderr=(
                asyncio.subprocess.PIPE
            ),
        )

        stdout, stderr = (
            await process
            .communicate()
        )

        if process.returncode != 0:

            raise RuntimeError(
                stderr.decode(
                    "utf-8",
                    errors="replace",
                )
            )

        result = json.loads(
            stdout.decode(
                "utf-8"
            )
        )

        if "error" in result:
            raise RuntimeError(
                str(
                    result["error"]
                )
            )

        return result
