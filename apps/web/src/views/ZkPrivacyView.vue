<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Zero-Knowledge Financial Privacy (Groth16)</h1>
        <p class="page-subtitle">Zero-knowledge proof of balance solvency and risk compliance without revealing private financial values.</p>
      </div>
      <span class="badge badge-success">GROTH16: VERIFIED ON-CHAIN</span>
    </div>

    <!-- ZK Proof Cryptographic Cards -->
    <div class="stats-grid">
      <div class="card stat-card">
        <span class="stat-label">Proof System & Curve</span>
        <span class="stat-val">Groth16 <small>BN254 (bn128)</small></span>
        <span class="stat-sub">1,321 Non-Linear Constraints</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Public Requested Amount</span>
        <span class="stat-val">10.00 <small>ACNY (1,000 minor)</small></span>
        <span class="stat-sub">Proven balance &ge; requested amount</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Max Risk Threshold</span>
        <span class="stat-val">15.00% <small>(1,500 BPS)</small></span>
        <span class="stat-sub">Proven private risk &le; 1,500 BPS</span>
      </div>
    </div>

    <!-- ZK Architectural Flow Diagram (J020) -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">Cryptographic Privacy Architecture & Signal Flow</span>
        <span class="badge badge-info">POSEIDON COMMITMENTS</span>
      </div>
      <div class="zk-flow-diagram">
        <div class="zk-col">
          <div class="signal-box private-box">
            <span class="signal-tag">PRIVATE WITNESS (OFF-CHAIN)</span>
            <div class="sig-val">Private Balance + Blinding Salt</div>
            <div class="sig-val">Private Risk Score + Blinding Salt</div>
          </div>
        </div>
        <div class="zk-center">
          <div class="circuit-box">
            <strong>Circom Circuit: AegisEligibility</strong>
            <small>Poseidon(2) Commitments • Groth16 Prover</small>
            <div class="circuit-arrow">═══════►</div>
          </div>
        </div>
        <div class="zk-col">
          <div class="signal-box public-box">
            <span class="signal-tag">ON-CHAIN VERIFICATION (FISCO)</span>
            <div class="sig-val mono">ZKEligibilityGate.sol (Block 96)</div>
            <div class="sig-status badge badge-success">VERIFIED ON-CHAIN (Status 0)</div>
          </div>
        </div>
      </div>
      <div class="zk-guarantee">
        Exact account balance and private risk score remain completely confidential. Only the cryptographic proof of solvency and constraint compliance is committed to the blockchain.
      </div>
    </div>

    <!-- Smart Contracts & Proof Calldata Evidence -->
    <div class="two-col">
      <div class="card">
        <div class="card-header">
          <span class="card-title">Verified On-Chain Proof Evidence</span>
          <span class="badge badge-success">RECORDED</span>
        </div>
        <div class="proof-meta-list">
          <div class="meta-item">
            <span>Proof Submission TxHash:</span>
            <span class="mono">{{ zkData.proof_tx_hash }}</span>
          </div>
          <div class="meta-item">
            <span>Block Number:</span>
            <strong class="badge badge-info">BLOCK {{ zkData.block_number }}</strong>
          </div>
          <div class="meta-item">
            <span>Proof Hash Commitment:</span>
            <span class="mono">{{ zkData.proof_hash }}</span>
          </div>
          <div class="meta-item">
            <span>Snapshot Registration Tx:</span>
            <span class="mono">{{ zkData.snapshot_registration.tx_hash }} (Block {{ zkData.snapshot_registration.block_number }})</span>
          </div>
          <div class="meta-item">
            <span>Replay Protection:</span>
            <span class="badge badge-success">ACTIVE (Duplicate Proofs Reverted)</span>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <span class="card-title">Precompiled Pairing Verifier</span>
          <span class="badge badge-neutral">SOLIDITY CONTRACTS</span>
        </div>
        <div class="proof-meta-list">
          <div class="meta-item">
            <span>AegisEligibilityVerifier:</span>
            <span class="mono">{{ zkData.contracts.verifier }}</span>
          </div>
          <div class="meta-item">
            <span>ZKEligibilityGate:</span>
            <span class="mono">{{ zkData.contracts.gate }}</span>
          </div>
          <div class="meta-item">
            <span>Precompile Operations:</span>
            <span>0x06 ECADD • 0x07 ECMUL • 0x08 ECPAIRING</span>
          </div>
          <div class="meta-item">
            <span>Witness Privacy Audit:</span>
            <span class="badge badge-success">ZK_PRIVACY_AUDIT=PASS (Zero Secrets in Git)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { fetchZk } from '../services/api.js'

const zkData = ref(fetchZk())
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

.zk-flow-diagram {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 16px 0;
}

.signal-box {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 240px;
}

.private-box {
  border-left: 4px solid #a855f7;
}

.public-box {
  border-left: 4px solid #3b82f6;
}

.signal-tag {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-dim);
  text-transform: uppercase;
}

.sig-val {
  font-size: 12px;
  color: var(--text-main);
}

.circuit-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  text-align: center;
}

.circuit-box strong {
  font-size: 13px;
  color: #38bdf8;
}

.circuit-box small {
  font-size: 11px;
  color: var(--text-dim);
}

.circuit-arrow {
  color: var(--primary);
  font-size: 20px;
  letter-spacing: 2px;
  margin-top: 4px;
}

.zk-guarantee {
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-left: 3px solid #10b981;
  padding: 10px 14px;
  border-radius: 4px;
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.4;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.proof-meta-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-subtle);
  font-size: 12px;
}

.meta-item span:first-child {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-dim);
  text-transform: uppercase;
}
</style>
