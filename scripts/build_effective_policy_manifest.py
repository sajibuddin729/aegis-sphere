#!/usr/bin/env python3
"""Build effective financial policy manifest from real tracked policy artifacts."""

import hashlib
import json
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent

EXECUTION_POLICY_FILE = REPO_ROOT / "data" / "policies" / "china" / "rules" / "wallet-rules-v1.json"
PORTFOLIO_POLICY_FILE = REPO_ROOT / "data" / "policies" / "china" / "rules" / "wallet-rules-v1.json"
CHINA_POLICY_FILE = REPO_ROOT / "data" / "policies" / "china" / "policy-set-v1.json"
REGULATORY_MANIFEST_FILE = REPO_ROOT / "data" / "policies" / "china" / "source-manifest-v1.json"
GUARD_CODE_FILE = REPO_ROOT / "apps" / "guard" / "src" / "guard_engine.py"

OUTPUT_MANIFEST = REPO_ROOT / "data" / "policies" / "effective" / "aegis-effective-financial-policy-v1.json"


def sha256_file(path: Path) -> str:
    if not path.exists():
        raise FileNotFoundError(f"Missing required policy file: {path}")
    raw = path.read_bytes()
    return "0x" + hashlib.sha256(raw).hexdigest()


def main():
    print("Computing real policy artifact hashes...")
    exec_hash = sha256_file(EXECUTION_POLICY_FILE)
    print(f"  Execution policy: {EXECUTION_POLICY_FILE.relative_to(REPO_ROOT)} -> {exec_hash}")

    portfolio_hash = sha256_file(PORTFOLIO_POLICY_FILE)
    print(f"  Portfolio policy: {PORTFOLIO_POLICY_FILE.relative_to(REPO_ROOT)} -> {portfolio_hash}")

    china_hash = sha256_file(CHINA_POLICY_FILE)
    print(f"  China compliance policy: {CHINA_POLICY_FILE.relative_to(REPO_ROOT)} -> {china_hash}")

    corpus_hash = sha256_file(REGULATORY_MANIFEST_FILE)
    print(f"  Regulatory manifest: {REGULATORY_MANIFEST_FILE.relative_to(REPO_ROOT)} -> {corpus_hash}")

    guard_hash = sha256_file(GUARD_CODE_FILE)
    print(f"  Guard policy code: {GUARD_CODE_FILE.relative_to(REPO_ROOT)} -> {guard_hash}")

    manifest = {
        "policyVersion": "aegis-effective-financial-policy-v1",
        "executionPolicyHash": exec_hash,
        "portfolioPolicyHash": portfolio_hash,
        "chinaCompliancePolicyHash": china_hash,
        "regulatoryCorpusManifestHash": corpus_hash,
        "guardPolicyVersion": "qwen3-guard-financial-v1",
        "guardPolicyHash": guard_hash,
    }

    OUTPUT_MANIFEST.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_MANIFEST, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2, ensure_ascii=False)
        f.write("\n")

    manifest_bytes = OUTPUT_MANIFEST.read_bytes()
    manifest_sha256 = "0x" + hashlib.sha256(manifest_bytes).hexdigest()
    print(f"\nWrote effective manifest to: {OUTPUT_MANIFEST.relative_to(REPO_ROOT)}")
    print(f"EFFECTIVE_POLICY_MANIFEST_HASH={manifest_sha256}")


if __name__ == "__main__":
    main()
