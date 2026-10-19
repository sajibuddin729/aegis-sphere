# Aegis-Sphere
### Safety-First Autonomous On-Chain Wallet Agent on FISCO BCOS

Aegis-Sphere is an autonomous financial wallet agent built on FISCO BCOS. It reads natural language instructions, analyzes financial intent, manages multi-asset portfolios, and executes on-chain transactions while enforcing strict mathematical risk controls, pre-execution audit commitments, zero-knowledge privacy, and independent on-chain governance.

---

## Table of Contents
1. [Overview](#1-overview)
2. [Competition Topic and Alignment](#2-competition-topic-and-alignment)
3. [Core Problems and Solutions](#3-core-problems-and-solutions)
4. [System Architecture](#4-system-architecture)
5. [Autonomous Execution Pipeline](#5-autonomous-execution-pipeline)
6. [Proof-of-Intent Specification](#6-proof-of-intent-specification)
7. [Multi-Agent AI University](#7-multi-agent-ai-university)
8. [Consensus Emergency Kill-Switch](#8-consensus-emergency-kill-switch)
9. [Zero-Knowledge Privacy Proofs](#9-zero-knowledge-privacy-proofs)
10. [Smart Contracts Manifest](#10-smart-contracts-manifest)
11. [Full Deployment Instructions](#11-full-deployment-instructions)
12. [Testing and Verification](#12-testing-and-verification)
13. [On-Chain Evidence and Transactions](#13-on-chain-evidence-and-transactions)
14. [Repository Structure](#14-repository-structure)
15. [Limitations and Future Extensions](#15-limitations-and-future-extensions)
16. [License](#16-license)

---

## 1. Overview
Aegis-Sphere connects artificial intelligence with enterprise consortium blockchain infrastructure. Built for Topic F (Autonomous On-Chain Agent Wallet), it follows a key security principle:

AI models propose decisions, but deterministic smart contracts, isolated signing bridges, and zero-knowledge gates retain absolute control over money movement.

If an AI makes a mistake or gets attacked by a prompt injection, the system catches the error and blocks the transaction before it ever reaches the blockchain.

---

## 2. Competition Topic and Alignment
- Track: Topic F - Autonomous On-Chain Agent Wallet
- Blockchain Network: FISCO BCOS v3.17.1 (PBFT Consensus, group0)
- Core Innovations:
  - Canonical Proof-of-Intent (PoI) with strict 18-field zero-float encoding
  - Cryptographically linked Pre-Execution Audit (Audit Block < Financial Block)
  - Isolated Java Signing Boundary with hardware private key protection
  - Software Emergency Kill-Switch with automatic fail-closed PBFT monitoring
  - Agentic AI University with 1024-d BGE-M3 semantic memory and on-chain update governance
  - Groth16 Zero-Knowledge privacy proofs for private financial balance solvency

---

## 3. Core Problems and Solutions

Autonomous AI agents on financial blockchains face five main hazards:

1. Hallucination and Prompt Injection:
   - Problem: Malicious text can trick language models into sending funds to attacker addresses.
   - Solution: Independent security gates (Qwen3Guard) and deterministic contract whitelists reject unauthorized addresses regardless of model confidence.

2. Unverifiable Execution:
   - Problem: Blockchains record token transfers without proof that the execution matched the user instruction.
   - Solution: Every transaction requires a Canonical Proof-of-Intent hash signed and registered on-chain before money moves.

3. Out-of-Order Audit Commitments:
   - Problem: Auditing after execution allows failed transactions to hide or re-write history.
   - Solution: Application ordering strictly enforces that the audit record is committed at Block N before the financial transfer executes at Block N+1.

4. Consensus and Node Failures:
   - Problem: If validator nodes stall or fork, autonomous agents might sign transactions against invalid state.
   - Solution: An independent emergency oracle monitors validator health every second. If two or more nodes fail, signing is cut off immediately.

5. Uncontrolled AI Self-Modification:
   - Problem: AI agents that update themselves can deploy buggy code directly to production.
   - Solution: Self-updates must pass an 11-step independent sandbox verifier and be recorded on an on-chain governance contract. Direct production auto-deployment is strictly blocked.

---

## 4. System Architecture

Aegis-Sphere consists of five coordinated tiers:

```
[User / Command Center :5173]
              |
[FastAPI Autonomous Gateway :8000]
     |--- Local AI Planner (Qwen3-8B :8081)
     |--- Safety Gate (Qwen3Guard :8082)
     |--- Regulatory RAG (BGE-M3 1024-d :8083)
     |--- AI University (PostgreSQL + pgvector :5433)
              |
[Independent Sandbox Verifier :8090] ---> [Governance Signer :9090] ---> [UpdateGovernance.sol]
              |
[Autonomous Emergency Oracle :8091]
              | (Software Kill-Switch Gate)
[Isolated Java Signing Bridge :9080]
              | (Financial Signer Key Material)
[4-Validator PBFT FISCO BCOS Network :20200-20203]
```

Key Architectural Boundaries:
- Private Signing Keys exist solely inside the Java process (Port 9080).
- The Python AI services have zero access to private keys.
- If any security check fails, the Java bridge refuses to construct or sign the transaction.

---

## 5. Autonomous Execution Pipeline

Every financial action follows a deterministic 10-step sequence:

1. Request Hashing: The raw UTF-8 user request is hashed directly with SHA-256 without modification.
2. Financial Planning: The planner agent creates a candidate transfer or rebalance structure.
3. Security Analysis: The security agent screens for prompt injections, wallet drain attacks, and jailbreaks.
4. Compliance Evaluation: Checks input against Chinese data privacy laws (PIPL, CSL, DSL) and AML structuring rules.
5. Regulatory RAG: Uses local BGE-M3 1024-d dense embeddings to verify legal statutes.
6. Risk Scoring: Calculates basis points (BPS) risk based on transaction size, account history, and asset volatility.
7. Leader Agent Decision: Synthesizes evidence to produce an APPROVE or REJECT verdict.
8. Deterministic Enforcer: Enforces rules in code. It can veto an APPROVE to REJECT, but can never upgrade a REJECT to APPROVE.
9. Pre-Execution Audit: Records the full evidence bundle into AuditEvidenceRegistry.sol on FISCO BCOS.
10. Isolated Signing and Execution: Once the audit block is verified, the Java Bridge signs the financial transaction on FISCO BCOS.

---

## 6. Proof-of-Intent Specification

The Canonical Proof-of-Intent (PoI) binds the user instruction, AI decision, and compliance evidence into an immutable 18-field schema:

```json
{
  "intentId": "0x6e90a3ca0e364d491558410c7435cce2f954e1c77a5ced16a23f1a2aa82aac2e",
  "userRequestHash": "0xb6780c10a30b42f3607730ea4ca76503c1533ce5402a11b0e3eefdae8fc7671b",
  "leaderAgentId": "aegis-leader",
  "modelVersion": "qwen-local",
  "action": "TRANSFER",
  "asset": "ACNY",
  "amount": 200,
  "recipient": "0x1000000000000000000000000000000000000002",
  "riskScore": 453,
  "riskClass": "LOW",
  "policyVersion": "aegis-effective-financial-policy-v1",
  "policyHash": "0xc7a89fd05228298a42a48f113e168f044e919fabdf240178db3c49a628cfdf38",
  "evidenceHash": "0xf590d65dc8d10a797ae09c70af0abe1b3349725d1ed1b5bb8e71fabe5ce91224",
  "explanationHash": "0x66da2a0e44b9230542072019009339963095dcc3771144882233991100223344",
  "timestamp": 1792159000,
  "expiry": 1792159300,
  "nonce": "0x77bb449911002233445566778899aabbccddeeff00112233445566778899aabb",
  "decision": "APPROVE"
}
```

Strict Zero-Float Rule:
No floating-point values are allowed in canonical data. Financial percentages are converted to Basis Points (1 BPS = 0.01%), and currency values are stored as minor integer units. This ensures identical cryptographic hashes across any operating system.

---

## 7. Multi-Agent AI University

Aegis-Sphere features continuous experiential learning without changing base model weights:

1. Semantic Experience Memory:
   - Uses PostgreSQL with pgvector (1024-d BGE-M3 dense embeddings).
   - Only defensive lessons with utility scores above 70 BPS are admitted.
   - Attack prompts from lost matches are quarantined to avoid memory poisoning.

2. Red and Blue Adversarial Arena:
   - Specialist Red Team agents probe financial boundaries with novel attack prompts.
   - Blue Defender agents enforce deterministic security policies.
   - Uses an Elo rating engine to track capability progression over time.

3. Controlled Knowledge Distillation:
   - When a specialist identifies a vulnerability, the lesson is distilled into episodic memory.
   - The Leader Agent retrieves these memory vectors during future evaluations.
   - Base model weights remain frozen, preventing model drift.

---

## 8. Consensus Emergency Kill-Switch

The system continuously tracks the health of all 4 PBFT validators on FISCO BCOS:

- Fault Model: Practical Byzantine Fault Tolerance with n = 4, f = 1.
  - 4 of 4 nodes healthy: NORMAL status. Signing enabled.
  - 3 of 4 nodes healthy: DEGRADED status. Quorum maintained. Signing enabled with warning.
  - 2 or fewer nodes healthy: EMERGENCY_STOP status. Signing immediately blocked fail-closed.
- Block Height Divergence: If validator block numbers diverge by more than 2 blocks, signing halts immediately.
- Recovery Rule: Returning from EMERGENCY_STOP to NORMAL requires 3 consecutive observation windows of 4 healthy nodes.

---

## 9. Zero-Knowledge Privacy Proofs

To preserve institutional confidentiality, account solvency and risk eligibility can be proven without disclosing raw balances:

- Circuit: Circom 2.1.0 with 1,321 non-linear constraints.
- Proving Scheme: Groth16 over the BN254 elliptic curve.
- Smart Contracts: AegisEligibilityVerifier.sol and ZKEligibilityGate.sol.
- Guarantees: Verifies that account balance is greater than requested transfer and risk score is below the maximum allowed threshold, while keeping balance and score hidden off-chain.

---

## 10. Smart Contracts Manifest

Deployed on FISCO BCOS (Group 0, Chain ID: chain0):

| Contract Name | On-Chain Address | Architecture Role |
|---|---|---|
| AegisToken (ACNY) | 0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085 | Simulated Consortium Currency Token |
| PoIRegistry | 0xd24180cc0fef2f3e545de4f9aafc09345cd08903 | Canonical Proof-of-Intent Registry |
| RiskCircuitBreaker | 0x37a44585bf1e9618fdb4c62c4c96189a07dd4b48 | Spending Caps and Recipient Whitelist |
| AgentWallet | 0x31ed5233b81c79d5adddeef991f531a9bbc2ad01 | Autonomous Smart Vault Custody |
| AuditEvidenceRegistry | 0xa2577385c5d9eb63b6bb230325e28c7294650f44 | Pre-Execution Audit Anchor |
| UpdateGovernance | 0x40fce2f6cbef3daa8e671549102d386f75a058ba | AI Candidate Update Governance |
| AegisEligibilityVerifier | 0x0966598496a26e837bbd75b0dd877a3612e02962 | Groth16 Cryptographic Pairing Verifier |
| ZKEligibilityGate | 0x717570d925f97a1bc4a852b61fe89b46a1294b2b | Private Eligibility Gate Registry |
| AegisGrowthIndex (AGIX) | 0x610857669da60d63f4c9e30713bb86a49251fe2a | Synthetic Portfolio Asset |
| SwapPool | 0x70b93b07646fd256572d3c8efeef3c8de4a7e7a4 | AMM Constant-Price Liquidity Pool |
| PortfolioRiskGuard | 0x07f5bea3c7a17b8f1c1eab27eaf267373cacb049 | Portfolio Allocation Risk Checks |
| PortfolioManager | 0x0519e7423be2a00eb57d9a7e6e98cd1e6f02cff4 | Autonomous Rebalance Coordinator |

---

## 11. Full Deployment Instructions

Follow these instructions to set up, build, and deploy the complete Aegis-Sphere system.

> 📖 **Comprehensive Multi-Daemon Guide:** For the full multi-service architecture, daemon ports, and advanced environment configurations, refer to the dedicated [Deployment Guide (docs/DEPLOYMENT.md)](docs/DEPLOYMENT.md).

### 11.1 System Prerequisites
- Operating System: macOS (Apple Silicon / Intel) or Linux (Ubuntu 22.04 LTS+)
- Node.js: v20.0 or higher with npm
- Python: v3.11 or v3.12
- Java: OpenJDK 17 LTS
- Maven: Apache Maven 3.8 or higher
- Docker: Docker Desktop or Docker Engine with Docker Compose

Check versions:
```bash
node -v
python3 --version
java -version
mvn -version
docker --version
```

---

### 11.2 One-Click Startup (Recommended)
Aegis-Sphere includes an automated orchestrator script that manages all services, ports, and logs:

```bash
# 1. Clone the repository
git clone https://github.com/aegis-sphere/aegis-sphere.git
cd aegis-sphere

# 2. Run the complete stack in foreground
./run_all.sh

# 3. Check health and on-chain status in another terminal
./run_all.sh --status

# 4. Stop all services cleanly when finished
./run_all.sh --stop
```

To run all daemons in the background:
```bash
./run_all.sh --daemon
```

---

### 11.3 Step-by-Step Manual Deployment

If you prefer to start each component manually, follow this exact sequence:

#### Step 1: Environment Setup
```bash
# Create Python virtual environment
python3 -m venv .venv
source .venv/bin/activate

# Install Python dependencies
pip install -r requirements.txt
```

#### Step 2: FISCO BCOS 4-Node Blockchain Network
```bash
# Option A: Start native 4-node local cluster
cd blockchain/network/127.0.0.1
bash start_all.sh
cd ../../../

# Option B: Run via Docker container
docker run -d --name aegis-fisco-dev \
  -p 20200-20203:20200-20203 \
  fiscobcos/fiscobcos:v3.17.1
```

Verify nodes are sealing blocks:
```bash
curl -k -s -H "Content-Type: application/json" \
  -X POST --data '{"jsonrpc":"2.0","method":"getBlockNumber","params":["group0"],"id":1}' \
  https://127.0.0.1:20200
```

#### Step 3: PostgreSQL and pgvector Semantic Store
```bash
docker run -d \
  --name aegis-postgres \
  -e POSTGRES_DB=aegis_memory \
  -e POSTGRES_USER=aegis \
  -e POSTGRES_PASSWORD=aegis_secure_password \
  -p 5433:5432 \
  pgvector/pgvector:pg16
```

#### Step 4: Autonomous Emergency Oracle (:8091)
```bash
source .venv/bin/activate
export ORACLE_PORT=8091
export FISCO_NODES="https://127.0.0.1:20200,https://127.0.0.1:20201,https://127.0.0.1:20202,https://127.0.0.1:20203"
python3 -m uvicorn aegis_oracle.main:app --app-dir apps/api/src --host 127.0.0.1 --port 8091
```

#### Step 5: Isolated Financial Signing Bridge (:9080)
```bash
cd apps/fisco-bridge
mvn clean package -DskipTests
java -jar target/fisco-bridge-1.0.0.jar --server.port=9080
cd ../../
```

#### Step 6: FastAPI Autonomous Financial Gateway (:8000)
```bash
source .venv/bin/activate
export GATEWAY_PORT=8000
export BRIDGE_URL="http://127.0.0.1:9080"
export ORACLE_URL="http://127.0.0.1:8091"
python3 -m uvicorn aegis_api.main:app --app-dir apps/api/src --host 127.0.0.1 --port 8000
```

#### Step 7: Vue 3 Web Command Center (:5173)
```bash
cd apps/web
npm install
npm run dev -- --host 127.0.0.1 --port 5173
```

Open your browser at `http://127.0.0.1:5173/`.

---

### 11.4 Active Network and Service Endpoints

| Service Name | Port | Transport | Purpose | Health Check URL |
|---|---|---|---|---|
| PostgreSQL + pgvector | 5433 | TCP | Semantic Memory Store | pg_isready -p 5433 |
| FISCO BCOS Node 0 | 20200 | HTTPS | PBFT Consensus Validator | POST getBlockNumber |
| FISCO BCOS Node 1 | 20201 | HTTPS | PBFT Consensus Validator | POST getBlockNumber |
| FISCO BCOS Node 2 | 20202 | HTTPS | PBFT Consensus Validator | POST getBlockNumber |
| FISCO BCOS Node 3 | 20203 | HTTPS | PBFT Consensus Validator | POST getBlockNumber |
| Emergency Oracle | 8091 | HTTP | 4-Validator Consensus Monitor | http://127.0.0.1:8091/oracle/health |
| Financial Signing Bridge | 9080 | HTTP | Isolated Key Signer | http://127.0.0.1:9080/health |
| Autonomous API Gateway | 8000 | HTTP | Financial Decision Orchestrator | http://127.0.0.1:8000/api/v1/health |
| Web Command Center | 5173 | HTTP | Institutional Dashboard | http://127.0.0.1:5173/ |

---

## 12. Testing and Verification

Aegis-Sphere features 102 automated tests verifying security bounds, replay protection, regulatory compliance, and multi-agent learning.

For complete test case descriptions, step-by-step commands, and invariant pass criteria, view the dedicated test specifications:
- [View Markdown Test Specification (test.md)](test.md)
- [Download Formatted Word Document (Aegis_Sphere_Test_Cases_Specification.docx)](Aegis_Sphere_Test_Cases_Specification.docx)

To run the full suite immediately:
```bash
source .venv/bin/activate
PYTHONPATH="$PWD/apps/api/src" pytest -v tests/
```

To run cryptographic Phase-I evidence verification:
```bash
python3 scripts/verify_phase_i_evidence.py
```

Expected output:
```
ALL_PHASE_I_CRYPTOGRAPHIC_VERIFICATIONS=PASS
102 passed in 0.50s
```

---

## 13. On-Chain Evidence and Transactions

Recorded on the FISCO BCOS local consortium network:

- Autonomous Token Transfer:
  - Financial Transfer Hash: `0xf49511589fc47a8fe208fc9ce34895b32b4cfbd074ecea92f5c95cd5b1311cdf` (Block 100)
  - Pre-Execution Audit Anchor: `0x28c6e84f0ae4bd6df5c76af5ea1aac9a3f7d697bc829c4795c19408cf934fca9` (Block 99)
  - Ordering Verified: Audit Block 99 < Financial Block 100

- Autonomous Portfolio Rebalance:
  - Financial Swap Hash: `0x010810c9d281fb0456449e41a3c13ea936b1ca0f9bb8ae28e88de181945ce87c` (Block 102)
  - Pre-Execution Audit Anchor: `0x3d263ce32d4e9bfea529f3ac7d4ad4918409e117349cf0860d59b3795fd7bd11` (Block 101)
  - Ordering Verified: Audit Block 101 < Financial Block 102

- AI Candidate Governance Update:
  - Transaction Hash: `0xe4945af241733d0a3731135737bc126c88f9bf38abb7cf694f269145a6025242` (Block 83)
  - Target Contract: UpdateGovernance.sol (`0x40fce2f6...`)

- Zero-Knowledge Privacy Proof Registration:
  - Transaction Hash: `0xf6d54704d5bbcaac6769af3526626353849a31736244ab2f4213e162b651ed80` (Block 96)
  - Target Contract: ZKEligibilityGate.sol (`0x717570d9...`)

---

## 14. Repository Structure

```
aegis-sphere/
|-- apps/
|   |-- api/                 # FastAPI Autonomous Gateway (:8000)
|   |-- fisco-bridge/        # Isolated Java Key Signer (:9080)
|   |-- guard/               # Qwen3Guard Safety Gate (:8082)
|   |-- rag/                 # BGE-M3 Dense Vector Index (:8083)
|   `-- web/                 # Vue 3 Institutional Web Interface (:5173)
|-- blockchain/
|   |-- contracts/           # Solidity Smart Contracts (0.8.11)
|   |-- deployments/         # Deployed Contract Manifests
|   `-- network/             # FISCO BCOS 4-Node PBFT Configuration
|-- data/
|   |-- policies/            # Financial and Compliance Policies
|   `-- recipients/          # Approved Entity Directories
|-- docs/
|   |-- DEPLOYMENT.md        # Extended Architecture Notes
|   `-- evidence/            # Phase Cryptographic Evidence Artifacts
|-- oracle/
|   `-- monitor/             # PBFT Emergency Oracle Monitor (:8091)
|-- sandbox/
|   |-- verification/        # Independent Sandbox Verifier (:8090)
|   `-- simulations/         # AI University Arena Scenarios
|-- scripts/                 # Cryptographic Integrity Verifiers
|-- tests/                   # 102 Automated Unit & Security Tests
|-- zk/
|   |-- circuits/            # Circom Zero-Knowledge Circuits
|   `-- reference/           # Groth16 Verification Keys
|-- test.md                  # Complete Test Specification Catalog
|-- run_all.sh               # One-Click Full Stack Orchestrator
`-- README.md                # Project Documentation
```

---

## 15. Limitations and Future Extensions
- Simulation Assets: ACNY is a consortium testnet simulation currency; AGIX is a synthetic portfolio asset. Neither represents public legal tender or speculative securities.
- Software Kill-Switch: Enforced deterministically in software. Physical hardware relay cutoff (microcontroller heartbeat relay) is identified as a future hardware extension.
- Regulatory RAG: Represents machine-readable project rule checking and does not replace formal regulatory certification.

---

## 16. License
Apache License 2.0. Built for Topic F: Autonomous On-Chain Agent Wallet.
