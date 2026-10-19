import hashlib
import json
from typing import Any


def canonical_bytes(
    value: dict,
) -> bytes:
    def reject_float(item):
        if isinstance(item, float):
            raise ValueError(
                "FLOAT_NOT_ALLOWED_IN_CANONICAL_DATA"
            )
        if isinstance(item, dict):
            for v in item.values():
                reject_float(v)
        elif isinstance(item, list):
            for v in item:
                reject_float(v)

    reject_float(value)
    return json.dumps(
        value,
        ensure_ascii=False,
        sort_keys=True,
        separators=(",", ":"),
    ).encode("utf-8")


canonical_json_bytes = canonical_bytes


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
    return "0x" + hashlib.sha256(canonical_bytes(value)).hexdigest()


def user_request_hash(
    raw_instruction: str,
) -> str:
    return (
        "0x"
        + hashlib.sha256(
            raw_instruction.encode(
                "utf-8"
            )
        ).hexdigest()
    )
