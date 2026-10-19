from unittest.mock import AsyncMock, MagicMock
import pytest
from aegis_api.domain.agent import (
    ComplianceAssessment,
    FinancialDecision,
    FinancialPlan,
    MarketAnalysis,
    SecurityAnalysis,
)
from aegis_api.domain.compliance import ChinaComplianceResult
from aegis_api.domain.regulatory import ComplianceEvidencePackage, RegulatoryComplianceBundle
from aegis_api.services.autonomous_risk import AutonomousRisk
from aegis_api.services.orchestrator import AutonomousFinancialOrchestrator


@pytest.fixture
def mock_orchestrator():
    wallet = MagicMock()
    wallet.get_state = AsyncMock(return_value={"balanceMinor": "1000000"})
    wallet.get_balance = AsyncMock(return_value={"balanceMinor": "1000000"})
    wallet.get_daily_spent = AsyncMock(return_value={"spentTodayMinor": "0"})
    wallet.transfer = AsyncMock()

    actions = MagicMock()
    actions.prepare_transfer = AsyncMock()
    actions.execute_prepared = AsyncMock()

    market_agent = MagicMock()
    market_agent.analyze = AsyncMock(
        return_value=MarketAnalysis(
            outlook="NEUTRAL", volatility="LOW", liquidity="HIGH", summary="Normal"
        )
    )

    market_data = MagicMock()
    market_data.snapshot = MagicMock(
        return_value={"price_reference": 1, "volatility_bps": 500}
    )

    leader = MagicMock()
    leader.plan = AsyncMock(
        return_value=FinancialPlan(
            action="TRANSFER",
            recipient="0x1000000000000000000000000000000000000002",
            amount_minor=1000,
            purpose="Valid Supplier Payment",
            confidence=0.95,
            reasoning_summary="Approved by planner",
        )
    )

    security_agent = MagicMock()
    security_agent.review = AsyncMock(
        return_value=SecurityAnalysis(
            safe_to_continue=True,
            prompt_injection_detected=False,
            policy_bypass_attempt=False,
            suspicious_transfer_pattern=False,
            severity="SAFE",
            reasons=[],
        )
    )

    china_compliance = MagicMock()
    china_compliance.evaluate = MagicMock(
        return_value=ChinaComplianceResult(
            decision="PASS",
            policy_version="aegis-effective-financial-policy-v1",
            triggered_rules=[],
            source_refs=[],
            pii_detected=False,
            sensitive_identity_detected=False,
            cross_border_flag=False,
            important_data_flag=False,
            aml_pattern_flag=False,
            explanation="Compliance passed",
        )
    )

    compliance = MagicMock()
    compliance.evaluate = MagicMock(
        return_value=MagicMock(approved=True, reasons=[], policy_status="PASS")
    )

    prompt_security = MagicMock()
    prompt_security.inspect = MagicMock(return_value=[])

    safety_gate = MagicMock()
    safety_gate.inspect = AsyncMock(return_value=MagicMock(allowed=True, severity="SAFE"))

    validator = MagicMock()
    validator.validate_amount = MagicMock()

    recipients = MagicMock()
    recipients.prompt_directory = MagicMock(return_value="Supplier B: 0x1000000000000000000000000000000000000002")
    recipients.resolve_address = MagicMock(return_value="0x1000000000000000000000000000000000000002")
    recipients.approved_address = MagicMock(return_value=True)

    regulatory_compliance = MagicMock()
    regulatory_compliance.assess = AsyncMock(
        return_value=RegulatoryComplianceBundle(
            final_decision="PASS",
            evidence_hash="0x" + "11" * 32,
            policy_version_hash="0x" + "22" * 32,
            retrieval_set_hash="0x" + "33" * 32,
            decision_hash="0x" + "44" * 32,
            package=ComplianceEvidencePackage(
                schema_version="v1",
                policy_version="v1",
                instruction_hash="0x" + "55" * 32,
                purpose_hash="0x" + "66" * 32,
                asset="ACNY",
                recipient_approved=True,
                risk_score_bps=500,
                deterministic_decision="PASS",
                triggered_rules=[],
                deterministic_source_refs=[],
                retrieval_query="test",
                retrieval_model="model",
                retrieval_mode="dense",
                retrieval_hits=[],
                reasoner_assessment="PASS",
                reasoner_source_ids=[],
                reasoner_chunk_ids=[],
                reasoner_summary="All pass",
                final_decision="PASS",
                created_at_utc="2026-10-16T00:00:00Z",
            ),
        )
    )

    risk = MagicMock()
    risk.score = MagicMock(return_value=AutonomousRisk(score_bps=500, risk_class="LOW", components={}))

    audit_anchor = MagicMock()
    audit_anchor.anchor = AsyncMock(return_value={"transactionHash": "0x123", "blockNumber": 10})

    orchestrator = AutonomousFinancialOrchestrator(
        wallet=wallet,
        actions=actions,
        market_agent=market_agent,
        market_data=market_data,
        leader=leader,
        security_agent=security_agent,
        china_compliance=china_compliance,
        compliance=compliance,
        regulatory_compliance=regulatory_compliance,
        audit_anchor=audit_anchor,
        risk=risk,
        prompt_security=prompt_security,
        safety_gate=safety_gate,
        validator=validator,
        recipients=recipients,
        leader_agent_id="aegis-leader",
        model_version="qwen-v1",
        max_onchain_risk_bps=2500,
    )
    return orchestrator


@pytest.mark.asyncio
async def test_deterministic_enforcer_veto_unsafe_approve(mock_orchestrator):
    """I-008 & I034: Leader APPROVE with unsafe risk -> Deterministic veto -> REJECT, 0 audit/financial write."""
    # Mock leader to APPROVE
    mock_orchestrator.leader.decide = AsyncMock(
        return_value=FinancialDecision(
            decision="APPROVE",
            action="TRANSFER",
            reasonCode="APPROVED",
            explanation="Leader wants to approve",
            approvedAmount=1000,
            approvedRecipient="0x1000000000000000000000000000000000000002",
            approvedRiskScore=3000,  # Exceeds max_onchain_risk_bps (2500)
        )
    )
    # Set risk service to report excessive risk
    mock_orchestrator.risk.score = MagicMock(
        return_value=AutonomousRisk(score_bps=3000, risk_class="HIGH", components={})
    )

    result = await mock_orchestrator.execute("Transfer 10 ACNY to Supplier B")
    assert result.decision == "REJECT"
    assert "DETERMINISTIC_ENFORCER_VETO" in result.reason
    assert result.transaction_hash is None
    assert result.audit_transaction_hash is None

    # Verify zero audit anchor and zero financial execution
    mock_orchestrator.audit_anchor.anchor.assert_not_called()
    mock_orchestrator.actions.execute_prepared.assert_not_called()


@pytest.mark.asyncio
async def test_leader_reject_cannot_be_upgraded(mock_orchestrator):
    """I-007 & I035: Leader REJECT cannot be upgraded even if all support gates pass."""
    mock_orchestrator.leader.decide = AsyncMock(
        return_value=FinancialDecision(
            decision="REJECT",
            action="TRANSFER",
            reasonCode="LEADER_UNCONVINCED",
            explanation="Leader rejects based on holistic context",
            approvedAmount=None,
            approvedRecipient=None,
            approvedRiskScore=500,
        )
    )

    result = await mock_orchestrator.execute("Transfer 10 ACNY to Supplier B")
    assert result.decision == "REJECT"
    assert "LEADER_REJECTED" in result.reason
    assert result.transaction_hash is None
    assert result.audit_transaction_hash is None

    # Zero writes
    mock_orchestrator.audit_anchor.anchor.assert_not_called()
    mock_orchestrator.actions.execute_prepared.assert_not_called()


@pytest.mark.asyncio
async def test_leader_sees_support_evidence_before_decision(mock_orchestrator):
    """I-006 & I013: Leader receives support evidence and decides after support gates."""
    mock_orchestrator.leader.decide = AsyncMock(
        return_value=FinancialDecision(
            decision="APPROVE",
            action="TRANSFER",
            reasonCode="ALL_CONTROLS_VERIFIED",
            explanation="Approved after verifying security, compliance, RAG, and risk.",
            approvedAmount=1000,
            approvedRecipient="0x1000000000000000000000000000000000000002",
            approvedRiskScore=500,
        )
    )
    mock_orchestrator.actions.prepare_transfer = AsyncMock(
        return_value=MagicMock(poi_hash="0x" + "aa" * 32, poi=MagicMock(expiry=1000000000))
    )
    mock_orchestrator.actions.execute_prepared = AsyncMock(
        return_value=MagicMock(
            reason="ON_CHAIN_SUCCESS",
            transaction_hash="0x" + "bb" * 32,
            block_number=105,
            poi_hash="0x" + "aa" * 32,
            intent_id="0x" + "cc" * 32,
            policy_hash="0x" + "dd" * 32,
        )
    )

    result = await mock_orchestrator.execute("Transfer 10 ACNY to Supplier B")
    assert result.decision == "EXECUTE"
    mock_orchestrator.leader.decide.assert_called_once()
    
    # Check that leader.decide received the supporting assessments
    call_kwargs = mock_orchestrator.leader.decide.call_args.kwargs
    assert "security_severity" in call_kwargs
    assert "china_compliance_status" in call_kwargs
    assert "regulatory_decision" in call_kwargs
    assert "risk_score_bps" in call_kwargs
