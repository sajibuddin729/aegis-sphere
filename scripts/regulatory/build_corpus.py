import hashlib
import json
import re
from pathlib import Path
from bs4 import BeautifulSoup

ROOT = Path(__file__).resolve().parents[2]
POLICY_ROOT = (
    ROOT / "data" / "policies" / "china"
)
RAW_DIR = (
    POLICY_ROOT / "sources" / "raw"
)
NORMALIZED_DIR = (
    POLICY_ROOT / "sources" / "normalized"
)
CORPUS_DIR = (
    POLICY_ROOT / "corpus"
)
MANIFEST_PATH = (
    POLICY_ROOT / "source-manifest-v1.json"
)

ARTICLE_PATTERN = re.compile(
    r"(?=第[一二三四五六七八九十百零〇0-9]+条)"
)
SPACE_PATTERN = re.compile(
    r"[ \t\u3000]+"
)
BLANK_PATTERN = re.compile(
    r"\n{3,}"
)


def sha256_text(value: str) -> str:
    return hashlib.sha256(
        value.encode("utf-8")
    ).hexdigest()


def extract_html(path: Path) -> str:
    raw = path.read_text(
        encoding="utf-8",
        errors="ignore",
    )
    soup = BeautifulSoup(
        raw,
        "html.parser",
    )
    for tag in soup(
        [
            "script",
            "style",
            "nav",
            "header",
            "footer",
            "noscript",
        ]
    ):
        tag.decompose()

    selectors = [
        ".main-content",
        ".Three_xilan_07",
        ".TRS_Editor",
        ".article-content",
        ".pages_content",
        ".content",
        "article",
        "main",
    ]
    candidates = []
    for selector in selectors:
        for node in soup.select(
            selector
        ):
            text = node.get_text(
                "\n",
                strip=True,
            )
            if len(text) > 500:
                candidates.append(
                    text
                )
    if candidates:
        text = max(
            candidates,
            key=len,
        )
    else:
        text = soup.get_text(
            "\n",
            strip=True,
        )
    text = SPACE_PATTERN.sub(
        " ",
        text,
    )
    text = BLANK_PATTERN.sub(
        "\n\n",
        text,
    )
    return text.strip()


def split_long(
    text: str,
    *,
    max_chars: int = 1400,
    overlap: int = 180,
) -> list[str]:
    if len(text) <= max_chars:
        return [text]
    output = []
    start = 0
    while start < len(text):
        end = min(
            start + max_chars,
            len(text),
        )
        chunk = (
            text[start:end]
            .strip()
        )
        if chunk:
            output.append(chunk)
        if end == len(text):
            break
        start = max(
            0,
            end - overlap,
        )
    return output


def article_chunks(
    text: str,
) -> list[str]:
    sections = [
        item.strip()
        for item in ARTICLE_PATTERN.split(
            text
        )
        if item.strip()
    ]
    chunks = []
    for section in sections:
        if len(section) < 80:
            continue
        chunks.extend(
            split_long(section)
        )
    if len(chunks) < 3:
        chunks = split_long(text)
    return chunks


def main():
    NORMALIZED_DIR.mkdir(
        parents=True,
        exist_ok=True,
    )
    CORPUS_DIR.mkdir(
        parents=True,
        exist_ok=True,
    )

    manifest = json.loads(
        MANIFEST_PATH.read_text(
            encoding="utf-8"
        )
    )

    all_chunks = []
    source_report = []

    for source in manifest[
        "sources"
    ]:
        source_id = source[
            "source_id"
        ]
        path = (
            RAW_DIR
            / source["local_file"]
        )
        if not path.exists():
            raise FileNotFoundError(
                f"Missing official snapshot: {path}"
            )
        text = extract_html(path)
        if len(text) < 1000:
            raise RuntimeError(
                f"Extracted source too short: "
                f"{source_id} ({len(text)})"
            )

        normalized_path = (
            NORMALIZED_DIR
            / f"{source_id}.txt"
        )
        normalized_path.write_text(
            text,
            encoding="utf-8",
        )

        snapshot_hash = sha256_text(
            text
        )
        chunks = article_chunks(text)

        for index, chunk in enumerate(
            chunks
        ):
            chunk_id = (
                f"{source_id}:"
                f"{index:04d}"
            )
            all_chunks.append(
                {
                    "chunk_id": chunk_id,
                    "source_id": source_id,
                    "title": source[
                        "title"
                    ],
                    "authority": source[
                        "authority"
                    ],
                    "source_snapshot_sha256": snapshot_hash,
                    "text_sha256": sha256_text(
                        chunk
                    ),
                    "text": chunk,
                }
            )

        source_report.append(
            {
                "source_id": source_id,
                "characters": len(
                    text
                ),
                "chunks": len(chunks),
                "snapshot_sha256": snapshot_hash,
            }
        )

    corpus_path = (
        CORPUS_DIR
        / "regulatory-chunks-v1.jsonl"
    )
    with corpus_path.open(
        "w",
        encoding="utf-8",
    ) as handle:
        for record in all_chunks:
            handle.write(
                json.dumps(
                    record,
                    ensure_ascii=False,
                    sort_keys=True,
                )
                + "\n"
            )

    meta = {
        "corpus_id": manifest[
            "corpus_id"
        ],
        "snapshot_date": manifest[
            "snapshot_date"
        ],
        "source_count": len(
            source_report
        ),
        "chunk_count": len(all_chunks),
        "sources": source_report,
    }
    (
        CORPUS_DIR
        / "corpus-meta-v1.json"
    ).write_text(
        json.dumps(
            meta,
            ensure_ascii=False,
            indent=2,
            sort_keys=True,
        ),
        encoding="utf-8",
    )

    print("CORPUS_BUILD=PASS")
    print(
        "SOURCES="
        + str(len(source_report))
    )
    print(
        "CHUNKS="
        + str(len(all_chunks))
    )


if __name__ == "__main__":
    main()
