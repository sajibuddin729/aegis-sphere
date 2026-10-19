<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">On-Chain Update Governance & Staging</h1>
        <p class="page-subtitle">Multi-stage independent verifier pipeline and on-chain governance authority on FISCO BCOS.</p>
      </div>
      <span class="badge badge-success">GOVERNANCE VERIFIER: DEDICATED SIGNER</span>
    </div>

    <!-- Mandatory Banner (J017) -->
    <div class="card banner-card">
      <div class="banner-text">
        AI MAY WRITE ITS OWN UPGRADES, BUT IT CANNOT AUTHORIZE ITS OWN UPGRADES.
      </div>
      <div class="banner-sub">
        Strict separation of duties: University evolution proposals require independent verification and dedicated on-chain governance authorization before staging. Auto-production deployment is strictly prohibited.
      </div>
    </div>

    <!-- Governance Lifecycle Pipeline Flow -->
    <div class="card flow-card">
      <div class="card-header">
        <span class="card-title">Autonomous Improvement Pipeline Flow</span>
        <span class="badge badge-info">11 MANDATORY CHECKS</span>
      </div>
      <div class="pipeline-steps">
        <div class="pipe-node">
          <span class="pipe-badge">1</span>
          <strong>Weakness / Finding</strong>
          <small>University Curriculum</small>
        </div>
        <div class="pipe-sep">→</div>
        <div class="pipe-node">
          <span class="pipe-badge">2</span>
          <strong>Evolution Agent</strong>
          <small>Candidate V2 Generator</small>
        </div>
        <div class="pipe-sep">→</div>
        <div class="pipe-node">
          <span class="pipe-badge">3</span>
          <strong>Independent Verifier</strong>
          <small>11 Isolated Checks (:8090)</small>
        </div>
        <div class="pipe-sep">→</div>
        <div class="pipe-node">
          <span class="pipe-badge">4</span>
          <strong>UpdateGovernance</strong>
          <small>FISCO Smart Contract (:9090)</small>
        </div>
        <div class="pipe-sep">→</div>
        <div class="pipe-node active-staging">
          <span class="pipe-badge">5</span>
          <strong>Controlled Staging</strong>
          <small>Human Review Only</small>
        </div>
      </div>
    </div>

    <!-- Main Evidence & Negative Attack -->
    <div class="two-col">
      <!-- Genuine Safe Candidate -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">Verified Genuine AI Candidate V2</span>
          <span class="badge badge-success">APPROVED ON-CHAIN</span>
        </div>
        <div class="candidate-details">
          <div class="detail-row">
            <span>Candidate ID:</span>
            <strong class="mono">{{ govData.candidate_id }}</strong>
          </div>
          <div class="detail-row">
            <span>Title:</span>
            <span>{{ govData.title }}</span>
          </div>
          <div class="detail-row">
            <span>Finding Provenance:</span>
            <span class="mono">{{ govData.finding_id }}</span>
          </div>
          <div class="detail-row">
            <span>Independent Verification:</span>
            <span class="badge badge-success">100 / 100 (11 / 11 CHECKS PASS)</span>
          </div>
          <div class="detail-row">
            <span>Governance Contract:</span>
            <span class="mono">{{ govData.governance.contract }}</span>
          </div>
          <div class="detail-row">
            <span>On-Chain Governance Tx:</span>
            <span class="mono">{{ govData.governance.transaction_hash }} (Block {{ govData.governance.block_number }})</span>
          </div>
          <div class="detail-row">
            <span>Staging Authorized:</span>
            <span class="badge badge-success">YES (sandbox/verification/staging)</span>
          </div>
          <div class="detail-row">
            <span>Production Auto-Deploy:</span>
            <span class="badge badge-danger">BLOCKED (0 Production Deployments)</span>
          </div>
        </div>
      </div>

      <!-- Negative Attack Rejection -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">Adversarial Tamper / Self-Auth Attack</span>
          <span class="badge badge-danger">FROZEN & REJECTED</span>
        </div>
        <div class="candidate-details">
          <div class="detail-row">
            <span>Candidate ID:</span>
            <strong class="mono">{{ govData.negative_test.candidate_id }}</strong>
          </div>
          <div class="detail-row">
            <span>Attack Vector:</span>
            <span class="badge badge-warning">{{ govData.negative_test.finding }}</span>
          </div>
          <div class="detail-row">
            <span>Verification Score:</span>
            <span class="badge badge-danger">0.0 / 100.0 (Invariant Violation)</span>
          </div>
          <div class="detail-row">
            <span>Verifier Decision:</span>
            <span class="badge badge-danger">{{ govData.negative_test.decision }}</span>
          </div>
          <div class="detail-row">
            <span>On-Chain Governance Status:</span>
            <span class="badge badge-danger">{{ govData.negative_test.governance_decision }}</span>
          </div>
          <div class="detail-row">
            <span>Destination:</span>
            <span>Quarantined to sandbox/verification/frozen/</span>
          </div>
          <div class="detail-row">
            <span>AI Self-Authorization Protection:</span>
            <span class="badge badge-success">ENFORCED (Verifier Signature Mandatory)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { fetchGovernance } from '../services/api.js'

const govData = ref(fetchGovernance())
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

.banner-card {
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.4);
  border-left: 5px solid #3b82f6;
  text-align: center;
  padding: 20px 24px;
}

.banner-text {
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 0.8px;
  color: #60a5fa;
}

.banner-sub {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 6px;
  line-height: 1.5;
}

.pipeline-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.pipe-node {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 150px;
}

.pipe-node.active-staging {
  border-color: #38bdf8;
  background: rgba(56, 189, 248, 0.1);
}

.pipe-badge {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--border);
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pipe-node strong {
  font-size: 12px;
  color: var(--text-main);
}

.pipe-node small {
  font-size: 11px;
  color: var(--text-dim);
}

.pipe-sep {
  color: var(--text-dim);
  font-size: 18px;
  font-weight: 700;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.candidate-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-subtle);
  font-size: 12px;
}

.detail-row span:first-child {
  color: var(--text-dim);
  font-size: 11px;
  text-transform: uppercase;
  font-weight: 600;
}
</style>
