import hashlib
import json
from typing import Any


def canonical_json_bytes(
    value: dict[str, Any],
) -> bytes:
    serialized = json.dumps(
        value,
        sort_keys=True,
        separators=(",", ":"),
        ensure_ascii=False,
    )

    return serialized.encode("utf-8")


def sha256_hex(
    raw: bytes,
) -> str:
    return "0x" + hashlib.sha256(raw).hexdigest()


def sha256_text(
    value: str,
) -> str:
    return sha256_hex(
        value.encode("utf-8")
    )


def canonical_hash(
    value: dict[str, Any],
) -> str:
    return sha256_hex(
        canonical_json_bytes(value)
    )
