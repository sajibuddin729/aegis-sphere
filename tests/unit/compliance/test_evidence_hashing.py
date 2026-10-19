from aegis_api.services.crypto import (
    canonical_hash,
)


def test_retrieval_hash_order_is_stable():

    first = {
        "hits": [
            {
                "source_id": "CN-PIPL",
                "chunk_id": "CN-PIPL:0001",
                "text_sha256": "abc",
            }
        ]
    }

    second = {
        "hits": [
            {
                "text_sha256": "abc",
                "chunk_id": "CN-PIPL:0001",
                "source_id": "CN-PIPL",
            }
        ]
    }

    assert (
        canonical_hash(first)
        == canonical_hash(second)
    )


def test_changed_chunk_changes_hash():

    first = {
        "chunk":
            "abc"
    }

    second = {
        "chunk":
            "abd"
    }

    assert (
        canonical_hash(first)
        != canonical_hash(second)
    )
