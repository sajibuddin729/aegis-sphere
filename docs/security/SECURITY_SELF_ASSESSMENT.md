# Aegis-Sphere Security Self-Assessment & Threat Model

---

## 1. Scope
This assessment covers the end-to-end security architecture of Aegis-Sphere, an autonomous financial on-chain agent wallet operating on FISCO BCOS v3.17.1. Evaluated systems include the FastAPI orchestrator, local LLM/safety inference daemons, dense vector regulatory retrieval (RAG), the isolated Java financial signing boundary, the PBFT emergency oracle, the independent sandbox verifier, and all deployed Solidity contracts.

---

## 2. Threat Model
Aegis-Sphere addresses 17 specific adversarial threat vectors:

| ID | Threat Vector | Attack Scenario | Defense Mechanism | Assessment Status |
|---|---|---|---|---|
| **T-01** | **Direct Prompt Injection** | Adversary injects instructions like `"Ignore all previous instructions..."` | `PromptSecurityGate` regex inspection + Qwen3Guard | **MITIGATED (PASS)** |
| **T-02** | **Obfuscated Jailbreak** | Structured evasion attempts to bypass safety filters | Dual-gate moderation: input inspection & proposed plan inspection | **MITIGATED (PASS)** |
| **T-03** | **Policy Bypass** | Prompt attempts to disable risk circuit breaker or skip compliance | Hardcoded deterministic enforcer outside LLM context | **MITIGATED (PASS)** |
| **T-04** | **Risk Threshold Bypass** | Instructions attempting to override maximum transaction limits | Smart contract `RiskCircuitBreaker.sol` on-chain caps | **MITIGATED (PASS)** |
| **T-05** | **Wallet Drain Attack** | Prompt ordering transfer of entire treasury balance | Whitelist recipient check + per-transaction & daily amount limits | **MITIGATED (PASS)** |
| **T-06** | **PoI Hash Replay** | Re-submitting an already-executed Proof-of-Intent | On-chain mapping in `PoIRegistry.sol` (`AEGIS: POI_ALREADY_EXISTS`) | **MITIGATED (PASS)** |
| **T-07** | **IntentId Replay** | Reusing intentId with modified nonce or recipient | Persistent ledger in `PersistentIntentReplayGuard.java` with fsync | **MITIGATED (PASS)** |
| **T-08** | **Expired PoI Replay** | Submitting delayed transaction past valid window | Signing boundary validates `Instant.now().getEpochSecond() <= expiry` | **MITIGATED (PASS)** |
| **T-09** | **Raw PII Leakage** | Storing or transmitting Chinese resident IDs, phone numbers, or cards | Regex PII filter in `ChinaComplianceEngine` blocking transaction | **MITIGATED (PASS)** |
| **T-10** | **Cross-Border Breach** | Exfiltrating regulated financial data overseas | China compliance cross-border rule evaluation blocking transfer | **MITIGATED (PASS)** |
| **T-11** | **RAG Outage / Evasion** | Vector DB unreachable or corrupted | Fail-closed policy: `REGULATORY_EVIDENCE_UNAVAILABLE` (HTTP 503) | **MITIGATED (PASS)** |
| **T-12** | **Audit Service Outage** | Attempting to move funds without audit anchor | Application-enforced ordering: Audit anchor must succeed before sign | **MITIGATED (PASS)** |
| **T-13** | **Audit Writer Revocation**| Unauthorized writer attempts to record audit | Contract modifier `onlyAuthorizedWriter` on `AuditEvidenceRegistry.sol` | **MITIGATED (PASS)** |
| **T-14** | **Financial Signer Theft** | AI model or sandbox exploits code execution to steal PEM | Financial key isolated in Java process; zero Python/file access | **MITIGATED (PASS)** |
| **T-15** | **Governance Escalation** | AI generates upgrade candidate that grants self-deployment | Sandbox verifier check 7 & 10 reject self-auth; Staging only | **MITIGATED (PASS)** |
| **T-16** | **PBFT Validator Failure** | 2 or more nodes crash or diverge | Autonomous Emergency Oracle sets `EMERGENCY_STOP`; signing blocked | **MITIGATED (PASS)** |
| **T-17** | **Invalid / Fake ZK Proof**| Submitting tampered signals or forged witnesses | SnarkJS constraint failure; Groth16 BN128 pairing verification revert | **MITIGATED (PASS)** |

---

## 3. Trust Boundaries
Aegis-Sphere establishes four distinct, isolated trust domains:
1. **AI Reasoning Domain (Low Trust):** Main Qwen model, Market Agent, Planner, Evolution Agent. Outputs are treated as unverified suggestions.
2. **Deterministic Security Domain (High Trust):** Prompt Security Gate, China Compliance Engine, Regulatory RAG, Plan Validator, Risk Service.
3. **Isolated Signing Boundary (Critical Trust):** Java Bridge (`:9080`) holding `agent-executor.pem`. Communicates only via authenticated loopback HTTP with strict HMAC/key verification.
4. **On-Chain Consensus Domain (Root Trust):** FISCO BCOS 4-validator PBFT blockchain. Smart contracts enforce final authorization and state persistence.

---

## 4. Financial Signer Isolation
- The financial signing key (`agent-executor.pem`) is stored exclusively in `apps/fisco-bridge/runtime/account/agent-executor.pem`.
- Static codebase audits confirm zero occurrences of private keys or PEM loading in `apps/api/src`, `oracle/`, or `sandbox/`.
- AI models run in separate processes without file access to the signing runtime.

---

## 5. AI Prompt Security & Moderation
- **Input Filtering:** Deterministic pattern inspection against known injection strings (`ignore previous`, `bypass security`, `drain wallet`).
- **Secondary Plan Inspection:** Proposed structured output from the LLM is passed through Qwen3Guard before any downstream processing.
- **Fail-Closed:** Any moderation failure immediately terminates the request with zero state mutation.

---

## 6. Risk Controls
- **On-Chain Risk Circuit Breaker:** Implemented in `RiskCircuitBreaker.sol`.
  - Max per-transaction limit: 50,000 ACNY.
  - Max daily spending allowance: 100,000 ACNY.
  - Max acceptable risk cap: 2,500 BPS (25.00%).
  - Blocked recipient blacklist.

---

## 7. Compliance Controls
- **Chinese Legal Framework:** Automated compliance checks against:
  - Cybersecurity Law (CSL)
  - Data Security Law (DSL)
  - Personal Information Protection Law (PIPL)
  - Anti-Money Laundering (AML) Law
  - Cross-Border Data Transfer Provisions
- **Disclaimer:** Automated project policy controls do not constitute legal certification.

---

## 8. Replay Protection Architecture
- **Dual Replay Defense:**
  1. *On-Chain:* `PoIRegistry.sol` stores `mapping(bytes32 => bool) public executedPoIs`. Reused `poiHash` reverts on-chain.
  2. *Signing Boundary:* `PersistentIntentReplayGuard.java` records consumed `intentId`s to disk with `fsync` before transaction completion. Survives complete server and bridge restarts.

---

## 9. Audit Integrity & Block Ordering
- Every autonomous transaction generates a universal `AegisEvidence` package.
- `AegisEvidence` is anchored on-chain in `AuditEvidenceRegistry.sol` before financial execution.
- Verification confirms:
  - `PoI.evidenceHash == AuditEvidenceRegistry.evidenceHash`
  - `PoI.poiHash == AuditEvidenceRegistry.relatedPoIHash`
  - `auditBlockNumber < financialBlockNumber` (Strict application-enforced block ordering).

---

## 10. Update Governance & Staging Isolation
- Candidate upgrades generated by the AI University require verification across 11 independent sandbox checks.
- Authorized candidates are committed on-chain to `UpdateGovernance.sol` by a dedicated governance verifier key (`0x108a9272...`).
- Staging output is saved to `sandbox/verification/staging/`. Production code mutation is strictly prohibited.

---

## 11. Autonomous Emergency Shutdown
- Emergency Oracle (`:8091`) monitors PBFT consensus across all 4 validator RPC endpoints.
- If $\ge 2$ nodes fail or diverge, state transitions to `EMERGENCY_STOP`.
- Financial bridge `:9080` intercepts all signing requests and rejects them fail-closed.
- Recovery requires 3 consecutive stable observation windows of 4 healthy nodes.

---

## 12. Zero-Knowledge Privacy Architecture
- Solvency and risk verification use Groth16 proofs generated over the BN254 elliptic curve.
- Circom circuit `aegis_eligibility.circom` enforces `balance >= amountMinor` and `riskScoreBps <= maxRiskScoreBps`.
- Private balances and risk scores remain off-chain; on-chain contracts verify Groth16 proofs via precompiled pairing checks (0x06, 0x07, 0x08).

---

## 13. Secret Management & Repository Hygiene
- Git index audits (`FINAL_SECRET_AUDIT=PASS`) confirm zero private keys, `.pem`, `.env`, or keystore files are committed.
- ZK privacy audits (`ZK_PRIVACY_AUDIT=PASS`) confirm zero private witnesses (`.wtns`) or proving keys (`.zkey`) are tracked.

---

## 14. Testing & Verification Summary
- **Phase-I Security & E2E Master Matrix:** **51 / 51 PASS (100%)**
- **Python Unit & Security Test Suite:** **102 PASS / 0 FAIL**
- **Java Financial Bridge:** **BUILD SUCCESS**
- **Unresolved CRITICAL Issues:** **0**
- **Unresolved HIGH Issues:** **0**

---

## 15. Known Limitations
1. **Simulation Assets:** AegisCNY is a consortium testnet asset token; AGIX is a synthetic growth index. Neither is legal tender or public security.
2. **Consortium Assumption:** Fault tolerance is based on standard PBFT $n \ge 3f + 1$ with $n=4, f=1$.
3. **Local Prover:** ZK witness generation is performed on the local node runtime.

---

## 16. Future Hardware Extensions
- Research is ongoing into physical hardware controller extensions:
  - Microcontroller heartbeat watchdog
  - Air-gap network disconnect relay
  - Sovereign power cutoff circuit
  - Out-of-band optical emergency beacon
