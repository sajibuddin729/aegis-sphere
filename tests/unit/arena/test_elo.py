from aegis_api.services.elo import (
    EloEngine,
)


def test_equal_rating_blue_win():

    result = EloEngine(
        k_factor=32
    ).update(
        red_rating=1200,
        blue_rating=1200,
        winner="BLUE",
    )

    assert (
        result.red_after
        == 1184
    )

    assert (
        result.blue_after
        == 1216
    )


def test_draw_preserves_equal_ratings():

    result = EloEngine(
        k_factor=32
    ).update(
        red_rating=1200,
        blue_rating=1200,
        winner="DRAW",
    )

    assert (
        result.red_after
        == 1200
    )

    assert (
        result.blue_after
        == 1200
    )
