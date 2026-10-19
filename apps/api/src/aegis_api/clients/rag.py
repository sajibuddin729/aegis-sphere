from typing import Any

import httpx


class RagUnavailable(RuntimeError):
    pass


class RagClient:
    def __init__(
        self,
        *,
        base_url: str,
        rag_key: str,
        timeout: float = 45.0,
    ):
        self.base_url = base_url.rstrip("/")
        self.rag_key = rag_key
        self.timeout = timeout

    async def retrieve(
        self,
        *,
        query: str,
        top_k: int = 5,
    ) -> dict[str, Any]:
        try:
            async with httpx.AsyncClient(
                timeout=self.timeout
            ) as client:
                response = await client.post(
                    self.base_url + "/retrieve",
                    headers={
                        "X-Aegis-Rag-Key": self.rag_key
                    },
                    json={
                        "query": query,
                        "top_k": top_k,
                    },
                )
        except httpx.RequestError as exc:
            raise RagUnavailable(
                "Regulatory RAG unavailable"
            ) from exc

        if response.status_code >= 500:
            raise RagUnavailable(
                "Regulatory RAG failed"
            )

        if response.status_code >= 400:
            raise RuntimeError(
                "Regulatory retrieval rejected"
            )

        return response.json()
