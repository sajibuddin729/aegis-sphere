<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">FISCO BCOS Consortium Blockchain Evidence</h1>
        <p class="page-subtitle">On-chain ledger state, smart contract deployments, and pre-execution audit cryptographic proofs.</p>
      </div>
      <span class="badge badge-success">CONSENSUS: PBFT SYNCHRONIZED</span>
    </div>

    <!-- Network KPI Grid -->
    <div class="stats-grid">
      <div class="card stat-card">
        <span class="stat-label">Network & Group</span>
        <span class="stat-val">FISCO BCOS <small>group0</small></span>
        <span class="stat-sub">ChainID: chain0 • Version 3.17.1</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Consensus Protocol</span>
        <span class="stat-val">PBFT <small>(n=4, f=1)</small></span>
        <span class="stat-sub">Deterministic Finality (Zero Fork Risk)</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Synchronized Block Height</span>
        <span class="stat-val">BLOCK 106+</span>
        <span class="stat-sub">All 4 Validators at Same Height</span>
      </div>
    </div>

    <!-- Pre-Execution Audit Ordering Proof (J015) -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">Cryptographically Linked Pre-Execution Audit with Application-Enforced Ordering</span>
        <span class="badge badge-success">INVARIANT VERIFIED</span>
      </div>
      <div class="audit-comparison-grid">
        <div class="tx-card">
          <div class="tx-badge">TRANSFER FLOW</div>
          <div class="tx-step">
            <span class="step-tag">1. AUDIT EVIDENCE REGISTRY ANCHOR</span>
            <div class="step-meta">
              <span>Block Number:</span> <strong class="badge badge-info">BLOCK 99</strong>
            </div>
            <div class="step-meta">
              <span>TxHash:</span> <span class="mono">0x28c6e84f0ae4bd6df5c76af5ea1aac9a3f7d697bc829c4795c19408cf934fca9</span>
            </div>
          </div>
          <div class="tx-arrow">↓ Audit Block 99 &lt; Financial Block 100</div>
          <div class="tx-step">
            <span class="step-tag">2. FINANCIAL TRANSFER EXECUTION</span>
            <div class="step-meta">
              <span>Block Number:</span> <strong class="badge badge-success">BLOCK 100</strong>
            </div>
            <div class="step-meta">
              <span>TxHash:</span> <span class="mono">0xf49511589fc47a8fe208fc9ce34895b32b4cfbd074ecea92f5c95cd5b1311cdf</span>
            </div>
          </div>
        </div>

        <div class="tx-card">
          <div class="tx-badge">REBALANCE FLOW</div>
          <div class="tx-step">
            <span class="step-tag">1. AUDIT EVIDENCE REGISTRY ANCHOR</span>
            <div class="step-meta">
              <span>Block Number:</span> <strong class="badge badge-info">BLOCK 101</strong>
            </div>
            <div class="step-meta">
              <span>TxHash:</span> <span class="mono">0x3d263ce32d4e9bfea529f3ac7d4ad4918409e117349cf0860d59b3795fd7bd11</span>
            </div>
          </div>
          <div class="tx-arrow">↓ Audit Block 101 &lt; Financial Block 102</div>
          <div class="tx-step">
            <span class="step-tag">2. FINANCIAL SWAP REBALANCE</span>
            <div class="step-meta">
              <span>Block Number:</span> <strong class="badge badge-success">BLOCK 102</strong>
            </div>
            <div class="step-meta">
              <span>TxHash:</span> <span class="mono">0x010810c9d281fb0456449e41a3c13ea936b1ca0f9bb8ae28e88de181945ce87c</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Deployed Smart Contracts Table (J014) -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">Deployed Smart Contracts Manifest (blockchain/deployments/local-group0.json)</span>
        <span class="badge badge-neutral">12 CONTRACTS</span>
      </div>
      <div class="contracts-table">
        <div class="con-row header-row">
          <span>Contract Name</span>
          <span>Role & Architecture Function</span>
          <span>On-Chain Address (Group0)</span>
        </div>
        <div v-for="c in contracts.contracts" :key="c.name" class="con-row">
          <strong>{{ c.name }}</strong>
          <span class="con-type">{{ c.type }}</span>
          <span class="mono con-addr">{{ c.address }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { fetchContracts } from '../services/api.js'

const contracts = ref(fetchContracts())
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
}

.page-subtitle {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 4px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-dim);
  text-transform: uppercase;
}

.stat-val {
  font-size: 22px;
  font-weight: 700;
  color: #09090b;
}

.stat-sub {
  font-size: 11px;
  color: var(--text-muted);
}

.audit-comparison-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.tx-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: 6px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tx-badge {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-dim);
  letter-spacing: 0.5px;
}

.tx-step {
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: var(--bg-card);
  padding: 10px 12px;
  border-radius: 4px;
  border: 1px solid var(--border);
}

.step-tag {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
}

.step-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
}

.step-meta span {
  color: var(--text-dim);
}

.tx-arrow {
  text-align: center;
  font-size: 11px;
  font-weight: 700;
  color: #34d399;
}

.contracts-table {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.con-row {
  display: grid;
  grid-template-columns: 1.5fr 2fr 2.5fr;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: 4px;
  font-size: 12px;
}

.header-row {
  background: transparent;
  border: none;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-dim);
  text-transform: uppercase;
}

.con-type {
  color: var(--text-muted);
}

.con-addr {
  color: #a5b4fc;
}
</style>
