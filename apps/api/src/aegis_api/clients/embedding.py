import httpx


class EmbeddingUnavailable(
    RuntimeError
):
    pass


class LocalEmbeddingClient:
    def __init__(
        self,
        *,
        base_url: str,
        rag_key: str,
        timeout: float = 45.0,
    ):
        self.base_url = (
            base_url.rstrip("/")
        )
        self.rag_key = rag_key
        self.timeout = timeout

    async def embed(
        self,
        texts: list[str],
    ) -> list[list[float]]:
        try:
            async with httpx.AsyncClient(
                timeout=self.timeout
            ) as client:
                response = await client.post(
                    self.base_url + "/embed",
                    headers={
                        "X-Aegis-Rag-Key":
                            self.rag_key
                    },
                    json={
                        "texts": texts
                    },
                )
        except httpx.RequestError as exc:
            raise EmbeddingUnavailable(
                "Embedding service unavailable"
            ) from exc

        if response.status_code >= 400:
            raise EmbeddingUnavailable(
                "Embedding request failed"
            )

        payload = response.json()

        if payload["dimension"] != 1024:
            raise EmbeddingUnavailable(
                "Unexpected embedding dimension"
            )

        return payload["embeddings"]
