import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)
from aegis_api.domain.agent import (
    MarketAnalysis,
)

SYSTEM_PROMPT = """
You are the Aegis-Sphere Market Analysis Agent.
Your job is to analyze the provided financial market snapshot.

Rules:
- Never invent missing market data.
- Treat SIMULATED data explicitly as simulation.
- Evaluate trend, volatility and liquidity.
- Do not execute transactions.
- Do not output wallet instructions.
- Return only the required structured output.
"""


class MarketAgent:
    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def analyze(
        self,
        snapshot: dict,
    ) -> MarketAnalysis:
        return await self.llm.structured(
            system_prompt=SYSTEM_PROMPT,
            user_prompt=(
                "Analyze this market snapshot:\n"
                + json.dumps(
                    snapshot,
                    sort_keys=True,
                )
            ),
            response_model=MarketAnalysis,
            temperature=0.1,
        )
