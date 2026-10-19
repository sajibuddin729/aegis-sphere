from aegis_api.services.portfolio_calculator import (
    PortfolioRebalanceCalculator,
)


STATE = {
    "balanceAcnyMinor": "700000",
    "balanceAgixMinor": "30000",
    "totalValueAcnyMinor": "1000000",
    "acnyAllocationBps": "7000",
    "agixAllocationBps": "3000",
    "priceAgixInAcny": "10",
}


def test_70_30_to_66_34():

    result = (
        PortfolioRebalanceCalculator()
        .calculate(
            state=STATE,
            target_agix_bps=3400,
            min_acny_reserve_bps=6500,
        )
    )

    assert (
        result.direction
        == "ACNY_TO_AGIX"
    )

    assert (
        result.amount_in_minor
        == 40000
    )

    assert (
        result.min_amount_out_minor
        == 4000
    )

    assert (
        result.projected_acny_bps
        == 6600
    )

    assert (
        result.projected_agix_bps
        == 3400
    )


def test_34_to_30_growth_reverse():
    state = {
        "balanceAcnyMinor": "660000",
        "balanceAgixMinor": "34000",
        "totalValueAcnyMinor": "1000000",
        "acnyAllocationBps": "6600",
        "agixAllocationBps": "3400",
        "priceAgixInAcny": "10",
    }
    result = (
        PortfolioRebalanceCalculator()
        .calculate(
            state=state,
            target_agix_bps=3000,
            min_acny_reserve_bps=6500,
        )
    )
    assert (
        result.direction
        == "AGIX_TO_ACNY"
    )
    assert (
        result.amount_in_minor
        == 4000
    )
    assert (
        result.min_amount_out_minor
        == 40000
    )
    assert (
        result.projected_acny_bps
        == 7000
    )
    assert (
        result.projected_agix_bps
        == 3000
    )
