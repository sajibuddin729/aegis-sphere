from aegis_api.services.crypto import (
    canonical_hash,
)


def test_canonical_hash_order_independent():
    first = {
        "b": 2,
        "a": 1,
    }

    second = {
        "a": 1,
        "b": 2,
    }

    assert (
        canonical_hash(first)
        == canonical_hash(second)
    )


def test_canonical_hash_changes_with_content():
    first = {
        "amount": 100,
    }

    second = {
        "amount": 101,
    }

    assert (
        canonical_hash(first)
        != canonical_hash(second)
    )
