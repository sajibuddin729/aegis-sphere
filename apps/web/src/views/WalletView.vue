<template>
  <div class="page-container">
    <!-- Header with Primary Action -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Agent Treasury Wallet</h1>
        <p class="page-subtitle">Autonomous financial custody on FISCO BCOS with cryptographically isolated signing protection.</p>
      </div>
      <div class="header-actions">
        <button class="btn-primary" @click="showTransferModal = true">
          <Send :size="14" />
          Initiate Transfer
        </button>
      </div>
    </div>

    <!-- Balance & Policy Limit Stats -->
    <div class="stats-grid">
      <div class="card stat-card">
        <div class="stat-top">
          <span class="stat-label">Available ACNY Balance</span>
          <Wallet class="stat-icon text-blue" :size="16" />
        </div>
        <div class="stat-val">{{ wallet.balanceAcny }} <span class="stat-unit">ACNY</span></div>
        <div class="stat-sub">Simulated e-CNY Consortium Reserve</div>
      </div>

      <div class="card stat-card">
        <div class="stat-top">
          <span class="stat-label">Daily Remaining Allowance</span>
          <Clock class="stat-icon text-amber" :size="16" />
        </div>
        <div class="stat-val">{{ wallet.remainingAllowanceAcny }} <span class="stat-unit">/ {{ wallet.dailyLimitAcny }} ACNY</span></div>
        <div class="stat-sub">Spent Today: {{ wallet.spentTodayAcny }} ACNY</div>
      </div>

      <div class="card stat-card">
        <div class="stat-top">
          <span class="stat-label">Per-Transaction Limit</span>
          <ShieldAlert class="stat-icon text-indigo" :size="16" />
        </div>
        <div class="stat-val">{{ wallet.maxPerTxAcny }} <span class="stat-unit">ACNY</span></div>
        <div class="stat-sub">Hard Deterministic Cap</div>
      </div>
    </div>

    <!-- Signing Boundary & Protection Parameters -->
    <div class="protection-grid">
      <div class="card prot-card">
        <div class="prot-icon-wrap">
          <Lock class="text-green" :size="18" />
        </div>
        <div class="prot-content">
          <span class="prot-title">Signing Protection Status</span>
          <span class="badge badge-success">ISOLATED SIGNING BOUNDARY</span>
          <span class="prot-desc">Financial private key is completely air-gapped from AI prompt contexts and LLM inference.</span>
        </div>
      </div>

      <div class="card prot-card">
        <div class="prot-icon-wrap">
          <Repeat class="text-blue" :size="18" />
        </div>
        <div class="prot-content">
          <span class="prot-title">Intent Replay Protection</span>
          <span class="badge badge-success">PERSISTENT GUARD ACTIVE</span>
          <span class="prot-desc">Universal nonce and intent ID tracking with fsynced ledger surviving process restarts.</span>
        </div>
      </div>

      <div class="card prot-card">
        <div class="prot-icon-wrap">
          <FileText class="text-cyan" :size="18" />
        </div>
        <div class="prot-content">
          <span class="prot-title">Active Financial Policy</span>
          <span class="mono prot-policy">{{ wallet.policyVersion }}</span>
          <span class="prot-desc">Cryptographically anchored composite policy controlling all spending invariants.</span>
        </div>
      </div>
    </div>

    <!-- Approved Whitelist Recipients Directory -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">
          <Users :size="14" />
          Approved Recipient Directory
        </span>
        <span class="badge badge-info">WHITELIST ENFORCED</span>
      </div>

      <table class="inst-table">
        <thead>
          <tr>
            <th>Entity Name</th>
            <th>Designation</th>
            <th>On-Chain Address</th>
            <th>Daily Limit</th>
            <th>Authorization Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="rec in recipients" :key="rec.address">
            <td><strong>{{ rec.name }}</strong></td>
            <td><span class="text-dim">{{ rec.category }}</span></td>
            <td>
              <div class="addr-wrap">
                <span class="mono addr-text">{{ rec.address.slice(0, 10) }}...{{ rec.address.slice(-8) }}</span>
                <button class="btn-copy" @click="copyAddress(rec.address)" title="Copy Address">
                  <Check v-if="copiedAddr === rec.address" class="text-green" :size="12" />
                  <Copy v-else :size="12" />
                </button>
              </div>
            </td>
            <td>{{ rec.limit }}</td>
            <td>
              <span class="badge badge-success">
                <Check :size="10" />
                APPROVED
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Recent Transactions Table -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">
          <History :size="14" />
          Recent Wallet Transfers
        </span>
        <span class="badge badge-neutral">{{ walletTransfers.length }} RECORDS</span>
      </div>

      <table class="inst-table">
        <thead>
          <tr>
            <th>TxHash</th>
            <th>Recipient</th>
            <th>Amount</th>
            <th>Audit Block</th>
            <th>Financial Block</th>
            <th>Status</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tx in walletTransfers" :key="tx.id">
            <td class="mono">
              <span class="text-blue">{{ tx.txHash ? tx.txHash.slice(0, 14) + '...' : 'N/A' }}</span>
            </td>
            <td>{{ tx.recipientName }}</td>
            <td><strong>{{ tx.amount }} ACNY</strong></td>
            <td class="mono">{{ tx.auditBlock || '-' }}</td>
            <td class="mono">{{ tx.blockNumber || '-' }}</td>
            <td>
              <span class="badge badge-success">
                <Check :size="10" />
                CONFIRMED
              </span>
            </td>
            <td class="text-dim">{{ tx.timestamp }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Expandable Technical Details for Auditors -->
    <div class="card technical-details-card">
      <button class="btn-toggle-tech" @click="showTechnical = !showTechnical">
        <ChevronDown v-if="showTechnical" :size="14" />
        <ChevronRight v-else :size="14" />
        <span>Technical Signer Configuration (Auditor Inspection)</span>
      </button>

      <div v-if="showTechnical" class="tech-body">
        <div class="tech-row">
          <span class="tech-lbl">Signer Address:</span>
          <span class="mono tech-val">{{ wallet.signerAddress }}</span>
        </div>
        <div class="tech-row">
          <span class="tech-lbl">Private Key Isolation:</span>
          <span class="tech-val">0 Exposure to AI Agents; isolated execution boundary</span>
        </div>
        <div class="tech-row">
          <span class="tech-lbl">Consortium Network:</span>
          <span class="tech-val">FISCO BCOS group0 (PBFT consensus)</span>
        </div>
        <div class="tech-row">
          <span class="tech-lbl">Expiry Validation:</span>
          <span class="tech-val">Strict 300-second Proof-of-Intent lifespan verified before signing</span>
        </div>
      </div>
    </div>

    <!-- Initiate Transfer Modal -->
    <div v-if="showTransferModal" class="modal-backdrop" @click.self="showTransferModal = false">
      <div class="modal-dialog">
        <div class="modal-header">
          <div class="modal-title">Initiate Autonomous Treasury Transfer</div>
          <button class="btn-ghost" @click="showTransferModal = false">
            <X :size="16" />
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Select Whitelisted Recipient</label>
            <select v-model="transferForm.recipient" class="form-select">
              <option v-for="rec in recipients" :key="rec.address" :value="rec.address">
                {{ rec.name }} ({{ rec.category }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Amount (ACNY)</label>
            <input 
              v-model="transferForm.amount" 
              type="number" 
              step="0.01" 
              max="10" 
              class="form-input" 
              placeholder="e.g. 2.00 (Max 10.00 ACNY)" 
            />
            <span class="form-hint">Maximum per-transaction policy limit: 10.00 ACNY</span>
          </div>

          <div class="form-group">
            <label class="form-label">Business Purpose</label>
            <input 
              v-model="transferForm.memo" 
              type="text" 
              class="form-input" 
              placeholder="e.g. Supplier procurement batch payment" 
            />
          </div>

          <div v-if="transferFeedback" class="modal-feedback" :class="feedbackIsError ? 'feedback-err' : 'feedback-ok'">
            {{ transferFeedback }}
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-secondary" @click="showTransferModal = false">Cancel</button>
          <button class="btn-primary" :disabled="transferLoading" @click="handleModalSubmit">
            <RefreshCw v-if="transferLoading" class="spin-icon" :size="13" />
            {{ transferLoading ? 'Processing Execution...' : 'Authorize & Execute' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Send, 
  Wallet, 
  Clock, 
  ShieldAlert, 
  Lock, 
  Repeat, 
  FileText, 
  Users, 
  History, 
  Check, 
  Copy, 
  ChevronRight, 
  ChevronDown, 
  X, 
  RefreshCw 
} from 'lucide-vue-next'
import { fetchWallet, executeAgent, activityList, globalWallet } from '../services/api.js'

const wallet = globalWallet

const showTransferModal = ref(false)
const showTechnical = ref(false)
const copiedAddr = ref('')
const transferLoading = ref(false)
const transferFeedback = ref('')
const feedbackIsError = ref(false)

const transferForm = ref({
  recipient: '0x1000000000000000000000000000000000000002',
  amount: '2.00',
  memo: 'Procurement raw materials settlement'
})

const recipients = ref([
  {
    name: 'Supplier A',
    category: 'Logistics & Fleet Operations',
    address: '0x1000000000000000000000000000000000000001',
    limit: '25.00 ACNY/day'
  },
  {
    name: 'Supplier B',
    category: 'Raw Materials Procurement',
    address: '0x1000000000000000000000000000000000000002',
    limit: '50.00 ACNY/day'
  },
  {
    name: 'Treasury Reserve Vault',
    category: 'Internal Surplus Staking Vault',
    address: '0x31ed5233b81c79d5adddeef991f531a9bbc2ad01',
    limit: '100.00 ACNY/day'
  }
])

const walletTransfers = computed(() => {
  return activityList.value.filter(tx => tx.type === 'TRANSFER')
})

onMounted(async () => {
  await fetchWallet()
})

function copyAddress(addr) {
  navigator.clipboard.writeText(addr)
  copiedAddr.value = addr
  setTimeout(() => { copiedAddr.value = '' }, 1500)
}

async function handleModalSubmit() {
  transferLoading.value = true
  transferFeedback.value = ''
  feedbackIsError.value = false

  const recObj = recipients.value.find(r => r.address === transferForm.value.recipient)
  const recName = recObj ? recObj.name : 'Supplier B'
  const prompt = `Transfer ${transferForm.value.amount} ACNY to ${recName} for ${transferForm.value.memo}.`

  try {
    const res = await executeAgent(prompt, {
      recipient: transferForm.value.recipient,
      recipientName: recName,
      amount: transferForm.value.amount,
      memo: transferForm.value.memo
    })
    if (res.decision === 'BLOCKED' || res.decision === 'REJECTED') {
      feedbackIsError.value = true
      transferFeedback.value = `Transaction Rejected: ${res.reasoning_summary}`
    } else {
      feedbackIsError.value = false
      transferFeedback.value = `✓ Transaction Executed! Block ${res.block_number} • TxHash: ${res.transaction_hash ? res.transaction_hash.slice(0, 18) + '...' : ''}`
      setTimeout(() => {
        showTransferModal.value = false
        transferFeedback.value = ''
      }, 1500)
    }
  } catch (err) {
    feedbackIsError.value = true
    transferFeedback.value = `Execution error: ${err.message}`
  } finally {
    transferLoading.value = false
  }
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
  align-items: flex-start;
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px 18px;
}

.stat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-val {
  font-size: 24px;
  font-weight: 800;
  color: #09090b;
  letter-spacing: -0.5px;
}

.stat-unit {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.stat-sub {
  font-size: 11px;
  color: #64748b;
}

.protection-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.prot-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px 18px;
  background-color: #ffffff;
  border: 1px solid var(--border);
}

.prot-icon-wrap {
  width: 36px;
  height: 36px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.prot-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.prot-title {
  font-size: 11px;
  font-weight: 700;
  color: #09090b;
  text-transform: uppercase;
}

.prot-policy {
  font-size: 11px;
  color: #1e3a8a;
  font-weight: 600;
}

.prot-desc {
  font-size: 11.5px;
  color: #475569;
  line-height: 1.4;
}

.text-blue { color: #1e3a8a; }
.text-amber { color: #b45309; }
.text-indigo { color: #3730a3; }
.text-green { color: #166534; }
.text-cyan { color: #0e7490; }
.text-dim { color: #64748b; }

.addr-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
}

.addr-text {
  font-size: 11px;
  color: #09090b;
}

.btn-copy {
  background: transparent;
  color: #64748b;
  padding: 3px;
  border-radius: 3px;
}

.btn-copy:hover {
  color: #09090b;
  background: #f4f4f5;
}

.technical-details-card {
  padding: 14px 18px;
  background-color: #ffffff;
  border: 1px solid var(--border);
}

.btn-toggle-tech {
  display: flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  color: #334155;
  font-size: 12px;
  font-weight: 600;
  width: 100%;
  text-align: left;
}

.btn-toggle-tech:hover {
  color: #09090b;
}

.tech-body {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tech-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.tech-lbl {
  color: #64748b;
  font-weight: 500;
}

.tech-val {
  color: #09090b;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  color: #334155;
}

.form-select, .form-input {
  width: 100%;
  background-color: #ffffff;
  border: 1px solid var(--border);
  color: #09090b;
}

.form-hint {
  font-size: 10.5px;
  color: #64748b;
}

.modal-feedback {
  padding: 10px 12px;
  border-radius: 4px;
  font-size: 11.5px;
  line-height: 1.4;
}

.feedback-ok {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #166534;
}

.feedback-err {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
}

.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
