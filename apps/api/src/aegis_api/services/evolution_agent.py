import json

from aegis_api.clients.llm import (
    LocalQwenClient,
)
from aegis_api.domain.candidate_update import (
    CandidateUpdateProposal,
    CandidateUpdateProposalV2,
)

SYSTEM_PROMPT = """
You are the Aegis-Sphere Evolution Agent.

Analyze a completed TRAINING episode and propose at most one useful improvement.

You may propose:
- PROMPT
- POLICY
- STRATEGY
- CODE

You CANNOT:
- edit any repository file,
- execute code,
- deploy a contract,
- access keys,
- change production configuration,
- authorize your own proposal.

The output is only a candidate for later independent verification.
If an improvement is unnecessary, propose a STRATEGY update describing preservation of the successful behavior.
Return only the required structured object.
"""

SYSTEM_PROMPT_V2 = """You are the Aegis-Sphere Evolution Agent.
Your job is to propose at most one candidate improvement
based on measured weaknesses from completed training,
arena, curriculum, or security evaluation evidence.

You MAY propose:
- PROMPT
- POLICY
- STRATEGY
- CODE

Mandatory requirements:
1. Identify a specific evidence-backed problem.
2. Include at least one explicit risk assessment.
3. Include at least two verification tests.
4. Include a rollback plan.
5. State security invariants that must remain unchanged.
6. Never request private-key access.
7. Never bypass risk, compliance, safety or audit controls.
8. Never authorize or deploy your own proposal.
9. Production deployment is outside your authority.
10. Prefer the smallest change that addresses the weakness.

Return only CandidateUpdateProposalV2."""


class EvolutionAgent:
    def __init__(
        self,
        llm: LocalQwenClient,
    ):
        self.llm = llm

    async def propose(
        self,
        *,
        scenario: dict,
        outputs: dict,
        evaluation: dict,
    ) -> CandidateUpdateProposal:
        return await self.llm.structured(
            system_prompt=SYSTEM_PROMPT,
            user_prompt=json.dumps(
                {
                    "scenario": scenario,
                    "agent_outputs": outputs,
                    "evaluation": evaluation,
                },
                ensure_ascii=False,
                sort_keys=True,
            ),
            response_model=CandidateUpdateProposal,
            temperature=0.1,
        )

    async def propose_v2(
        self,
        *,
        finding: dict,
    ) -> CandidateUpdateProposalV2:
        return await self.llm.structured(
            system_prompt=SYSTEM_PROMPT_V2,
            user_prompt=json.dumps(
                finding,
                ensure_ascii=False,
                sort_keys=True,
            ),
            response_model=CandidateUpdateProposalV2,
            temperature=0.1,
        )
