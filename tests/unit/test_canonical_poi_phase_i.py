from aegis_api.domain.poi import (
    AegisPoI,
)


EXPECTED = {
    "intentId",
    "userRequestHash",
    "leaderAgentId",
    "modelVersion",
    "action",
    "asset",
    "amount",
    "recipient",
    "riskScore",
    "riskClass",
    "policyVersion",
    "policyHash",
    "evidenceHash",
    "explanationHash",
    "timestamp",
    "expiry",
    "nonce",
    "decision",
}


def sample():

    return AegisPoI(
        intentId="0x" + "11" * 32,
        userRequestHash="0x" + "22" * 32,
        leaderAgentId="aegis-leader",
        modelVersion="qwen-local",
        action="TRANSFER",
        asset="ACNY",
        amount=100,
        recipient="0x" + "33" * 20,
        riskScore=500,
        riskClass="LOW",
        policyVersion="aegis-effective-financial-policy-v1",
        policyHash="0x" + "44" * 32,
        evidenceHash="0x" + "55" * 32,
        explanationHash="0x" + "66" * 32,
        timestamp=1000,
        expiry=1300,
        nonce="0x" + "77" * 32,
        decision="APPROVE",
    )


def test_exact_field_set():

    poi = sample()

    assert (
        set(
            poi.model_dump().keys()
        )
        == EXPECTED
    )


def test_no_legacy_fields():

    keys = sample().model_dump()

    forbidden = {
        "schema_version",
        "purpose",
        "compliance_evidence_hash",
        "amount_minor",
        "risk_score_bps",
        "timestamp_utc",
        "expiry_utc",
    }

    assert (
        set(keys)
        .isdisjoint(forbidden)
    )


def test_transfer_and_rebalance_share_model():

    transfer = sample()

    rebalance = transfer.model_copy(
        update={
            "action": "REBALANCE"
        }
    )

    assert isinstance(
        transfer,
        AegisPoI,
    )

    assert isinstance(
        rebalance,
        AegisPoI,
    )
