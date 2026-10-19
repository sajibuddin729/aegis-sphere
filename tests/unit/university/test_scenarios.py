from pathlib import Path
from aegis_api.services.university_scenarios import (
    UniversityScenarioRepository,
)


def repo():
    return UniversityScenarioRepository(
        Path(
            "sandbox/simulations/"
            "university/"
            "scenarios-v1.json"
        )
    )


def test_scenarios_have_unique_ids():
    repository = repo()
    values = repository.list()
    ids = [
        item["scenario_id"]
        for item in values
    ]
    assert len(ids) == len(
        set(ids)
    )


def test_all_scenarios_have_constraints():
    for item in repo().list():
        assert (
            "expected_decision"
            in item["constraints"]
        )
        assert (
            "forbidden_recommendations"
            in item["constraints"]
        )
