from typing import Any
import httpx


class GuardUnavailable(RuntimeError):
    pass


class GuardClient:

    def __init__(
        self,
        *,
        base_url: str,
        guard_key: str,
        timeout: float = 30.0,
    ):
        self.base_url = (
            base_url.rstrip("/")
        )
        self.guard_key = (
            guard_key
        )
        self.timeout = timeout

    async def check(
        self,
        content: str,
    ) -> dict[str, Any]:

        try:
            async with httpx.AsyncClient(
                timeout=self.timeout
            ) as client:
                response = await client.post(
                    (
                        self.base_url
                        + "/guard/check"
                    ),
                    headers={
                        "X-Aegis-Guard-Key":
                            self.guard_key
                    },
                    json={
                        "content": content,
                        "context": "financial",
                    },
                )
        except httpx.RequestError as exc:
            raise GuardUnavailable(
                "Safety Guard unavailable"
            ) from exc

        if response.status_code >= 500:
            raise GuardUnavailable(
                "Safety Guard failed"
            )

        if response.status_code >= 400:
            raise RuntimeError(
                "Safety Guard request rejected"
            )

        return response.json()
