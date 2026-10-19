import json
from pathlib import Path

from aegis_api.domain.portfolio import (
    AutonomousPortfolioResult,
)

from aegis_api.providers.portfolio import (
    AegisPortfolioProvider,
)

from aegis_api.providers.portfolio_actions import (
    PortfolioActionProvider,
)

from aegis_api.services.asset_management_agent import (
    AssetManagementAgent,
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

from aegis_api.services.market_agent import (
    MarketAgent,
)

from aegis_api.services.market_data import (
    MarketDataService,
)

from aegis_api.services.portfolio_calculator import (
    PortfolioCalculationError,
    PortfolioRebalanceCalculator,
)

from aegis_api.services.portfolio_compliance import (
    PortfolioComplianceService,
)

from aegis_api.services.portfolio_plan_validator import (
    PortfolioPlanValidationError,
    PortfolioPlanValidator,
)

from aegis_api.services.portfolio_risk import (
    PortfolioRiskService,
)

from aegis_api.services.portfolio_security_agent import (
    PortfolioSecurityAgent,
)

from aegis_api.services.prompt_security import (
    PromptSecurityGate,
)

from aegis_api.services.safety_gate import (
    IndependentSafetyGate,
)
from aegis_api.domain.agent import FinancialDecision
from aegis_api.domain.evidence import AegisEvidence
from aegis_api.services.crypto import (
    canonical_hash,
    sha256_text,
    user_request_hash,
)


class AutonomousPortfolioOrchestrator:

    def __init__(
        self,
        *,
        portfolio:
            AegisPortfolioProvider,

        actions:
            PortfolioActionProvider,

        asset_agent:
            AssetManagementAgent,

        market_agent:
            MarketAgent,

        security_agent:
            PortfolioSecurityAgent,

        market_data:
            MarketDataService,

        prompt_security:
            PromptSecurityGate,

        validator:
            PortfolioPlanValidator,

        calculator:
            PortfolioRebalanceCalculator,

        risk:
            PortfolioRiskService,

        compliance:
            PortfolioComplianceService,

        max_onchain_risk_bps: int,

        safety_gate:
            IndependentSafetyGate,

        china_compliance:
            ChinaComplianceEngine,

        regulatory_compliance:
            RegulatoryCompliancePipeline,

        audit_anchor:
            AuditEvidenceAnchor,
    ):
        self.portfolio = portfolio
        self.actions = actions

        self.asset_agent = asset_agent
        self.market_agent = market_agent
        self.security_agent = security_agent

        self.market_data = market_data

        self.prompt_security = (
            prompt_security
        )

        self.validator = validator
        self.calculator = calculator

        self.risk = risk
        self.compliance = compliance

        self.max_onchain_risk_bps = (
            max_onchain_risk_bps
        )

        self.safety_gate = safety_gate
        self.china_compliance = china_compliance
        self.regulatory_compliance = regulatory_compliance
        self.audit_anchor = audit_anchor

    async def execute(
        self,
        instruction: str,
    ) -> AutonomousPortfolioResult:

        # 1. Prompt attack gate

        hits = (
            self.prompt_security
            .inspect(instruction)
        )

        if hits:
            return self._reject(
                reason=(
                    "PROMPT_SECURITY_BLOCK"
                ),

                security_status=(
                    "CRITICAL"
                ),

                market_summary=(
                    "Not evaluated"
                ),

                reasoning=(
                    "Protected security "
                    "pattern detected."
                ),
            )

        # 1b. Independent safety moderation on input

        input_guard = await self.safety_gate.inspect(instruction)
        if not input_guard.allowed:
            return self._reject(
                reason=(
                    "QWEN3GUARD_INPUT_BLOCK"
                ),
                security_status=(
                    input_guard.severity
                ),
                market_summary=(
                    "Not evaluated"
                ),
                reasoning=(
                    "Independent safety "
                    "moderation blocked "
                    "the portfolio instruction."
                ),
            )

        # 2. Read real on-chain portfolio

        current = (
            await self.portfolio
            .get_state()
        )

        # 3. Market snapshot

        market_snapshot = (
            self.market_data
            .snapshot()
        )

        market = (
            await self.market_agent
            .analyze(
                market_snapshot
            )
        )

        # 4. Asset manager AI

        plan = (
            await self.asset_agent
            .plan(
                user_request=(
                    instruction
                ),

                portfolio_state=(
                    current
                ),

                market_analysis=(
                    market.model_dump()
                ),

                market_snapshot=(
                    market_snapshot
                ),
            )
        )

        if (
            plan.action
            == "ANALYZE"
        ):
            return AutonomousPortfolioResult(
                decision="ANALYZE",

                reason="ANALYSIS_ONLY",

                security_status="SAFE",

                compliance_status="PASS",

                market_summary=(
                    market.summary
                ),

                reasoning_summary=(
                    plan.reasoning_summary
                ),
            )

        if (
            plan.action
            == "HOLD"
        ):
            return AutonomousPortfolioResult(
                decision="HOLD",

                reason="PORTFOLIO_HOLD",

                target_acny_bps=(
                    plan.target_acny_bps
                ),

                target_agix_bps=(
                    plan.target_agix_bps
                ),

                security_status="SAFE",

                compliance_status="PASS",

                market_summary=(
                    market.summary
                ),

                reasoning_summary=(
                    plan.reasoning_summary
                ),
            )

        # 5. Exact user-plan consistency

        try:

            self.validator.validate(
                original_request=(
                    instruction
                ),

                plan=plan,
            )

        except (
            PortfolioPlanValidationError
        ) as exc:

            return self._reject(
                reason=str(exc),

                security_status=(
                    "CAUTION"
                ),

                market_summary=(
                    market.summary
                ),

                reasoning=(
                    plan.reasoning_summary
                ),
            )

        # 5b. Independent safety moderation on portfolio plan

        plan_guard = await self.safety_gate.inspect(
            json.dumps(
                plan.model_dump(),
                sort_keys=True,
            )
        )

        if not plan_guard.allowed:
            return self._reject(
                reason=(
                    "QWEN3GUARD_PLAN_BLOCK"
                ),
                security_status=(
                    plan_guard.severity
                ),
                market_summary=(
                    market.summary
                ),
                reasoning=(
                    "Independent safety "
                    "moderation rejected "
                    "the portfolio plan."
                ),
            )

        # 6. AI security reviewer

        security = (
            await self.security_agent
            .review(
                original_request=(
                    instruction
                ),

                current_state=(
                    current
                ),

                plan=plan,
            )
        )

        if (
            not security
            .safe_to_continue
            or security
            .severity
            == "CRITICAL"
        ):

            return self._reject(
                reason=(
                    "PORTFOLIO_SECURITY_REJECT"
                ),

                security_status=(
                    security.severity
                ),

                market_summary=(
                    market.summary
                ),

                reasoning="; ".join(
                    security.reasons
                ),
            )

        # 7. Deterministic trade calculation

        try:

            calculation = (
                self.calculator
                .calculate(
                    state=current,

                    target_agix_bps=(
                        plan.target_agix_bps
                    ),

                    min_acny_reserve_bps=(
                        plan
                        .min_acny_reserve_bps
                    ),
                )
            )

        except (
            PortfolioCalculationError
        ) as exc:

            return self._reject(
                reason=str(exc),

                security_status=(
                    security.severity
                ),

                market_summary=(
                    market.summary
                ),

                reasoning=(
                    plan.reasoning_summary
                ),
            )

        if (
            calculation.direction
            == "NONE"
        ):

            return AutonomousPortfolioResult(
                decision="HOLD",

                reason=(
                    "ALREADY_AT_TARGET"
                ),

                target_acny_bps=(
                    calculation
                    .target_acny_bps
                ),

                target_agix_bps=(
                    calculation
                    .target_agix_bps
                ),

                final_acny_bps=(
                    calculation
                    .current_acny_bps
                ),

                final_agix_bps=(
                    calculation
                    .current_agix_bps
                ),

                security_status=(
                    security.severity
                ),

                compliance_status=(
                    "PASS"
                ),

                market_summary=(
                    market.summary
                ),

                reasoning_summary=(
                    plan.reasoning_summary
                ),
            )

        # 8. Compliance

        if (
            calculation.direction
            == "ACNY_TO_AGIX"
        ):
            source = "ACNY"
            target = "AGIX"

        else:
            source = "AGIX"
            target = "ACNY"

        compliance = (
            self.compliance
            .evaluate(
                source_asset=source,

                target_asset=target,

                target_agix_bps=(
                    calculation
                    .target_agix_bps
                ),
            )
        )

        if not compliance.approved:

            return self._reject(
                reason=(
                    "PORTFOLIO_COMPLIANCE_BLOCK"
                ),

                security_status=(
                    security.severity
                ),

                compliance_status=(
                    compliance.status
                ),

                market_summary=(
                    market.summary
                ),

                reasoning="; ".join(
                    compliance.reasons
                ),
            )

        # 8b. China compliance engine

        china_result = (
            self.china_compliance
            .evaluate(
                instruction=(
                    instruction
                ),
                purpose=(
                    plan.reasoning_summary
                ),
                asset=(
                    target
                ),
                recipient_approved=True,
            )
        )

        # 9. Deterministic portfolio risk

        allocation_shift = abs(
            calculation
            .target_agix_bps
            -
            calculation
            .current_agix_bps
        )

        risk = self.risk.score(
            trade_notional=(
                calculation
                .notional_acny_minor
            ),

            allocation_shift_bps=(
                allocation_shift
            ),

            market_snapshot=(
                market_snapshot
            ),

            security_severity=(
                security.severity
            ),
        )

        effective_risk_limit = (
            self.max_onchain_risk_bps
        )

        if (
            plan
            .max_acceptable_risk_bps
            is not None
        ):

            effective_risk_limit = min(
                effective_risk_limit,

                plan
                .max_acceptable_risk_bps,
            )

        if (
            risk.score_bps
            > effective_risk_limit
        ):

            return self._reject(
                reason=(
                    "PORTFOLIO_RISK_REJECT"
                ),

                security_status=(
                    security.severity
                ),

                market_summary=(
                    market.summary
                ),

                reasoning=(
                    f"Risk {risk.score_bps} "
                    "BPS exceeds limit "
                    f"{effective_risk_limit}."
                ),

                risk_score=(
                    risk.score_bps
                ),

                target_acny=(
                    calculation
                    .target_acny_bps
                ),

                target_agix=(
                    calculation
                    .target_agix_bps
                ),
            )

        # 9b. Regulatory compliance pipeline
        regulatory = await self.regulatory_compliance.assess(
            instruction=instruction,
            purpose=plan.reasoning_summary,
            asset=target,
            recipient_approved=True,
            risk_score_bps=risk.score_bps,
            deterministic_result=china_result,
        )

        if regulatory.final_decision != "PASS":
            return self._reject(
                reason="CHINA_POLICY_BLOCK",
                security_status=security.severity,
                compliance_status="BLOCK",
                market_summary=market.summary,
                reasoning="; ".join(china_result.triggered_rules),
            )

        # 10. Final Leader Decision & Deterministic Enforcer
        recipient_addr = getattr(
            self.actions.poi_service, "swap_pool_address", "0x70b93b07646fd256572d3c8efeef3c8de4a7e7a4"
        )
        leader_decision = FinancialDecision(
            decision="APPROVE",
            action="REBALANCE",
            reasonCode="PORTFOLIO_APPROVED_BY_ALL_CONTROLS",
            explanation=f"Rebalance {calculation.direction} of {calculation.amount_in_minor} minor units approved after market, security, compliance, RAG, and risk verification.",
            approvedAmount=calculation.amount_in_minor,
            approvedRecipient=recipient_addr,
            approvedRiskScore=risk.score_bps,
        )

        # Deterministic veto: enforcer can only APPROVE -> REJECT, never REJECT -> APPROVE
        if (
            leader_decision.decision != "APPROVE"
            or security.severity != "SAFE"
            or china_result.policy_status != "PASS"
            or risk.score_bps > self.max_onchain_risk_bps
        ):
            return self._reject(
                reason="DETERMINISTIC_ENFORCER_VETO",
                security_status=security.severity,
                compliance_status="BLOCK",
                market_summary=market.summary,
                reasoning="Deterministic safety enforcer vetoed execution",
                risk_score=risk.score_bps,
            )

        # 11. Universal AegisEvidence Package
        user_req_hash = user_request_hash(instruction)
        unified_evidence = AegisEvidence(
            intent={
                "type": "REBALANCE",
                "rawInstruction": instruction,
                "userRequestHash": user_req_hash,
                "direction": calculation.direction,
                "amountInMinor": calculation.amount_in_minor,
                "minAmountOutMinor": calculation.min_amount_out_minor,
                "targetAcnyBps": calculation.target_acny_bps,
                "targetAgixBps": calculation.target_agix_bps,
            },
            marketState={
                "acnyBalanceMinor": int(market_snapshot.get("balanceAcnyMinor", 0)),
                "agixBalanceMinor": int(market_snapshot.get("balanceAgixMinor", 0)),
                "totalValueAcnyMinor": int(market_snapshot.get("totalValueAcnyMinor", 0)),
                "priceAgixInAcny": str(market_snapshot.get("priceAgixInAcny", "0")),
                "acnyAllocationBps": int(market_snapshot.get("acnyAllocationBps", 0)),
                "agixAllocationBps": int(market_snapshot.get("agixAllocationBps", 0)),
            },
            proposal={
                "action": plan.action,
                "targetAcnyBps": plan.target_acny_bps,
                "targetAgixBps": plan.target_agix_bps,
                "confidenceBps": int(plan.confidence * 10000),
                "reasoningSummary": plan.reasoning_summary,
            },
            securityAssessment={
                "severity": security.severity,
                "safeToContinue": security.safe_to_continue,
                "reasons": security.reasons,
            },
            chinaComplianceAssessment={
                "policyStatus": china_result.policy_status,
                "triggeredRules": china_result.triggered_rules,
            },
            regulatoryEvidence={
                "status": regulatory.final_decision,
                "retrievalSetHash": regulatory.retrieval_set_hash,
                "sourceIds": regulatory.evidence_ids,
            },
            riskAssessment={
                "scoreBps": risk.score_bps,
                "riskClass": risk.risk_class,
                "projectedAcnyBps": calculation.projected_acny_bps,
                "projectedAgixBps": calculation.projected_agix_bps,
            },
            leaderDecision=leader_decision.model_dump(),
        )
        unified_evidence_hash = canonical_hash(unified_evidence.model_dump())

        # 12. Execute: Prepare -> Anchor -> Execute

        prepared = await self.actions.prepare(
            calculation=calculation,
            risk=risk,
            market_snapshot=market_snapshot,
            compliance_evidence_hash=unified_evidence_hash,
            evidence_hash=unified_evidence_hash,
            user_request_hash=user_req_hash,
            explanation_hash=sha256_text(leader_decision.explanation),
            decision=leader_decision.decision,
        )

        audit = await self.audit_anchor.anchor(
            compliance=regulatory,
            related_poi_hash=prepared.poi_hash,
            evidence_hash=unified_evidence_hash,
        )

        executed = await self.actions.execute_prepared(
            prepared
        )

        try:
            runtime_dir = Path("docs/evidence/phase-i/runtime")
            runtime_dir.mkdir(parents=True, exist_ok=True)
            (runtime_dir / "final-rebalance-evidence.json").write_text(
                json.dumps(unified_evidence.model_dump(), indent=2, ensure_ascii=False)
            )
            (runtime_dir / "final-rebalance-poi.json").write_text(
                json.dumps(prepared.poi.model_dump(), indent=2, ensure_ascii=False)
            )
        except Exception:
            pass

        tx = (
            executed[
                "transaction"
            ]
        )

        # 11. Verify actual resulting state

        final_state = (
            await self.portfolio
            .get_state()
        )

        return AutonomousPortfolioResult(
            decision="EXECUTE",

            reason=(
                "ON_CHAIN_REBALANCE_SUCCESS"
            ),

            transaction_hash=(
                tx["transactionHash"]
            ),

            block_number=(
                tx["blockNumber"]
            ),

            poi_hash=(
                executed[
                    "poi_hash"
                ]
            ),

            intent_id=(
                executed[
                    "intent_id"
                ]
            ),

            policy_hash=(
                executed[
                    "policy_hash"
                ]
            ),

            direction=(
                calculation.direction
            ),

            amount_in_minor=(
                calculation
                .amount_in_minor
            ),

            amount_out_minor=(
                calculation
                .min_amount_out_minor
            ),

            calculated_risk_bps=(
                risk.score_bps
            ),

            target_acny_bps=(
                calculation
                .target_acny_bps
            ),

            target_agix_bps=(
                calculation
                .target_agix_bps
            ),

            final_acny_bps=int(
                final_state[
                    "acnyAllocationBps"
                ]
            ),

            final_agix_bps=int(
                final_state[
                    "agixAllocationBps"
                ]
            ),

            security_status=(
                security.severity
            ),

            compliance_status=(
                compliance.status
            ),

            market_summary=(
                market.summary
            ),

            reasoning_summary=(
                plan.reasoning_summary
            ),

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
        target_acny: int | None = None,
        target_agix: int | None = None,
    ):

        return AutonomousPortfolioResult(
            decision="REJECT",

            reason=reason,

            calculated_risk_bps=(
                risk_score
            ),

            target_acny_bps=(
                target_acny
            ),

            target_agix_bps=(
                target_agix
            ),

            security_status=(
                security_status
            ),

            compliance_status=(
                compliance_status
            ),

            market_summary=(
                market_summary
            ),

            reasoning_summary=(
                reasoning
            ),
        )
