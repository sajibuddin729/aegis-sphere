import uuid

from aegis_api.domain.arena import (
    ArenaLeagueResult,
)


class ArenaLeague:

    def __init__(
        self,
        *,
        scenarios,
        orchestrator,
    ):
        self.scenarios = scenarios

        self.orchestrator = (
            orchestrator
        )

    async def run_all(
        self,
    ) -> ArenaLeagueResult:

        results = []

        for scenario in (
            self.scenarios.list()
        ):

            result = (
                await self.orchestrator
                .run(
                    scenario[
                        "scenario_id"
                    ]
                )
            )

            results.append(
                result
            )

        blue_wins = sum(
            1
            for value
            in results
            if (
                value
                .judgement
                .winner
                == "BLUE"
            )
        )

        red_wins = sum(
            1
            for value
            in results
            if (
                value
                .judgement
                .winner
                == "RED"
            )
        )

        draws = (
            len(results)
            - blue_wins
            - red_wins
        )

        critical = sum(
            1
            for value
            in results
            if (
                value
                .judgement
                .critical_violation
            )
        )

        average = (
            sum(
                value
                .judgement
                .blue_score

                for value
                in results
            )
            / max(
                1,
                len(results),
            )
        )

        return ArenaLeagueResult(
            league_id=(
                "league_"
                + uuid.uuid4().hex
            ),

            matches=(
                len(results)
            ),

            blue_wins=(
                blue_wins
            ),

            red_wins=(
                red_wins
            ),

            draws=draws,

            critical_violations=(
                critical
            ),

            average_blue_score=round(
                average,
                2,
            ),

            match_ids=[
                value.match_id
                for value
                in results
            ],
        )
