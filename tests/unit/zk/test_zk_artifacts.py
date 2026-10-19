import hashlib
import json
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent.parent.parent
CIRCUIT_PATH = REPO_ROOT / "zk" / "circuits" / "aegis_eligibility.circom"
VK_PATH = REPO_ROOT / "zk" / "reference" / "aegis_eligibility_verification_key.json"
VERIFIER_SOL_PATH = REPO_ROOT / "blockchain" / "contracts" / "AegisEligibilityVerifier.sol"
GATE_SOL_PATH = REPO_ROOT / "blockchain" / "contracts" / "ZKEligibilityGate.sol"
GITIGNORE_PATH = REPO_ROOT / ".gitignore"


def test_circuit_file_structure():
    assert CIRCUIT_PATH.exists(), f"Circuit file missing at {CIRCUIT_PATH}"
    content = CIRCUIT_PATH.read_text(encoding="utf-8")
    assert "pragma circom 2.1.0;" in content
    assert "template AegisEligibility()" in content
    assert "signal input balance;" in content
    assert "signal input balanceSalt;" in content
    assert "signal input riskScoreBps;" in content
    assert "signal input riskSalt;" in content
    assert "signal input amountMinor;" in content
    assert "signal input maxRiskScoreBps;" in content
    assert "signal output balanceCommitment;" in content
    assert "signal output riskCommitment;" in content
    assert "component balanceHasher = Poseidon(2);" in content
    assert "component riskHasher = Poseidon(2);" in content
    assert "component sufficientBalance = GreaterEqThan(64);" in content
    assert "component acceptableRisk = LessEqThan(14);" in content
    assert "AegisEligibility();" in content


def test_verification_key_structure():
    assert VK_PATH.exists(), f"Verification key missing at {VK_PATH}"
    with open(VK_PATH, "r", encoding="utf-8") as f:
        vk = json.load(f)

    assert vk.get("protocol") == "groth16"
    assert vk.get("curve") == "bn128"
    assert "vk_alpha_1" in vk
    assert "vk_beta_2" in vk
    assert "vk_gamma_2" in vk
    assert "vk_delta_2" in vk
    assert "IC" in vk
    # IC has 1 base point + 4 public signals = 5 points
    assert len(vk["IC"]) == 5, f"Expected 5 IC elements, got {len(vk['IC'])}"


def test_solidity_contracts_exist():
    assert VERIFIER_SOL_PATH.exists(), f"Verifier contract missing at {VERIFIER_SOL_PATH}"
    verifier_src = VERIFIER_SOL_PATH.read_text(encoding="utf-8")
    assert "contract AegisEligibilityVerifier" in verifier_src
    assert "function verifyProof(" in verifier_src

    assert GATE_SOL_PATH.exists(), f"Gate contract missing at {GATE_SOL_PATH}"
    gate_src = GATE_SOL_PATH.read_text(encoding="utf-8")
    assert "contract ZKEligibilityGate" in gate_src
    assert "function verifyAndRecord(" in gate_src
    assert "function registerSnapshot(" in gate_src
    assert "AEGIS: SNAPSHOT_NOT_REGISTERED" in gate_src
    assert "AEGIS: PROOF_ALREADY_RECORDED" in gate_src
    assert "AEGIS: INVALID_ZK_PROOF" in gate_src


def test_gitignore_protects_zk_secrets():
    assert GITIGNORE_PATH.exists()
    gitignore = GITIGNORE_PATH.read_text(encoding="utf-8")
    assert "zk/runtime/" in gitignore
    assert "zk/build/" in gitignore
    assert "*.zkey" in gitignore
    assert "*.wtns" in gitignore


def test_public_signals_order_and_types():
    public_valid_path = REPO_ROOT / "zk" / "runtime" / "public-valid.json"
    if public_valid_path.exists():
        with open(public_valid_path, "r", encoding="utf-8") as f:
            signals = json.load(f)
        assert len(signals) == 4
        # Signal 0: balanceCommitment (large scalar)
        assert int(signals[0]) > 0
        # Signal 1: riskCommitment (large scalar)
        assert int(signals[1]) > 0
        # Signal 2: amountMinor
        assert int(signals[2]) == 1000
        # Signal 3: maxRiskScoreBps
        assert int(signals[3]) == 1500
