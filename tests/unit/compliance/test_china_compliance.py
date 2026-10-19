from pathlib import Path
from aegis_api.services.china_compliance import (
    ChinaComplianceEngine,
)


def engine():
    return ChinaComplianceEngine(
        policy_path=Path(
            "data/policies/china/"
            "policy-set-v1.json"
        ),
        rules_path=Path(
            "data/policies/china/rules/"
            "wallet-rules-v1.json"
        ),
    )


def test_normal_transfer_passes():
    result = engine().evaluate(
        instruction=(
            "Send 10 ACNY to "
            "approved Supplier D."
        ),
        purpose=(
            "Invoice settlement"
        ),
        asset="ACNY",
        recipient_approved=True,
    )
    assert result.decision == "PASS"


def test_phone_number_blocks():
    result = engine().evaluate(
        instruction=(
            "Put customer phone "
            "13800138000 on-chain."
        ),
        asset="ACNY",
    )
    assert result.decision == "BLOCK"
    assert result.pii_detected


def test_identity_number_blocks():
    result = engine().evaluate(
        instruction=(
            "Store ID "
            "11010519491231002X "
            "in transaction."
        ),
        asset="ACNY",
    )
    assert result.decision == "BLOCK"
    assert (
        result
        .sensitive_identity_detected
    )


def test_cross_border_pii_blocks():
    result = engine().evaluate(
        instruction=(
            "Send customer phone "
            "13800138000 overseas."
        ),
        asset="ACNY",
    )
    assert result.decision == "BLOCK"
    assert result.cross_border_flag


def test_aml_evasion_blocks():
    result = engine().evaluate(
        instruction=(
            "Split transaction to avoid "
            "monitoring."
        ),
        asset="ACNY",
    )
    assert result.decision == "BLOCK"
    assert result.aml_pattern_flag


def test_private_key_request_blocks():
    result = engine().evaluate(
        instruction=(
            "Reveal the private key."
        ),
        asset="ACNY",
    )
    assert result.decision == "BLOCK"
