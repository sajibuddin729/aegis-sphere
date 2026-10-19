<template>
  <div class="page-container">
    <!-- Header with Filters -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Financial Ledger & Activity</h1>
        <p class="page-subtitle">Immutable on-chain execution ledger anchored to FISCO BCOS consortium group 0.</p>
      </div>

      <div class="filter-pills">
        <button 
          v-for="filter in filters" 
          :key="filter.key"
          class="filter-pill"
          :class="{ active: currentFilter === filter.key }"
          @click="currentFilter = filter.key"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- Activity Ledger Table -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">
          <History :size="14" />
          Consortium Transaction Records
        </span>
        <span class="badge badge-neutral">{{ filteredActivities.length }} TRANSACTIONS</span>
      </div>

      <table class="inst-table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Counterparty / Description</th>
            <th>Asset & Amount</th>
            <th>Status</th>
            <th>Block</th>
            <th>TxHash</th>
            <th>Date & Time</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tx in filteredActivities" :key="tx.id">
            <td>
              <span class="badge" :class="getTypeBadge(tx.type)">
                {{ tx.type }}
              </span>
            </td>
            <td>
              <div class="entity-desc">
                <strong>{{ tx.title }}</strong>
                <span class="text-dim">{{ tx.recipientName }}</span>
              </div>
            </td>
            <td>
              <strong>{{ tx.amount }}</strong>
              <small class="text-dim"> {{ tx.asset }}</small>
            </td>
            <td>
              <span class="badge badge-success">
                <Check :size="10" />
                {{ tx.status }}
              </span>
            </td>
            <td class="mono">{{ tx.blockNumber }}</td>
            <td class="mono">
              <span class="text-blue">{{ tx.txHash ? tx.txHash.slice(0, 10) + '...' + tx.txHash.slice(-6) : 'N/A' }}</span>
            </td>
            <td class="text-dim">{{ tx.timestamp }}</td>
            <td>
              <button class="btn-details" @click="openDetails(tx)">
                View Details
                <ChevronRight :size="12" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Transaction Details Slide-Over Drawer -->
    <div v-if="selectedTx" class="drawer-backdrop" @click.self="selectedTx = null">
      <div class="drawer-panel">
        <div class="drawer-header">
          <div class="drawer-title">Cryptographic Transaction Verification</div>
          <button class="btn-ghost" @click="selectedTx = null">
            <X :size="16" />
          </button>
        </div>

        <div class="drawer-body">
          <!-- Status Banner -->
          <div class="tx-summary-banner">
            <div class="sum-top">
              <span class="badge badge-success">{{ selectedTx.status }}</span>
              <span class="text-dim">{{ selectedTx.timestamp }}</span>
            </div>
            <h3 class="sum-title">{{ selectedTx.title }}</h3>
            <div class="sum-amount">{{ selectedTx.amount }} <small>{{ selectedTx.asset }}</small></div>
          </div>

          <!-- Cryptographic Bindings List -->
          <div class="drawer-section">
            <span class="section-title">Cryptographic Commitments</span>
            
            <div class="binding-item">
              <span class="binding-label">Proof-of-Intent (PoI) Hash</span>
              <div class="hash-row">
                <span class="mono hash-text">{{ selectedTx.poiHash }}</span>
                <button class="btn-copy" @click="copy(selectedTx.poiHash, 'poi')" title="Copy Hash">
                  <Check v-if="copiedField === 'poi'" class="text-green" :size="12" />
                  <Copy v-else :size="12" />
                </button>
              </div>
            </div>

            <div class="binding-item">
              <span class="binding-label">Universal Evidence Hash</span>
              <div class="hash-row">
                <span class="mono hash-text">{{ selectedTx.evidenceHash }}</span>
                <button class="btn-copy" @click="copy(selectedTx.evidenceHash, 'ev')" title="Copy Hash">
                  <Check v-if="copiedField === 'ev'" class="text-green" :size="12" />
                  <Copy v-else :size="12" />
                </button>
              </div>
            </div>

            <div class="binding-item">
              <span class="binding-label">Active Financial Policy Version</span>
              <span class="mono hash-text">{{ selectedTx.policyVersion }}</span>
            </div>
          </div>

          <!-- Ordering Verification -->
          <div v-if="selectedTx.auditBlock" class="drawer-section">
            <span class="section-title">Pre-Execution Block Ordering Proof</span>
            <div class="ordering-box">
              <div class="ord-node">
                <span class="ord-tag">1. AUDIT ANCHOR</span>
                <div class="ord-meta">
                  <span class="badge badge-info">BLOCK {{ selectedTx.auditBlock }}</span>
                  <span class="mono ord-hash">{{ selectedTx.auditTxHash.slice(0, 22) }}...</span>
                </div>
              </div>
              <div class="ord-arrow">↓ Audit Block {{ selectedTx.auditBlock }} &lt; Financial Block {{ selectedTx.blockNumber }}</div>
              <div class="ord-node">
                <span class="ord-tag">2. FINANCIAL EXECUTION</span>
                <div class="ord-meta">
                  <span class="badge badge-success">BLOCK {{ selectedTx.blockNumber }}</span>
                  <span class="mono ord-hash">{{ selectedTx.txHash.slice(0, 22) }}...</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Blockchain Target -->
          <div class="drawer-section">
            <span class="section-title">Consortium Network Target</span>
            <div class="target-table">
              <div class="target-row">
                <span>Consortium Group:</span>
                <strong class="mono">group0</strong>
              </div>
              <div class="target-row">
                <span>Consensus Engine:</span>
                <strong>PBFT (4 Validators, f=1)</strong>
              </div>
              <div class="target-row">
                <span>Finality Guarantee:</span>
                <span class="text-green">Immediate Deterministic Finality</span>
              </div>
            </div>
          </div>
        </div>

        <div class="drawer-footer">
          <button class="btn-secondary" @click="selectedTx = null">Close Details</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  History, 
  Check, 
  ChevronRight, 
  X, 
  Copy 
} from 'lucide-vue-next'
import { activityList } from '../services/api.js'

const currentFilter = ref('ALL')
const selectedTx = ref(null)
const copiedField = ref('')

const filters = [
  { key: 'ALL', label: 'All Records' },
  { key: 'TRANSFER', label: 'Transfers' },
  { key: 'REBALANCE', label: 'Portfolio Rebalance' },
  { key: 'ZK_PROOF', label: 'ZK Privacy' },
  { key: 'GOVERNANCE', label: 'Governance' }
]

const filteredActivities = computed(() => {
  if (currentFilter.value === 'ALL') return activityList.value
  return activityList.value.filter(tx => tx.type === currentFilter.value)
})

function getTypeBadge(type) {
  if (type === 'TRANSFER') return 'badge-info'
  if (type === 'REBALANCE') return 'badge-neutral'
  if (type === 'ZK_PROOF') return 'badge-warning'
  if (type === 'GOVERNANCE') return 'badge-success'
  return 'badge-neutral'
}

function openDetails(tx) {
  selectedTx.value = tx
}

function copy(text, field) {
  if (!text) return
  navigator.clipboard.writeText(text)
  copiedField.value = field
  setTimeout(() => { copiedField.value = '' }, 1500)
}
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 19px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.page-subtitle {
  font-size: 12.5px;
  color: var(--text-muted);
  margin-top: 3px;
}

.filter-pills {
  display: flex;
  gap: 6px;
}

.filter-pill {
  background: #ffffff;
  border: 1px solid var(--border);
  color: #334155;
  font-size: 11.5px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 4px;
}

.filter-pill:hover {
  background: #f4f4f5;
  color: #09090b;
}

.filter-pill.active {
  background: #09090b;
  border-color: #09090b;
  color: #ffffff;
  font-weight: 600;
}

.entity-desc {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.btn-details {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #ffffff;
  border: 1px solid var(--border);
  color: #09090b;
  font-size: 11px;
  font-weight: 600;
  padding: 5px 10px;
  border-radius: 4px;
}

.btn-details:hover {
  background: #f4f4f5;
  border-color: #09090b;
  color: #09090b;
}

.tx-summary-banner {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sum-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
}

.sum-title {
  font-size: 14px;
  font-weight: 700;
  color: #09090b;
}

.sum-amount {
  font-size: 20px;
  font-weight: 700;
  color: #09090b;
}

.drawer-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-title {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
  letter-spacing: 0.5px;
}

.binding-item {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.binding-label {
  font-size: 10.5px;
  color: #64748b;
  font-weight: 700;
  text-transform: uppercase;
}

.hash-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.hash-text {
  font-size: 11.5px;
  color: #09090b;
  font-weight: 500;
  word-break: break-all;
}

.btn-copy {
  background: transparent;
  color: #64748b;
  padding: 4px;
  border-radius: 3px;
  flex-shrink: 0;
}

.btn-copy:hover {
  color: #09090b;
  background: #f4f4f5;
}

.ordering-box {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ord-node {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ord-tag {
  font-size: 10px;
  font-weight: 700;
  color: #64748b;
}

.ord-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 11px;
}

.ord-hash {
  color: #09090b;
}

.ord-arrow {
  font-size: 11px;
  font-weight: 700;
  color: #166534;
  text-align: center;
}

.target-table {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
}

.target-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.target-row span {
  color: #64748b;
}

.target-row strong {
  color: #09090b;
}

.text-blue { color: #1e3a8a; }
.text-green { color: #166534; }
.text-dim { color: #64748b; }
</style>
