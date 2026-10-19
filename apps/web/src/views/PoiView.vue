<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Canonical Proof-of-Intent (PoI)</h1>
        <p class="page-subtitle">Cryptographic commitment binding user request, AI decision, risk score, legal policy, and pre-execution evidence.</p>
      </div>
      <div class="header-actions">
        <button class="btn-copy" @click="copyHash">
          {{ copied ? 'Copied!' : 'Copy PoI Hash' }}
        </button>
        <button class="btn-json" @click="showJson = !showJson">
          {{ showJson ? 'Hide Canonical JSON' : 'View Canonical JSON' }}
        </button>
      </div>
    </div>

    <!-- PoI Hash Summary Banner -->
    <div class="card hash-card">
      <div class="hash-label">Canonical PoI Hash (SHA256 of Canonical Zero-Float JSON)</div>
      <div class="hash-val mono">{{ poiHash }}</div>
      <div class="hash-sub">Registered on-chain in FISCO BCOS PoIRegistry contract</div>
    </div>

    <!-- Canonical JSON Modal / Box -->
    <div v-if="showJson" class="card json-card">
      <div class="card-header">
        <span class="card-title">Canonical JSON Representation (Strict Zero Float)</span>
        <span class="badge badge-neutral">RFC8785 Equivalence</span>
      </div>
      <pre class="json-code mono">{{ JSON.stringify(poiData, null, 2) }}</pre>
    </div>

    <!-- 18 Fields Grouped Logically -->
    <div class="poi-grid">
      <!-- 1. Identity Group -->
      <div class="card group-card">
        <div class="group-header">
          <span class="group-title">1. Identity</span>
          <span class="badge badge-info">PROVENANCE</span>
        </div>
        <div class="field-list">
          <div class="field-item">
            <span class="field-name">intentId</span>
            <span class="field-val mono">{{ poiData.intentId }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">userRequestHash</span>
            <span class="field-val mono">{{ poiData.userRequestHash }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">leaderAgentId</span>
            <span class="field-val">{{ poiData.leaderAgentId }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">modelVersion</span>
            <span class="field-val">{{ poiData.modelVersion }}</span>
          </div>
        </div>
      </div>

      <!-- 2. Action Group -->
      <div class="card group-card">
        <div class="group-header">
          <span class="group-title">2. Action</span>
          <span class="badge badge-info">EXECUTION TARGET</span>
        </div>
        <div class="field-list">
          <div class="field-item">
            <span class="field-name">action</span>
            <span class="field-val badge badge-success">{{ poiData.action }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">asset</span>
            <span class="field-val">{{ poiData.asset }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">amount</span>
            <span class="field-val">{{ poiData.amount }} minor units (2.00 ACNY)</span>
          </div>
          <div class="field-item">
            <span class="field-name">recipient</span>
            <span class="field-val mono">{{ poiData.recipient }}</span>
          </div>
        </div>
      </div>

      <!-- 3. Risk Group -->
      <div class="card group-card">
        <div class="group-header">
          <span class="group-title">3. Risk Evaluation</span>
          <span class="badge badge-success">INVARIANTS</span>
        </div>
        <div class="field-list">
          <div class="field-item">
            <span class="field-name">riskScore</span>
            <span class="field-val">{{ poiData.riskScore }} BPS (4.53%)</span>
          </div>
          <div class="field-item">
            <span class="field-name">riskClass</span>
            <span class="field-val badge badge-success">{{ poiData.riskClass }}</span>
          </div>
        </div>
      </div>

      <!-- 4. Policy Group -->
      <div class="card group-card">
        <div class="group-header">
          <span class="group-title">4. Policy Commitment</span>
          <span class="badge badge-info">COMPOSITE MANIFEST</span>
        </div>
        <div class="field-list">
          <div class="field-item">
            <span class="field-name">policyVersion</span>
            <span class="field-val mono">{{ poiData.policyVersion }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">policyHash</span>
            <span class="field-val mono">{{ poiData.policyHash }}</span>
          </div>
        </div>
      </div>

      <!-- 5. Evidence Group -->
      <div class="card group-card">
        <div class="group-header">
          <span class="group-title">5. Evidence Binding</span>
          <span class="badge badge-success">PoI == AUDIT</span>
        </div>
        <div class="field-list">
          <div class="field-item">
            <span class="field-name">evidenceHash</span>
            <span class="field-val mono">{{ poiData.evidenceHash }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">explanationHash</span>
            <span class="field-val mono">{{ poiData.explanationHash }}</span>
          </div>
        </div>
      </div>

      <!-- 6. Validity & Decision Group -->
      <div class="card group-card">
        <div class="group-header">
          <span class="group-title">6. Validity & Final Decision</span>
          <span class="badge badge-success">DECISION</span>
        </div>
        <div class="field-list">
          <div class="field-item">
            <span class="field-name">timestamp</span>
            <span class="field-val mono">{{ poiData.timestamp }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">expiry</span>
            <span class="field-val mono">{{ poiData.expiry }} (timestamp + 300s)</span>
          </div>
          <div class="field-item">
            <span class="field-name">nonce</span>
            <span class="field-val mono">{{ poiData.nonce }}</span>
          </div>
          <div class="field-item">
            <span class="field-name">decision</span>
            <span class="field-val badge badge-success">{{ poiData.decision }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const poiHash = ref('0xf1ba21bdac22b01b86defdff8281608affe5acfd827bba66fd56ef00427ddd2b')
const copied = ref(false)
const showJson = ref(false)

const poiData = ref({
  intentId: "0x6e90a3ca0e364d491558410c7435cce2f954e1c77a5ced16a23f1a2aa82aac2e",
  userRequestHash: "0xb6780c10a30b42f3607730ea4ca76503c1533ce5402a11b0e3eefdae8fc7671b",
  leaderAgentId: "aegis-leader",
  modelVersion: "qwen-local",
  action: "TRANSFER",
  asset: "ACNY",
  amount: 200,
  recipient: "0x1000000000000000000000000000000000000002",
  riskScore: 453,
  riskClass: "LOW",
  policyVersion: "aegis-effective-financial-policy-v1",
  policyHash: "0xc7a89fd05228298a42a48f113e168f044e919fabdf240178db3c49a628cfdf38",
  evidenceHash: "0xf590d65dc8d10a797ae09c70af0abe1b3349725d1ed1b5bb8e71fabe5ce91224",
  explanationHash: "0x66da2a0e44b9230542072019009339963095dcc3771144882233991100223344",
  timestamp: 1792159000,
  expiry: 1792159300,
  nonce: "0x77bb449911002233445566778899aabbccddeeff00112233445566778899aabb",
  decision: "APPROVE"
})

function copyHash() {
  navigator.clipboard.writeText(poiHash.value)
  copied.value = true
  setTimeout(() => { copied.value = false }, 1800)
}
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

.header-actions {
  display: flex;
  gap: 10px;
}

.btn-copy, .btn-json {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  color: var(--text-main);
  padding: 6px 14px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.btn-copy:hover, .btn-json:hover {
  background: var(--bg-surface-hover);
  border-color: var(--primary);
}

.hash-card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-left: 4px solid var(--primary);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.hash-label {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-dim);
}

.hash-val {
  font-size: 16px;
  font-weight: 700;
  color: #38bdf8;
  word-break: break-all;
}

.hash-sub {
  font-size: 11px;
  color: var(--text-muted);
}

.json-card {
  background: #f8fafc;
  border: 1px solid var(--border);
}

.json-code {
  font-size: 12px;
  color: #09090b;
  line-height: 1.45;
  overflow-x: auto;
  max-height: 280px;
}

.poi-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.group-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-subtle);
  padding-bottom: 8px;
}

.group-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-main);
}

.field-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.field-name {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-dim);
  font-family: var(--font-mono);
}

.field-val {
  font-size: 12px;
  color: var(--text-main);
  word-break: break-all;
}
</style>
