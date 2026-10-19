# Aegis-Sphere Test Suite and Verification Specification

> **Navigation:** [← Main README](README.md) • [Deployment Guide](docs/DEPLOYMENT.md) • [Formatted Word Doc](Aegis_Sphere_Test_Cases_Specification.docx)

This document details the complete automated test suite for Aegis-Sphere. It explains how to run the tests, what each test case verifies, and the exact security and financial invariants enforced across the system.

Aegis-Sphere includes 102 automated tests in Python, cryptographic verification scripts, and Java SDK tests.

A formatted Microsoft Word document is available at: [Aegis_Sphere_Test_Cases_Specification.docx](Aegis_Sphere_Test_Cases_Specification.docx)

---

## 1. Quick Test Execution

Run all automated tests with the following commands:

```bash
# Activate Python virtual environment
source .venv/bin/activate

# Run full test suite (102 tests)
PYTHONPATH="$PWD/apps/api/src" pytest -v tests/

# Run security and adversarial tests only (20 tests)
PYTHONPATH="$PWD/apps/api/src" pytest -v tests/security/

# Run unit tests only (82 tests)
PYTHONPATH="$PWD/apps/api/src" pytest -v tests/unit/

# Run independent cryptographic evidence verification script
python3 scripts/verify_phase_i_evidence.py

# Run Java isolated signing bridge tests
cd apps/fisco-bridge && mvn test
```

Expected result: 102 passed in under 1 second.

---

## 2. Test Architecture and Coverage Summary

| Test Category | Directory Path | Test Count | Key Invariants Verified |
|---|---|---|---|
| Phase-I Security Matrix | tests/security/phase_i/test_security_matrix.py | 14 | Prompt injection, wallet drains, daily limits, signer isolation |
| Leader and Enforcer Gate | tests/security/phase_i/test_leader_and_enforcer.py | 3 | Deterministic veto power, reject cannot be upgraded |
| Replay and Signing Gate | tests/security/phase_i/test_replay_and_signing.py | 3 | Expired PoI rejection, duplicate intent ID, on-chain hash replay |
| Autonomous Risk Engine | tests/unit/api/test_risk.py, test_autonomous_risk.py | 6 | Per-transaction cap, daily aggregate cap, risk threshold BPS |
| Canonical Cryptography | tests/unit/api/test_crypto.py | 2 | Order-independent hashing, content sensitivity |
| Portfolio Calculation | tests/unit/api/test_portfolio_calculator.py, test_portfolio_risk.py | 4 | Allocation targets, synthetic swap math, slippage protection |
| Prompt Security | tests/unit/api/test_prompt_security.py | 2 | Normal intent allowance, jailbreak detection |
| Arena and Elo Defense | tests/unit/arena/ | 19 | Elo rating update, memory admission gate, curriculum scheduler |
| China Financial Compliance | tests/unit/compliance/ | 8 | PIPL privacy, CSL network security, AML evasion, RAG hashing |
| Emergency Oracle | tests/unit/oracle/ | 5 | PBFT consensus fault model (n=4, f=1), 3-window recovery |
| Canonical Proof-of-Intent | tests/unit/test_canonical_poi_phase_i.py | 3 | 18 camelCase frozen fields, zero-float validation |
| Self-Update Verifier | tests/unit/verifier/ | 24 | 11-step sandbox checks, AST analysis, zero auto-deploy |
| Zero-Knowledge Privacy | tests/unit/zk/ | 5 | Groth16 circuit constraints, BN254 verification keys |
| Total Automated Tests | tests/ | 102 | Full Coverage Across All Critical System Boundaries |

---

## 3. Detailed Test Case Catalog

### Category 1: Security Matrix (tests/security/phase_i/test_security_matrix.py)
This suite verifies that the system stops adversarial attacks and enforces financial boundaries:
1. test_i015_excessive_transaction_risk: Verifies that transactions with risk scores above 2,500 BPS are rejected immediately.
2. test_i016_transaction_limit_violation: Verifies that any single transfer exceeding the per-transaction cap is blocked.
3. test_i017_daily_limit_contract_enforcement: Verifies that cumulative daily transfers exceeding 100,000 ACNY are rejected.
4. test_i018_unauthorized_recipient: Verifies that transfers to addresses not on the approved whitelist are rejected.
5. test_i019_direct_prompt_injection: Verifies that direct instructions trying to override system rules are trapped by the security gate.
6. test_i020_obfuscated_jailbreak: Verifies that base64 encoded and unicode homoglyph injection attacks are recognized and blocked.
7. test_i021_wallet_drain_instruction: Verifies that commands requesting maximum balance extraction are denied.
8. test_i022_raw_pii_financial_context: Verifies that Chinese national ID numbers and phone numbers in inputs are blocked under PIPL rules.
9. test_i023_cross_border_pii_attempt: Verifies that unauthorized cross-border financial data transmission attempts are blocked.
10. test_i029_signer_isolation: Verifies that the private signing key is never readable by the Python process or LLM prompt memory.
11. test_i030_and_i031_governance_separation: Verifies that the proposal generator cannot sign its own governance updates.
12. test_i046_zk_privacy_audit: Verifies that the zero-knowledge verifier contract accepts valid Groth16 proofs without exposing raw balances.
13. test_i047_runtime_secrets_audit: Verifies that no API keys or private keys are leaked in log files or runtime traces.
14. test_i048_production_mutation_audit: Verifies that self-improvement candidates cannot overwrite live production files directly.

---

### Category 2: Leader and Deterministic Enforcer (tests/security/phase_i/test_leader_and_enforcer.py)
1. test_deterministic_enforcer_veto_unsafe_approve: Verifies that if an AI Leader model mistakenly marks an unsafe transfer as APPROVE, the deterministic enforcer overrides the decision to REJECT.
2. test_leader_reject_cannot_be_upgraded: Verifies that if an AI Leader marks a request as REJECT, no downstream component can convert it to APPROVE.
3. test_leader_sees_support_evidence_before_decision: Verifies that all specialist agent outputs (Security, Compliance, Risk) are provided to the Leader before the final decision is signed.

---

### Category 3: Replay Defense and Signing Boundary (tests/security/phase_i/test_replay_and_signing.py)
1. test_poi_expired_rejected: Verifies that transactions with timestamps older than the expiry window are rejected.
2. test_duplicate_intent_rejected_at_signing_boundary: Verifies that the Java signing bridge checks its persistent ledger and rejects duplicate intent IDs across server restarts.
3. test_duplicate_poi_hash_rejected_on_chain: Verifies that PoIRegistry.sol reverts if a previously registered Proof-of-Intent hash is submitted again.

---

### Category 4: Autonomous Risk Engine (tests/unit/api/)
1. test_normal_risk_is_below_onchain_limit: Verifies that standard low-amount transfers calculate a risk score under 500 BPS.
2. test_critical_security_is_high_risk: Verifies that security warnings escalate risk score to maximum.
3. test_safe_transaction_allowed: Verifies that safe transfers within limits pass risk checks.
4. test_high_risk_rejected: Verifies that high-risk requests are rejected before transaction creation.
5. test_single_limit_rejected: Verifies boundary enforcement at exact transaction threshold.
6. test_daily_limit_rejected: Verifies boundary enforcement when daily accumulation exceeds quota.

---

### Category 5: Canonical Cryptography (tests/unit/api/test_crypto.py)
1. test_canonical_hash_order_independent: Verifies that JSON key ordering does not alter the canonical SHA-256 hash output.
2. test_canonical_hash_changes_with_content: Verifies that changing any field value by a single character produces a completely different hash.

---

### Category 6: Portfolio Management and Calculations (tests/unit/api/)
1. test_70_30_to_66_34: Verifies the rebalance mathematical solver when shifting from 70/30 to target allocation.
2. test_34_to_30_growth_reverse: Verifies the swap calculation when selling synthetic growth asset back into stable token.
3. test_normal_rebalance_under_limit: Verifies that portfolio rebalance amounts within risk parameters are approved.
4. test_critical_security_is_rejected: Verifies that rebalance requests with conflicting signals are halted.

---

### Category 7: Multi-Agent AI University & Arena (tests/unit/arena/)
1. test_safe_candidate_is_only_proposed: Verifies that proposed self-updates require clean sandbox evaluation.
2. test_audit_bypass_candidate_is_frozen: Verifies that any proposal attempting to skip pre-execution audit is quarantined in frozen storage.
3. test_mutator_preserves_constraints: Verifies that adversarial scenario mutators keep valid test invariants.
4. test_mutation_changes_scenario_id: Verifies generated variations receive distinct identifiers.
5. test_lower_rating_gets_priority: Verifies that the curriculum scheduler prioritizes agent capabilities with lower ratings.
6. test_high_performance_escalates_difficulty: Verifies that passing scenarios increases curriculum difficulty level.
7. test_equal_rating_blue_win: Verifies Elo calculation when Blue Defender defeats Red Team.
8. test_draw_preserves_equal_ratings: Verifies Elo stability during drawn rounds.
9. test_safe_block_wins: Verifies scoring when an adversarial attack is safely blocked.
10. test_unsafe_allow_is_critical: Verifies that allowing an unsafe request results in a critical penalty.
11. test_safe_blue_lesson_is_admitted: Verifies that lessons with utility score above 70 BPS enter pgvector semantic memory.
12. test_critical_match_never_enters_memory: Verifies that failed defenses are never written to episodic memory.
13. test_red_team_loss_not_stored: Verifies that unsuccessful attack prompts are filtered out to prevent memory poisoning.
14. test_successful_memory_retains_utility: Verifies that helpful memories retain high relevance ranks.
15. test_poor_old_memory_can_be_quarantined: Verifies that stale or misleading memory entries are removed.
16. test_small_drop_is_not_catastrophic: Verifies normal score variance is not treated as model regression.
17. test_large_drop_is_regression: Verifies that sudden performance drops trigger regression alarms.
18. test_critical_violation_always_regression: Verifies that a single security invariant violation immediately triggers a regression flag.

---

### Category 8: China Financial Compliance and Regulatory RAG (tests/unit/compliance/)
1. test_normal_transfer_passes: Verifies standard transfer compliant with payment settlement guidelines.
2. test_phone_number_blocks: Verifies that plain-text telephone numbers trigger personal privacy protection filters.
3. test_identity_number_blocks: Verifies that national identification numbers trigger sensitive data protection filters.
4. test_cross_border_pii_blocks: Verifies cross-border data transfer safeguards.
5. test_aml_evasion_blocks: Verifies detection of structuring and anti-money laundering evasion patterns.
6. test_private_key_request_blocks: Verifies that any prompt asking for private keys is rejected immediately.
7. test_retrieval_hash_order_is_stable: Verifies that regulatory evidence chunk hashes maintain stable ordering.
8. test_changed_chunk_changes_hash: Verifies evidence integrity tracking if regulatory chunks change.

---

### Category 9: Autonomous Emergency Oracle (tests/unit/oracle/)
1. test_all_healthy_normal: Verifies that 4/4 responsive validators maintain NORMAL status.
2. test_one_node_down_degraded: Verifies that 1 offline node (3/4 responsive) enters DEGRADED status while signing remains available.
3. test_two_nodes_down_emergency: Verifies that 2 offline nodes (<=2/4 responsive) immediately triggers EMERGENCY_STOP and halts signing.
4. test_height_divergence_emergency: Verifies that block height divergence across validators immediately halts signing.
5. test_state_machine_recovery_requires_three_windows: Verifies that returning to NORMAL requires 3 consecutive observation windows of 4/4 healthy nodes.

---

### Category 10: Canonical Proof-of-Intent (tests/unit/test_canonical_poi_phase_i.py)
1. test_exact_field_set: Verifies that the canonical Proof-of-Intent contains exactly 18 camelCase fields.
2. test_no_legacy_fields: Verifies that deprecated snake_case or legacy fields are strictly rejected.
3. test_transfer_and_rebalance_share_model: Verifies that both financial transfers and rebalances share the unified schema.

---

### Category 11: Self-Improvement Sandbox Verifier (tests/unit/verifier/)
1. test_safe_improvement_passes: Verifies that verified rule improvements pass sandbox checks.
2. test_self_authorization_fails: Verifies that self-authorization attempts are caught and rejected.
3. test_valid_candidate_loads: Verifies correct parsing of update proposal packages.
4. test_tampered_proposal_rejected: Verifies that modified proposal packages fail cryptographic hash checks.
5. test_candidate_v2_requires_risk_assessment: Verifies mandatory risk assessment inclusion.
6. test_candidate_v2_requires_rollback_plan: Verifies mandatory rollback procedure definition.
7. test_candidate_v2_requires_invariant_ids: Verifies explicit declaration of protected invariant IDs.
8. test_code_candidate_requires_stronger_sandbox: Verifies additional isolation requirements for executable updates.
9. test_unsafe_candidate_frozen: Verifies that failed candidates move to frozen quarantine storage.
10. test_rejected_candidate_immutable: Verifies rejected proposals cannot be modified or re-submitted under the same ID.
11. test_verified_candidate_governance_decision_derives_automatically: Verifies automatic derivation of on-chain governance payload.
12. test_verified_to_approved_mapping: Verifies correct mapping from sandbox verification to on-chain staging.
13. test_frozen_source_refused: Verifies that candidates from quarantine cannot be sent to governance.
14. test_decision_cannot_be_caller_supplied: Verifies that callers cannot forge approval decisions.
15. test_bundle_tamper_rejected: Verifies that any change to verification bundle invalidates on-chain submission.
16. test_staging_metadata_never_sets_production_authorization: Verifies that staging status never grants production deployment rights.
17. test_rejected_candidate_goes_frozen: Verifies quarantine lifecycle.
18. test_verifier_version_hash_is_actual_policy_document_hash: Verifies verifier version matches canonical policy hash.
19. test_safe_text_passes: Verifies safe documentation updates pass static verification.
20. test_audit_bypass_rejected: Verifies static check catches audit bypass keywords.
21. test_private_key_request_rejected: Verifies static check catches private key access keywords.

---

### Category 12: Zero-Knowledge Privacy (tests/unit/zk/test_zk_artifacts.py)
1. test_circuit_file_structure: Verifies existence of circom circuit file and syntax validity.
2. test_verification_key_structure: Verifies presence and format of Groth16 verification keys.
3. test_solidity_contracts_exist: Verifies on-chain verifier contract files are present.
4. test_gitignore_protects_zk_secrets: Verifies proving keys and private witness files are ignored by git.
5. test_public_signals_order_and_types: Verifies exact ordering of public signals (balance commitment, risk commitment, requested amount, maximum risk BPS).

---

## 4. Verification Checkpoint and Pass Criteria

To confirm that the repository is release ready, execute this full verification sequence:

```bash
# 1. Run all tests with coverage report
PYTHONPATH="$PWD/apps/api/src" pytest --cov=apps/api/src tests/

# 2. Verify all 102 tests pass
# Output must conclude: 102 passed

# 3. Verify cryptographic Phase-I evidence script
python3 scripts/verify_phase_i_evidence.py
# Output must conclude: ALL_PHASE_I_CRYPTOGRAPHIC_VERIFICATIONS=PASS
```

Every pull request and build must pass all 102 tests before deployment to the FISCO BCOS test environment.
