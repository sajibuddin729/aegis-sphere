import json
from pathlib import Path
import numpy as np
from sentence_transformers import (
    SentenceTransformer,
)


class RegulatoryRetriever:
    def __init__(
        self,
        *,
        project_root: Path,
        model_name: str,
        device: str,
    ):
        self.model_name = model_name
        index_dir = (
            project_root
            / "data"
            / "policies"
            / "china"
            / "index"
        )
        corpus_path = (
            project_root
            / "data"
            / "policies"
            / "china"
            / "corpus"
            / "regulatory-chunks-v1.jsonl"
        )
        self.vectors = np.load(
            index_dir / "dense-vectors-v1.npy"
        )
        self.records = []
        with corpus_path.open(
            "r",
            encoding="utf-8",
        ) as handle:
            for line in handle:
                if line.strip():
                    self.records.append(
                        json.loads(line)
                    )
        if len(self.records) != self.vectors.shape[0]:
            raise RuntimeError(
                "RAG_INDEX_CORPUS_MISMATCH"
            )
        self.model = SentenceTransformer(
            model_name,
            device=device,
        )
        self.model.max_seq_length = 1024

    def retrieve(
        self,
        *,
        query: str,
        top_k: int,
    ) -> list[dict]:
        query_vector = (
            self.model.encode(
                [query],
                normalize_embeddings=True,
                convert_to_numpy=True,
            )[0]
        )
        # Both query and corpus vectors
        # are normalized, so dot product
        # equals cosine similarity.
        scores = (
            self.vectors @ query_vector
        )
        top_indices = (
            np.argsort(scores)[::-1][:top_k]
        )
        hits = []
        for rank, index in enumerate(
            top_indices,
            start=1,
        ):
            record = (
                self.records[int(index)]
            )
            hits.append(
                {
                    "rank": rank,
                    "score": round(
                        float(scores[index]),
                        6,
                    ),
                    "chunk_id": record["chunk_id"],
                    "source_id": record["source_id"],
                    "title": record["title"],
                    "authority": record["authority"],
                    "text_sha256": record["text_sha256"],
                    "source_snapshot_sha256": record[
                        "source_snapshot_sha256"
                    ],
                    "text": record["text"],
                }
            )
        return hits

    def embed(
        self,
        texts: list[str],
    ) -> list[list[float]]:
        vectors = self.model.encode(
            texts,
            normalize_embeddings=True,
            convert_to_numpy=True,
        )
        return [
            vector.astype(np.float32).tolist()
            for vector in vectors
        ]

