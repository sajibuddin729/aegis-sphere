import json
import uuid
import asyncpg
from pgvector.asyncpg import (
    register_vector,
)

from aegis_api.clients.embedding import (
    LocalEmbeddingClient,
)
from aegis_api.services.crypto import (
    sha256_text,
)


class UniversityMemoryService:
    def __init__(
        self,
        *,
        database_url: str,
        embedding: LocalEmbeddingClient,
        top_k: int,
    ):
        self.database_url = (
            database_url
        )
        self.embedding = embedding
        self.top_k = top_k
        self.pool = None

    async def start(self):
        if self.pool is not None:
            return

        async def init(
            connection,
        ):
            await register_vector(
                connection
            )

        self.pool = await asyncpg.create_pool(
            self.database_url,
            min_size=1,
            max_size=5,
            init=init,
        )

    async def close(self):
        if self.pool is not None:
            await self.pool.close()
            self.pool = None

    async def recall(
        self,
        *,
        query: str,
        category: str | None = None,
    ) -> list[dict]:
        await self.start()
        vector = (
            await self.embedding.embed([query])
        )[0]

        if category:
            rows = await self.pool.fetch(
                """
                SELECT
                    memory_id,
                    memory_type,
                    category,
                    content,
                    utility_score,
                    effective_utility_score,
                    metadata,
                    1 - ( embedding <=> $1 ) AS similarity
                FROM university_memories
                WHERE approved_for_reuse = TRUE
                  AND quarantined = FALSE
                  AND category = $2
                  AND embedding IS NOT NULL
                ORDER BY
                (
                    0.80 * (
                        1 - (
                            embedding <=> $1
                        )
                    )
                    +
                    0.20 * (
                        LEAST(
                            100,
                            GREATEST(
                                0,
                                effective_utility_score
                            )
                        ) / 100.0
                    )
                ) DESC
                LIMIT $3
                """,
                vector,
                category,
                self.top_k,
            )
        else:
            rows = await self.pool.fetch(
                """
                SELECT
                    memory_id,
                    memory_type,
                    category,
                    content,
                    utility_score,
                    effective_utility_score,
                    metadata,
                    1 - ( embedding <=> $1 ) AS similarity
                FROM university_memories
                WHERE approved_for_reuse = TRUE
                  AND quarantined = FALSE
                  AND embedding IS NOT NULL
                ORDER BY
                (
                    0.80 * (
                        1 - (
                            embedding <=> $1
                        )
                    )
                    +
                    0.20 * (
                        LEAST(
                            100,
                            GREATEST(
                                0,
                                effective_utility_score
                            )
                        ) / 100.0
                    )
                ) DESC
                LIMIT $2
                """,
                vector,
                self.top_k,
            )

        memory_ids = [
            row["memory_id"]
            for row
            in rows
        ]

        if memory_ids:
            await self.pool.execute(
                """
                UPDATE university_memories
                SET
                    retrieval_count =
                        retrieval_count + 1,
                    last_retrieved_at =
                        NOW()
                WHERE memory_id =
                    ANY($1::text[])
                """,
                memory_ids,
            )

        results = []
        for row in rows:
            meta = row["metadata"]
            if isinstance(meta, str):
                meta = json.loads(meta)
            elif meta is not None:
                meta = dict(meta)
            else:
                meta = {}
            results.append({
                "memory_id": row["memory_id"],
                "memory_type": row["memory_type"],
                "category": row["category"],
                "content": row["content"],
                "utility_score": (
                    float(row["utility_score"])
                    if row["utility_score"] is not None
                    else 0.0
                ),
                "effective_utility_score": (
                    float(row["effective_utility_score"])
                    if "effective_utility_score" in row and row["effective_utility_score"] is not None
                    else 0.0
                ),
                "metadata": meta,
                "similarity": (
                    round(float(row["similarity"]), 4)
                    if row["similarity"] is not None
                    else 0.0
                ),
            })
        return results

    async def record_reuse_outcome(
        self,
        *,
        memory_ids: list[str],
        successful: bool,
    ):
        if not memory_ids:
            return
        await self.start()
        if successful:
            await self.pool.execute(
                """
                UPDATE university_memories
                SET
                    successful_reuse_count =
                        successful_reuse_count + 1
                WHERE memory_id =
                    ANY($1::text[])
                """,
                memory_ids,
            )

    async def promote_memory(
        self,
        *,
        source_episode_id: str | None = None,
        category: str,
        content: str,
        utility_score: float,
        metadata: dict,
    ) -> str:
        await self.start()
        content_hash = (
            sha256_text(
                content
            )
        )

        existing = await self.pool.fetchval(
            """
            SELECT memory_id
            FROM university_memories
            WHERE content_hash = $1
            """,
            content_hash,
        )
        if existing:
            return existing

        vector = (
            await self.embedding.embed([content])
        )[0]

        memory_id = (
            "mem_" + uuid.uuid4().hex
        )

        await self.pool.execute(
            """
            INSERT INTO university_memories (
                memory_id,
                source_episode_id,
                memory_type,
                category,
                content,
                content_hash,
                utility_score,
                effective_utility_score,
                approved_for_reuse,
                embedding,
                metadata
            )
            VALUES (
                $1,$2,'LESSON',$3,$4,
                $5,$6,$6,TRUE,$7,$8::jsonb
            )
            """,
            memory_id,
            source_episode_id,
            category,
            content,
            content_hash,
            utility_score,
            vector,
            json.dumps(metadata),
        )

        return memory_id

