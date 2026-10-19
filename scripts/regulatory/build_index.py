import hashlib
import json
import os
from pathlib import Path
import numpy as np
from sentence_transformers import (
    SentenceTransformer,
)

ROOT = Path(__file__).resolve().parents[2]
POLICY_ROOT = (
    ROOT / "data" / "policies" / "china"
)
CORPUS_PATH = (
    POLICY_ROOT / "corpus" / "regulatory-chunks-v1.jsonl"
)
INDEX_DIR = (
    POLICY_ROOT / "index"
)
MODEL_NAME = (
    "BAAI/bge-m3"
)


def file_sha256(
    path: Path,
) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for block in iter(
            lambda: handle.read(
                1024 * 1024
            ),
            b"",
        ):
            digest.update(block)
    return digest.hexdigest()


def main():
    INDEX_DIR.mkdir(
        parents=True,
        exist_ok=True,
    )
    device = os.getenv(
        "AEGIS_RAG_DEVICE",
        "mps",
    )

    records = []
    with CORPUS_PATH.open(
        "r",
        encoding="utf-8",
    ) as handle:
        for line in handle:
            line = line.strip()
            if line:
                records.append(
                    json.loads(line)
                )

    if not records:
        raise RuntimeError(
            "Regulatory corpus is empty"
        )

    model = SentenceTransformer(
        MODEL_NAME,
        device=device,
    )
    # Corpus chunks are intentionally
    # short, so 1024 is enough even
    # though BGE-M3 supports longer input.
    model.max_seq_length = 1024

    texts = [
        record["text"]
        for record in records
    ]
    embeddings = model.encode(
        texts,
        batch_size=4,
        show_progress_bar=True,
        normalize_embeddings=True,
        convert_to_numpy=True,
    )
    embeddings = np.asarray(
        embeddings,
        dtype=np.float32,
    )

    np.save(
        INDEX_DIR
        / "dense-vectors-v1.npy",
        embeddings,
    )

    metadata = [
        {
            key: value
            for key, value in record.items()
            if key != "text"
        }
        for record in records
    ]
    (
        INDEX_DIR
        / "chunk-metadata-v1.json"
    ).write_text(
        json.dumps(
            metadata,
            ensure_ascii=False,
            indent=2,
            sort_keys=True,
        ),
        encoding="utf-8",
    )

    meta = {
        "index_id": "aegis-bge-m3-regulatory-v1",
        "model": MODEL_NAME,
        "retrieval_mode": "dense",
        "normalized_vectors": True,
        "dimension": int(
            embeddings.shape[1]
        ),
        "chunk_count": int(
            embeddings.shape[0]
        ),
        "corpus_sha256": file_sha256(
            CORPUS_PATH
        ),
    }
    (
        INDEX_DIR
        / "index-meta-v1.json"
    ).write_text(
        json.dumps(
            meta,
            indent=2,
            sort_keys=True,
        ),
        encoding="utf-8",
    )

    print("BGE_INDEX=PASS")
    print("DEVICE=" + device)
    print(
        "CHUNKS="
        + str(embeddings.shape[0])
    )
    print(
        "DIMENSION="
        + str(embeddings.shape[1])
    )


if __name__ == "__main__":
    main()
