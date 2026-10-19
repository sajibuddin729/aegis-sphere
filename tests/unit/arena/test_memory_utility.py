from aegis_api.services.memory_utility import (
    MemoryUtilityPolicy,
)


def test_successful_memory_retains_utility():

    score, quarantine = (
        MemoryUtilityPolicy()
        .calculate(
            original_utility=95,
            age_days=1,
            retrieval_count=10,
            successful_reuse_count=10,
        )
    )

    assert score > 80
    assert not quarantine


def test_poor_old_memory_can_be_quarantined():

    score, quarantine = (
        MemoryUtilityPolicy()
        .calculate(
            original_utility=20,
            age_days=500,
            retrieval_count=10,
            successful_reuse_count=0,
        )
    )

    assert score < 30
    assert quarantine
