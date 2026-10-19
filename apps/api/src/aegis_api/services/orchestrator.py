from pathlib import Path
import json
import re

from aegis_api.domain.agent import (
    AutonomousAgentResult,
)
from aegis_api.domain.evidence import (
    AegisEvidence,
)
from aegis_api.services.crypto import (
    canonical_hash,
    sha256_text,
    user_request_hash,
)
from aegis_api.domain.wallet import (
    TransferRequest,
)
from aegis_api.providers.actions import (
    AegisActionProvider,
)
from aegis_api.providers.wallet import (
    AegisWalletProvider,
)
from aegis_api.services.autonomous_risk import (
    AutonomousRiskService,
)
from aegis_api.services.audit_anchor import (
    AuditEvidenceAnchor,
)
from aegis_api.services.china_compliance import (
    ChinaComplianceEngine,
)
from aegis_api.services.regulatory_compliance import (
    RegulatoryCompliancePipeline,
)
from aegis_api.services.compliance import (
    ComplianceService,
)
from aegis_api.services.leader_agent import (
    LeaderFinancialAgent,
)
from aegis_api.services.market_agent import (
    MarketAgent,
)
from aegis_api.services.market_data import (
    MarketDataService,
)
from aegis_api.services.plan_validator import (
    PlanValidationError,
    PlanValidator,
)
from aegis_api.services.prompt_security import (
    PromptSecurityGate,
)
from aegis_api.services.recipient import (
    RecipientResolver,
)
from aegis_api.services.safety_gate import (
    IndependentSafetyGate,
)
from aegis_api.services.security_agent import (
    SecurityAgent,
)


class AutonomousFinancialOrchestrator:
    def __init__(
        self,
        *,
        leader: LeaderFinancialAgent,
        market_agent: MarketAgent,
        security_agent: SecurityAgent,
        wallet: AegisWalletProvider,
        actions: AegisActionProvider,
        risk: AutonomousRiskService,
        compliance: ComplianceService,
        prompt_security: PromptSecurityGate,
        validator: PlanValidator,
        market_data: MarketDataService,
        recipients: RecipientResolver,
        max_onchain_risk_bps: int,
        leader_agent_id: str,
        model_version: str,
        safety_gate: IndependentSafetyGate,
        china_compliance: ChinaComplianceEngine,
        regulatory_compliance: RegulatoryCompliancePipeline,
        audit_anchor: AuditEvidenceAnchor,
    ):
        self.leader = leader
        self.market_agent = market_agent
        self.security_agent = security_agent
        self.wallet = wallet
        self.actions = actions
        self.risk = risk
        self.compliance = compliance
        self.prompt_security = prompt_security
        self.validator = validator
        self.market_data = market_data
        self.recipients = recipients
        self.max_onchain_risk_bps = max_onchain_risk_bps
        self.leader_agent_id = leader_agent_id
        self.model_version = model_version
        self.safety_gate = safety_gate
        self.china_compliance = china_compliance
        self.regulatory_compliance = regulatory_compliance
        self.audit_anchor = audit_anchor

    async def execute(
        self,
        user_request: str,
    ) -> AutonomousAgentResult:
        # Layer 1: deterministic injection defense
        hits = self.prompt_security.inspect(user_request)
        if hits:
            return self._reject(
                reason="PROMPT_SECURITY_BLOCK",
                security_status="CRITICAL",
                market_summary="Not evaluated",
                reasoning="Request matched protected security patterns.",
            )

        # Layer 1b: independent safety moderation on input
        guard_input = await self.safety_gate.inspect(user_request)
        if not guard_input.allowed:
            return self._reject(
                reason="QWEN3GUARD_INPUT_BLOCK",
                security_status=guard_input.severity,
                market_summary="Not evaluated",
                reasoning=(
                    "Independent safety moderation blocked the instruction."
                ),
            )

        # Layer 2: collect wallet + market state
        wallet_state = await self.wallet.get_state()
        market_snapshot = self.market_data.snapshot()

        # Layer 3: specialized market agent
        market = await self.market_agent.analyze(market_snapshot)

        # Layer 4: Main Agent plan
        plan = await self.leader.plan(
            request=user_request,
            market_analysis=market,
            market_snapshot=market_snapshot,
            wallet_state=wallet_state,
            recipient_directory=self.recipients.prompt_directory(),
        )

        if plan.action != "TRANSFER":
            return AutonomousAgentResult(
                decision=(
                    "ANALYZE" if plan.action == "ANALYZE" else "REJECT"
                ),
                reason=plan.action,
                action=plan.action,
                security_status="SAFE",
                compliance_status="PASS",
                market_summary=market.summary,
                reasoning_summary=plan.reasoning_summary,
            )

        # Required transfer fields
        if plan.recipient is None or plan.amount_minor is None:
            return self._reject(
                reason="INVALID_AI_PLAN",
                security_status="CAUTION",
                market_summary=market.summary,
                reasoning=plan.reasoning_summary,
            )

        # Layer 5: validate AI interpretation
        try:
            self.validator.validate_amount(
                original_request=user_request,
                planned_minor=plan.amount_minor,
            )
        except PlanValidationError:
            return self._reject(
                reason="AI_PLAN_AMOUNT_MISMATCH",
                security_status="CAUTION",
                market_summary=market.summary,
                reasoning=plan.reasoning_summary,
            )

        # Resolve recipient address if model returned name or key
        resolved_recipient = self.recipients.resolve_address(plan.recipient)
        if resolved_recipient:
            plan.recipient = resolved_recipient

        recipient_approved = self.recipients.approved_address(
            plan.recipient
        )

        # Layer 5b: independent safety moderation on proposed AI plan
        guard_plan = await self.safety_gate.inspect(
            json.dumps(
                plan.model_dump(),
                sort_keys=True,
            )
        )
        if not guard_plan.allowed:
            return self._reject(
                reason="QWEN3GUARD_PLAN_BLOCK",
                security_status=guard_plan.severity,
                market_summary=market.summary,
                reasoning=(
                    "Independent model moderation rejected the AI-generated plan."
                ),
            )

        # Layer 6: AI security review
        security = await self.security_agent.review(
            original_request=user_request,
            plan=plan,
        )

        # Layer 7: China compliance engine
        compliance_v2 = self.china_compliance.evaluate(
            instruction=user_request,
            purpose=plan.purpose,
            asset="ACNY",
            recipient_approved=recipient_approved,
        )

        # Layer 7b: Legacy compliance policy fallback
        compliance = self.compliance.evaluate(
            purpose=plan.purpose,
            asset="ACNY",
            recipient_approved=recipient_approved,
        )

        # Layer 8: deterministic financial risk
        risk = self.risk.score(
            amount_minor=plan.amount_minor,
            market_snapshot=market_snapshot,
            recipient_approved=recipient_approved,
            security_severity=security.severity,
        )

        # Deterministic extraction of user risk limit if stated in prompt
        limit_match = re.search(
            r"(?:below|under|<|at most|less than)\s*(\d+(?:\.\d+)?)\s*%",
            user_request,
            re.IGNORECASE,
        )
        if limit_match:
            user_risk_bps = int(float(limit_match.group(1)) * 100)
            if plan.max_acceptable_risk_bps is None:
                plan.max_acceptable_risk_bps = user_risk_bps
            else:
                plan.max_acceptable_risk_bps = min(
                    plan.max_acceptable_risk_bps, user_risk_bps
                )

        effective_limit = self.max_onchain_risk_bps
        if plan.max_acceptable_risk_bps is not None:
            effective_limit = min(
                effective_limit,
                plan.max_acceptable_risk_bps,
            )

        # Layer 8b: Regulatory evidence retrieval & compliance reasoner
        regulatory = await self.regulatory_compliance.assess(
            instruction=user_request,
            purpose=plan.purpose,
            asset="ACNY",
            recipient_approved=recipient_approved,
            risk_score_bps=risk.score_bps,
            deterministic_result=compliance_v2,
        )

        # Compute exact raw userRequestHash
        user_req_hash = user_request_hash(user_request)

        # Layer 8c: Final Leader Decision (receives all support evidence)
        leader_decision = await self.leader.decide(
            request=user_request,
            proposal=plan,
            market_analysis=market,
            security_assessment=security.model_dump(),
            china_compliance=compliance_v2.model_dump(),
            regulatory_summary=regulatory.reasoner_summary,
            risk_score_bps=risk.score_bps,
            security_severity=security.severity,
            china_compliance_status=compliance_v2.decision,
            regulatory_decision=regulatory.final_decision,
        )

        # Layer 8d: Leader decision check (REJECT cannot be upgraded)
        if leader_decision.decision != "APPROVE":
            return self._reject(
                reason="LEADER_REJECTED",
                security_status=security.severity,
                market_summary=market.summary,
                reasoning=f"Leader rejected proposal: {leader_decision.explanation}",
                risk_score=risk.score_bps,
            )

        # Layer 8e: Deterministic Final Enforcer
        # Vetoes APPROVE if any mandatory safety/compliance/risk invariant is violated
        if (
            not security.safe_to_continue
            or security.severity != "SAFE"
            or not compliance.approved
            or compliance_v2.decision != "PASS"
            or regulatory.final_decision != "PASS"
            or risk.score_bps > effective_limit
            or (leader_decision.approvedAmount is not None and leader_decision.approvedAmount != plan.amount_minor)
            or (leader_decision.approvedRecipient is not None and leader_decision.approvedRecipient != plan.recipient)
        ):
            veto_reasons = []
            if not security.safe_to_continue or security.severity != "SAFE":
                veto_reasons.append(f"Security: {security.severity}")
            if not compliance.approved or compliance_v2.decision != "PASS":
                veto_reasons.append("Compliance rejected")
            if regulatory.final_decision != "PASS":
                veto_reasons.append("Regulatory rejected")
            if risk.score_bps > effective_limit:
                veto_reasons.append(f"Risk {risk.score_bps} exceeds limit {effective_limit}")
            return self._reject(
                reason="DETERMINISTIC_ENFORCER_VETO",
                security_status=security.severity,
                market_summary=market.summary,
                reasoning=f"Deterministic final enforcer vetoed Leader approval: {'; '.join(veto_reasons)}",
                risk_score=risk.score_bps,
            )

        # Layer 8f: Build unified AegisEvidence & unified evidenceHash
        unified_evidence = AegisEvidence(
            intent={
                "rawInstruction": user_request,
                "userRequestHash": user_req_hash,
                "action": "TRANSFER",
                "amount": plan.amount_minor,
                "recipient": plan.recipient,
            },
            marketState=market_snapshot,
            proposal=plan.model_dump(),
            securityAssessment=security.model_dump(),
            chinaComplianceAssessment=compliance_v2.model_dump(),
            regulatoryEvidence={
                "retrievalSetHash": regulatory.retrieval_set_hash,
                "sourceIds": regulatory.reasoner_source_ids,
                "evidenceSummary": regulatory.reasoner_summary,
            },
            riskAssessment={
                "scoreBps": risk.score_bps,
                "riskClass": risk.risk_class,
            },
            leaderDecision=leader_decision.model_dump(),
        )
        unified_evidence_hash = canonical_hash(unified_evidence.model_dump())

        # Layer 9: execute through existing cryptographic/on-chain path
        request = TransferRequest(
            recipient=plan.recipient,
            amount_minor=plan.amount_minor,
            risk_score_bps=risk.score_bps,
            purpose=plan.purpose,
        )

        prepared = await self.actions.prepare_transfer(
            request,
            leader_agent_id=self.leader_agent_id,
            model_version=self.model_version,
            compliance_evidence_hash=unified_evidence_hash,
            evidence_hash=unified_evidence_hash,
            user_request_hash=user_req_hash,
            explanation_hash=sha256_text(leader_decision.explanation),
            decision=leader_decision.decision,
            action="TRANSFER",
        )

        audit = await self.audit_anchor.anchor(
            compliance=regulatory,
            related_poi_hash=prepared.poi_hash,
            evidence_hash=unified_evidence_hash,
        )

        result = await self.actions.execute_prepared(
            prepared
        )

        try:
            runtime_dir = Path("docs/evidence/phase-i/runtime")
            runtime_dir.mkdir(parents=True, exist_ok=True)
            (runtime_dir / "final-transfer-evidence.json").write_text(
                json.dumps(unified_evidence.model_dump(), indent=2, ensure_ascii=False)
            )
            (runtime_dir / "final-transfer-poi.json").write_text(
                json.dumps(prepared.poi.model_dump(), indent=2, ensure_ascii=False)
            )
        except Exception:
            pass

        return AutonomousAgentResult(
            decision="EXECUTE",
            reason=result.reason,
            action="TRANSFER",
            transaction_hash=result.transaction_hash,
            block_number=result.block_number,
            poi_hash=result.poi_hash,
            intent_id=result.intent_id,
            policy_hash=result.policy_hash,
            risk_score_bps=risk.score_bps,
            security_status=security.severity,
            compliance_status=compliance.policy_status,
            market_summary=market.summary,
            reasoning_summary=plan.reasoning_summary,
            compliance_evidence_hash=unified_evidence_hash,
            audit_transaction_hash=audit["transactionHash"],
            audit_block_number=audit["blockNumber"],
        )

    def _reject(
        self,
        *,
        reason: str,
        security_status: str,
        market_summary: str,
        reasoning: str,
        compliance_status: str = "PASS",
        risk_score: int | None = None,
    ) -> AutonomousAgentResult:
        return AutonomousAgentResult(
            decision="REJECT",
            reason=reason,
            action="NONE",
            risk_score_bps=risk_score,
            security_status=security_status,
            compliance_status=compliance_status,
            market_summary=market_summary,
            reasoning_summary=reasoning,
        )
