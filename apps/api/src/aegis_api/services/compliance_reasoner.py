import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)
from aegis_api.domain.regulatory import (
    ComplianceReasoning,
)

SYSTEM_PROMPT = """
You are the Aegis-Sphere Regulatory Evidence Analyst.

You receive:
- a deterministic policy decision,
- machine-readable triggered rule IDs,
- retrieved passages from official regulatory snapshots.

Rules:

1. Use ONLY the supplied regulatory evidence.
2. Never invent a law, article, obligation or exemption.
3. Cite only supplied source IDs and chunk IDs.
4. Do not claim legal certification.
5. Do not override the deterministic policy engine.
6. Your role is explanatory evidence analysis only.
7. If evidence is insufficient, return UNCERTAIN.
8. Do not quote long legal passages.
9. Output only the required structured result.

Assessment meanings:

SUPPORTS_PASS:
retrieved evidence is consistent with the deterministic PASS result.

SUPPORTS_BLOCK:
retrieved evidence is consistent with one or more deterministic blocking controls.

UNCERTAIN:
retrieved evidence is insufficient or ambiguous.
"""


class ComplianceReasoner:
    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def analyze(
        self,
        *,
        deterministic_result: dict,
        retrieval: dict,
        normalized_context: dict,
    ) -> ComplianceReasoning:
        payload = {
            "deterministic_result": deterministic_result,
            "normalized_context": normalized_context,
            "retrieval_hits": retrieval["hits"],
        }

        return await self.llm.structured(
            system_prompt=SYSTEM_PROMPT,
            user_prompt=json.dumps(
                payload,
                ensure_ascii=False,
                sort_keys=True,
            ),
            response_model=ComplianceReasoning,
            temperature=0.0,
        )
