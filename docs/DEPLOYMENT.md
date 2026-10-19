# Aegis-Sphere Deployment & Service Orchestration Guide

> **Navigation:** [← Main README](../README.md) • [Automated Test Suite Specification](../test.md) • [One-Click Runner (run_all.sh)](../run_all.sh)

This document defines the exact prerequisite checklist, environment configurations, and ordered startup sequence for all 11 daemons of the Aegis-Sphere ecosystem.

---

## 1. System Prerequisites
- **Operating System:** macOS (Apple Silicon / Intel) or Linux (Ubuntu 22.04 LTS / Debian 12)
- **Node.js:** v20.0+ (Tested on v25.8.1 with npm 11.11.0)
- **Python:** v3.11 or v3.12 (Tested on Python 3.12.13)
- **Java:** OpenJDK 17 LTS (Required for FISCO BCOS Java SDK)
- **Maven:** Apache Maven 3.8+
- **Docker:** Docker Desktop or Docker Engine with Compose (for PostgreSQL/pgvector)
- **Rust / Circom:** Circom 2.1+ and SnarkJS (for ZK verification)

---

## 2. Ports & Network Architecture

| Service # | Component | Port | Transport / Protocol | Health Check Endpoint |
|---|---|---|---|---|
| **1** | PostgreSQL + pgvector | `5433` | TCP / PostgreSQL | `pg_isready -h 127.0.0.1 -p 5433` |
| **2** | FISCO BCOS Validator 0 | `20200` | HTTPS / JSON-RPC | `getBlockNumber` (group0) |
| **2** | FISCO BCOS Validator 1 | `20201` | HTTPS / JSON-RPC | `getBlockNumber` (group0) |
| **2** | FISCO BCOS Validator 2 | `20202` | HTTPS / JSON-RPC | `getBlockNumber` (group0) |
| **2** | FISCO BCOS Validator 3 | `20203` | HTTPS / JSON-RPC | `getBlockNumber` (group0) |
| **3** | Main Qwen3-8B Engine | `8081` | HTTP / OpenAI-Compat | `GET http://127.0.0.1:8081/health` |
| **4** | Qwen3Guard Safety Gate | `8082` | HTTP / FastAPI | `GET http://127.0.0.1:8082/health` |
| **5** | Regulatory RAG (BGE-M3) | `8083` | HTTP / FastAPI | `GET http://127.0.0.1:8083/health` |
| **6** | Emergency Oracle Monitor | `8091` | HTTP / FastAPI | `GET http://127.0.0.1:8091/oracle/health` |
| **7** | Financial Signing Bridge | `9080` | HTTP / Java SDK | `GET http://127.0.0.1:9080/health` |
| **8** | Governance Bridge | `9090` | HTTP / Java SDK | `GET http://127.0.0.1:9090/health` |
| **9** | Independent Verifier API | `8090` | HTTP / FastAPI | `GET http://127.0.0.1:8090/health` |
| **10** | FastAPI Autonomous Gateway | `8000` | HTTP / REST | `GET http://127.0.0.1:8000/api/v1/health` |
| **11** | Vue 3 Web Command Center | `5173` | HTTP / Vite | `GET http://127.0.0.1:5173/` |

---

## 3. Ordered Service Startup Sequence

### Step 1: PostgreSQL & pgvector (Semantic Memory)
```bash
docker run -d \
  --name aegis-postgres \
  -e POSTGRES_DB=aegis_memory \
  -e POSTGRES_USER=aegis \
  -e POSTGRES_PASSWORD=aegis_secure_password \
  -p 5433:5432 \
  pgvector/pgvector:pg16
```

### Step 2: FISCO BCOS 4-Node PBFT Network
```bash
cd blockchain/network/127.0.0.1
bash start_all.sh

# Verify all 4 nodes are producing blocks
for p in 20200 20201 20202 20203; do
  curl -k -s \
    --cert sdk/sdk.crt --key sdk/sdk.key \
    -H "Content-Type: application/json" \
    -X POST --data '{"jsonrpc":"2.0","method":"getBlockNumber","params":["group0"],"id":1}' \
    https://127.0.0.1:$p | jq .
done
```

### Step 3: Main Qwen3-8B Engine (:8081)
```bash
llama-server \
  -m models/qwen3-8b-instruct.Q4_K_M.gguf \
  --host 127.0.0.1 \
  --port 8081 \
  --ctx-size 8192 \
  --n-gpu-layers 99
```

### Step 4: Qwen3Guard Safety Gate (:8082)
```bash
source .venv/bin/activate
set -a && source apps/guard/runtime/guard.env && set +a
uvicorn main:app --app-dir apps/guard/src --host 127.0.0.1 --port 8082
```

### Step 5: BGE-M3 Regulatory RAG Vector Service (:8083)
```bash
source .venv/bin/activate
set -a && source apps/rag/runtime/rag.env && set +a
uvicorn main:app --app-dir apps/rag/src --host 127.0.0.1 --port 8083
```

### Step 6: Autonomous Emergency Oracle Monitor (:8091)
```bash
source .venv/bin/activate
set -a && source oracle/monitor/runtime/oracle.env && set +a
uvicorn main:app --app-dir oracle/monitor/src --host 127.0.0.1 --port 8091
```

### Step 7: Isolated Financial Signing Bridge (:9080)
```bash
export JAVA_HOME="/opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
set -a && source apps/fisco-bridge/runtime/bridge.env && set +a
export FISCO_CONFIG_PATH="$PWD/apps/fisco-bridge/runtime/config.toml"
mvn -q -f apps/fisco-bridge/pom.xml exec:java -Dexec.mainClass=com.aegis.bridge.BridgeServer
```

### Step 8: Dedicated Governance Bridge (:9090)
```bash
export JAVA_HOME="/opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
set -a && source oracle/verifier/runtime/governance-bridge.env && set +a
export FISCO_CONFIG_PATH="$PWD/apps/fisco-bridge/runtime/config.toml"
mvn -q -f apps/fisco-bridge/pom.xml exec:java -Dexec.mainClass=com.aegis.verifier.GovernanceServer
```

### Step 9: Independent Sandbox Verifier API (:8090)
```bash
source .venv/bin/activate
set -a && source sandbox/verification/runtime/verifier.env && set +a
export PYTHONPATH="$PWD/apps/api/src:$PWD/oracle/verifier/api"
uvicorn main:app --app-dir oracle/verifier/api --host 127.0.0.1 --port 8090
```

### Step 10: FastAPI Autonomous Financial Gateway (:8000)
```bash
source .venv/bin/activate
set -a && source apps/api/runtime/api.env && set +a
export PYTHONPATH="$PWD/apps/api/src"
uvicorn aegis_api.main:app --host 127.0.0.1 --port 8000
```

### Step 11: Vue 3 Web Command Center (:5173)
```bash
cd apps/web
npm install
npm run dev -- --host 127.0.0.1 --port 5173
```

---

## 4. End-to-End Health Verification Script
To verify the entire ecosystem in one automated pass:
```bash
# Verify Python test suite
source .venv/bin/activate
PYTHONPATH="$PWD/apps/api/src" pytest -q tests/

# Verify cryptographic hash invariants
python scripts/verify_phase_i_evidence.py

# Verify port listeners
for p in 8000 8081 8082 8083 8090 8091 9080 9090 5433 20200 20201 20202 20203 5173; do
  printf "Port %s: " "$p"
  lsof -nP -iTCP:$p -sTCP:LISTEN | tail -1
done
```
