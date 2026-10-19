import time
import httpx
import pytest

BRIDGE_URL = "http://127.0.0.1:9080"
BRIDGE_KEY = "5f237c11b5f21ec1381b6c2e212ce7fbd8c2283ee4b8bbe21729d82557f9d7e1"
HEADERS = {"X-Aegis-Bridge-Key": BRIDGE_KEY}


@pytest.mark.asyncio
async def test_poi_expired_rejected():
    """I-012: Expired PoI is rejected before signing with AEGIS: POI_EXPIRED."""
    now_ts = int(time.time())
    expired_ts = now_ts - 3600  # 1 hour ago
    
    payload = {
        "recipient": "0x1000000000000000000000000000000000000002",
        "amountMinor": "100",
        "poiHash": "0x" + "a1" * 32,
        "intentId": "0x" + "b1" * 32,
        "policyHash": "0xc7a89fd05228298a42a48f113e168f044e919fabdf240178db3c49a628cfdf38",
        "riskScoreBps": "500",
        "expiry": expired_ts,
    }
    
    async with httpx.AsyncClient() as client:
        res = await client.post(f"{BRIDGE_URL}/wallet/transfer", json=payload, headers=HEADERS)
        assert res.status_code == 422
        data = res.json()
        assert "POI_EXPIRED" in str(data)


@pytest.mark.asyncio
async def test_duplicate_intent_rejected_at_signing_boundary():
    """I-010: Same intentId with different poiHash is persistently rejected at signing boundary."""
    # We already consumed intent 0x6e90a3ca0e364d491558410c7435cce2f954e1c77a5ced16a23f1a2aa82aac2e
    # in the previous transfer test. Let's submit a new poiHash with this same intentId.
    consumed_intent = "0x6e90a3ca0e364d491558410c7435cce2f954e1c77a5ced16a23f1a2aa82aac2e"
    new_poi_hash = "0x" + "c2" * 32
    
    payload = {
        "recipient": "0x1000000000000000000000000000000000000002",
        "amountMinor": "100",
        "poiHash": new_poi_hash,
        "intentId": consumed_intent,
        "policyHash": "0xc7a89fd05228298a42a48f113e168f044e919fabdf240178db3c49a628cfdf38",
        "riskScoreBps": "500",
        "expiry": int(time.time()) + 300,
    }
    
    async with httpx.AsyncClient() as client:
        res = await client.post(f"{BRIDGE_URL}/wallet/transfer", json=payload, headers=HEADERS)
        assert res.status_code == 422
        data = res.json()
        assert "INTENT_ALREADY_EXECUTED" in str(data)


@pytest.mark.asyncio
async def test_duplicate_poi_hash_rejected_on_chain():
    """I-009: Duplicate poiHash is rejected on-chain."""
    # The poiHash 0xf1ba21bdac22b01b86defdff8281608affe5acfd827bba66fd56ef00427ddd2b is on-chain.
    # If submitted with a brand-new intentId, the bridge allows it past the intent guard,
    # but the FISCO BCOS PoIRegistry contract must reject it on-chain with POI_ALREADY_EXISTS.
    existing_poi = "0xf1ba21bdac22b01b86defdff8281608affe5acfd827bba66fd56ef00427ddd2b"
    new_intent = "0x" + "d3" * 32
    
    payload = {
        "recipient": "0x1000000000000000000000000000000000000002",
        "amountMinor": "100",
        "poiHash": existing_poi,
        "intentId": new_intent,
        "policyHash": "0xc7a89fd05228298a42a48f113e168f044e919fabdf240178db3c49a628cfdf38",
        "riskScoreBps": "500",
        "expiry": int(time.time()) + 300,
    }
    
    async with httpx.AsyncClient() as client:
        res = await client.post(f"{BRIDGE_URL}/wallet/transfer", json=payload, headers=HEADERS)
        assert res.status_code == 422
        data = res.json()
        # Transaction failed / reverted on chain due to existing PoI
        assert data.get("success") is False or "TRANSACTION_REJECTED" in str(data)
