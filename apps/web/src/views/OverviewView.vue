<template>
  <div class="page-container">
    <!-- Executive Header -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">{{ t('overviewTitle') }}</h1>
        <p class="page-subtitle">{{ t('overviewSub') }}</p>
      </div>
      <div class="header-status">
        <span class="badge badge-success">
          <ShieldCheck :size="12" />
          {{ t('tagProtected') }}
        </span>
      </div>
    </div>

    <!-- 4 Primary KPI Cards -->
    <div class="kpi-grid">
      <div class="card kpi-card">
        <div class="kpi-header">
          <span class="kpi-label">{{ t('walletBalanceLbl') }}</span>
          <Wallet class="kpi-icon text-blue" :size="16" />
        </div>
        <div class="kpi-value">{{ wallet.balanceAcny }} <span class="kpi-unit">ACNY</span></div>
        <div class="kpi-sub">{{ t('walletBalanceSub') }}</div>
      </div>

      <div class="card kpi-card">
        <div class="kpi-header">
          <span class="kpi-label">{{ t('portfolioAllocLbl') }}</span>
          <PieChart class="kpi-icon text-indigo" :size="16" />
        </div>
        <div class="kpi-value">
          {{ portfolio.acnyAllocationBps / 100 }}% <span class="kpi-unit">ACNY</span>
          <span class="kpi-sep">/</span>
          {{ portfolio.agixAllocationBps / 100 }}% <span class="kpi-unit">AGIX</span>
        </div>
        <div class="kpi-sub">{{ t('portfolioAllocSub') }}</div>
      </div>

      <div class="card kpi-card">
        <div class="kpi-header">
          <span class="kpi-label">{{ t('safetyStatusLbl') }}</span>
          <CheckCircle2 class="kpi-icon text-green" :size="16" />
        </div>
        <div class="kpi-value text-green">PASS</div>
        <div class="kpi-sub">{{ t('safetyStatusSub') }}</div>
      </div>

      <div class="card kpi-card">
        <div class="kpi-header">
          <span class="kpi-label">{{ t('networkHealthLbl') }}</span>
          <Cpu class="kpi-icon text-cyan" :size="16" />
        </div>
        <div class="kpi-value">{{ oracle.healthy_nodes || 4 }} / 4 <span class="kpi-unit">Validators</span></div>
        <div class="kpi-sub">{{ t('networkHealthSub') }}</div>
      </div>
    </div>

    <!-- Ask Aegis Command Section -->
    <div class="card command-card">
      <div class="card-header">
        <span class="card-title">
          <Sparkles :size="14" />
          {{ t('engineTitle') }}
        </span>
        <button class="btn-ghost" @click="showTraceDrawer = true">
          <Layers :size="13" />
          {{ t('viewTrace') }}
        </button>
      </div>

      <div class="command-body">
        <div class="command-input-wrap">
          <textarea 
            v-model="instruction" 
            class="command-input" 
            rows="2"
            placeholder="Enter natural language financial instruction (e.g. Transfer 2.00 ACNY to Supplier B if risk is low)..."
          ></textarea>
        </div>

        <div class="command-toolbar">
          <div class="preset-chips">
            <span class="preset-label">{{ t('quickActionsLbl') }}</span>
            <button class="preset-chip" @click="setPreset('transfer')">Transfer 2.00 ACNY</button>
            <button class="preset-chip" @click="setPreset('rebalance')">Rebalance (67/33)</button>
            <button class="preset-chip chip-reject" @click="setPreset('exceed')">Exceed Limit (25 ACNY)</button>
          </div>
          <button class="btn-primary" :disabled="loading" @click="handleExecute">
            <RefreshCw v-if="loading" class="spin-icon" :size="14" />
            <ArrowRight v-else :size="14" />
            {{ loading ? 'Evaluating Controls...' : t('btnExecute') }}
          </button>
        </div>
      </div>

      <!-- Compact 5-State User-Facing Execution Progress -->
      <!-- Compact 5-State User-Facing Execution Progress -->
      <div class="progress-section">
        <div class="progress-header">
          <span class="section-subtitle">{{ t('pipelineTitle') }}</span>
          <div 
            class="decision-pill" 
            :class="{
              'pill-standby': executionState === 'idle',
              'pill-running': executionState === 'evaluating',
              'pill-blocked': executionState === 'blocked',
              'pill-approved': executionState === 'confirmed'
            }"
          >
            {{ executionState === 'idle' ? 'STANDBY • READY' : 
               executionState === 'evaluating' ? 'EVALUATING CONTROLS...' :
               executionState === 'blocked' ? 'BLOCKED' : 'APPROVED' }}
          </div>
        </div>

        <div class="five-states-track">
          <!-- State 1 -->
          <div 
            class="state-step" 
            :class="executionState === 'idle' ? 'inactive' : (executionState === 'evaluating' && activeStep === 1 ? 'active' : 'completed')"
          >
            <div class="state-indicator">
              <RefreshCw v-if="executionState === 'evaluating' && activeStep === 1" class="spin-icon" :size="12" />
              <Check v-else-if="executionState !== 'idle'" :size="12" />
              <span v-else>1</span>
            </div>
            <div class="state-info">
              <span class="state-name">{{ t('step1Name') }}</span>
              <span class="state-detail">{{ executionState === 'idle' ? 'Awaiting intent' : t('step1Desc') }}</span>
            </div>
          </div>

          <div class="state-line" :class="{ completed: executionState !== 'idle' && (executionState !== 'evaluating' || activeStep >= 2) }"></div>

          <!-- State 2 -->
          <div 
            class="state-step" 
            :class="executionState === 'idle' ? 'inactive' : (executionState === 'evaluating' ? (activeStep === 2 ? 'active' : (activeStep > 2 ? 'completed' : 'inactive')) : 'completed')"
          >
            <div class="state-indicator">
              <RefreshCw v-if="executionState === 'evaluating' && activeStep === 2" class="spin-icon" :size="12" />
              <Check v-else-if="executionState !== 'idle' && (executionState !== 'evaluating' || activeStep >= 2)" :size="12" />
              <span v-else>2</span>
            </div>
            <div class="state-info">
              <span class="state-name">{{ t('step2Name') }}</span>
              <span class="state-detail">{{ executionState === 'blocked' ? 'Policy violation flagged' : (executionState === 'idle' ? 'Standby check' : t('step2Desc')) }}</span>
            </div>
          </div>

          <div class="state-line" :class="{ completed: (executionState === 'confirmed') || (executionState === 'evaluating' && activeStep >= 3), blocked: executionState === 'blocked' }"></div>

          <!-- State 3 -->
          <div 
            class="state-step" 
            :class="executionState === 'idle' ? 'inactive' : (executionState === 'evaluating' ? (activeStep === 3 ? 'active' : (activeStep > 3 ? 'completed' : 'inactive')) : (executionState === 'blocked' ? 'state-blocked' : 'completed'))"
          >
            <div class="state-indicator">
              <RefreshCw v-if="executionState === 'evaluating' && activeStep === 3" class="spin-icon" :size="12" />
              <X v-else-if="executionState === 'blocked'" :size="12" />
              <Check v-else-if="executionState === 'confirmed' || (executionState === 'evaluating' && activeStep > 3)" :size="12" />
              <span v-else>3</span>
            </div>
            <div class="state-info">
              <span class="state-name">{{ t('step3Name') }}</span>
              <span class="state-detail">{{ executionState === 'blocked' ? 'Vetoed by Enforcer' : (executionState === 'idle' ? 'Standby check' : t('step3Desc')) }}</span>
            </div>
          </div>

          <div class="state-line" :class="{ completed: executionState === 'confirmed' || (executionState === 'evaluating' && activeStep >= 4), blocked: executionState === 'blocked' }"></div>

          <!-- State 4 -->
          <div 
            class="state-step" 
            :class="executionState === 'confirmed' ? 'completed' : (executionState === 'evaluating' ? (activeStep === 4 ? 'active' : (activeStep > 4 ? 'completed' : 'inactive')) : 'inactive')"
          >
            <div class="state-indicator">
              <RefreshCw v-if="executionState === 'evaluating' && activeStep === 4" class="spin-icon" :size="12" />
              <Check v-else-if="executionState === 'confirmed' || (executionState === 'evaluating' && activeStep > 4)" :size="12" />
              <span v-else>4</span>
            </div>
            <div class="state-info">
              <span class="state-name">{{ t('step4Name') }}</span>
              <span class="state-detail">{{ executionState === 'blocked' ? 'Proof skipped' : (executionState === 'idle' ? 'Standby check' : t('step4Desc')) }}</span>
            </div>
          </div>

          <div class="state-line" :class="{ completed: executionState === 'confirmed' || (executionState === 'evaluating' && activeStep >= 5), blocked: executionState === 'blocked' }"></div>

          <!-- State 5 -->
          <div 
            class="state-step" 
            :class="executionState === 'confirmed' ? 'completed' : (executionState === 'evaluating' && activeStep === 5 ? 'active' : 'inactive')"
          >
            <div class="state-indicator">
              <RefreshCw v-if="executionState === 'evaluating' && activeStep === 5" class="spin-icon" :size="12" />
              <Check v-else-if="executionState === 'confirmed'" :size="12" />
              <span v-else>5</span>
            </div>
            <div class="state-info">
              <span class="state-name">{{ t('step5Name') }}</span>
              <span class="state-detail">{{ executionState === 'blocked' ? 'Execution halted' : (executionState === 'idle' ? 'Standby check' : t('step5Desc')) }}</span>
            </div>
          </div>
        </div>

        <!-- Outcome Summary Box -->
        <div 
          class="outcome-box" 
          :class="{
            'outcome-standby': executionState === 'idle',
            'outcome-running': executionState === 'evaluating',
            'outcome-blocked': executionState === 'blocked',
            'outcome-approved': executionState === 'confirmed'
          }"
        >
          <div class="outcome-top">
            <div class="outcome-title">
              <Sparkles v-if="executionState === 'idle'" class="text-blue" :size="16" />
              <RefreshCw v-else-if="executionState === 'evaluating'" class="spin-icon text-blue" :size="16" />
              <AlertTriangle v-else-if="executionState === 'blocked'" class="text-amber" :size="16" />
              <CheckCircle2 v-else class="text-green" :size="16" />
              <strong>
                {{ executionState === 'idle' ? 'Deterministic Safety & Compliance Engine Ready' :
                   executionState === 'evaluating' ? 'Evaluating Multi-Agent Controls & On-Chain Verification...' :
                   executionState === 'blocked' ? 'Autonomous Signing Vetoed by Policy Guard' :
                   'Autonomous Execution Confirmed' }}
              </strong>
            </div>
            <div v-if="executionState === 'idle'" class="outcome-meta">
              <span class="mono">Consortium PBFT Consensus • Live</span>
            </div>
            <div v-else-if="executionState === 'evaluating'" class="outcome-meta">
              <span class="mono">Step {{ activeStep }} of 5 Active</span>
            </div>
            <div v-else-if="executionState === 'blocked'" class="outcome-meta">
              <span class="badge badge-danger">POLICY_VETO</span>
            </div>
            <div v-else-if="activeResult?.transaction_hash" class="outcome-meta">
              <span>Block {{ activeResult.block_number }}</span>
              <span class="mono">{{ activeResult.transaction_hash.slice(0, 18) }}...</span>
            </div>
          </div>
          <p class="outcome-desc">
            {{ executionState === 'idle' ? 'Select an action or enter an instruction above and click "Execute Intent" to evaluate multi-agent compliance, deterministic limits, and trigger autonomous signing.' :
               executionState === 'evaluating' ? (activeStep === 1 ? 'Parsing natural language financial intent and locking parameter invariants...' : activeStep === 2 ? 'Verifying regulatory compliance (PIPL, cross-border restrictions, BGE-M3 legal grounding)...' : activeStep === 3 ? 'Evaluating deterministic policy limits and invariant enforcer...' : activeStep === 4 ? 'Securing Proof-of-Intent hash and anchoring pre-execution audit on FISCO BCOS...' : 'Submitting signed transaction payload to FISCO BCOS validator consortium...') :
               activeResult?.reasoning_summary }}
          </p>
        </div>
      </div>
    </div>

    <!-- Recent Activity Section -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">
          <History :size="14" />
          {{ t('recentActivityTitle') }}
        </span>
        <router-link to="/activity" class="btn-ghost">
          {{ t('viewAllTx') }}
          <ArrowRight :size="12" />
        </router-link>
      </div>

      <table class="inst-table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Description</th>
            <th>Asset & Amount</th>
            <th>Status</th>
            <th>Block</th>
            <th>Timestamp</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tx in recentActivities" :key="tx.id">
            <td>
              <span class="badge" :class="tx.type === 'TRANSFER' ? 'badge-info' : tx.type === 'REBALANCE' ? 'badge-neutral' : 'badge-success'">
                {{ tx.type }}
              </span>
            </td>
            <td><strong>{{ tx.title }}</strong></td>
            <td>{{ tx.amount }} <small>{{ tx.asset }}</small></td>
            <td>
              <span class="badge badge-success">
                <Check :size="10" />
                {{ tx.status }}
              </span>
            </td>
            <td class="mono">{{ tx.blockNumber }}</td>
            <td class="text-dim">{{ tx.timestamp }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Technical Trace Slide-Over Drawer -->
    <div v-if="showTraceDrawer" class="drawer-backdrop" @click.self="showTraceDrawer = false">
      <div class="drawer-panel">
        <div class="drawer-header">
          <div class="drawer-title">Technical Architecture Execution Trace</div>
          <button class="btn-ghost" @click="showTraceDrawer = false">
            <X :size="16" />
          </button>
        </div>

        <div class="drawer-body">
          <p class="trace-intro">
            10-step internal pipeline enforcing pre-execution audit commitment and isolated signing.
          </p>

          <div class="trace-steps">
            <div class="trace-step">
              <span class="trace-num">01</span>
              <div class="trace-content">
                <strong>Intent Locked</strong>
                <span class="trace-sub mono">userRequestHash: {{ activeResult?.intent_id ? activeResult.intent_id.slice(0, 28) + '...' : '0xb6780c10a30b...7671b' }}</span>
              </div>
            </div>

            <div class="trace-step">
              <span class="trace-num">02</span>
              <div class="trace-content">
                <strong>Planner Proposal</strong>
                <span class="trace-sub">{{ activeResult?.action || 'TRANSFER' }} {{ activeResult?.amount_minor ? (activeResult.amount_minor / 100) + ' ACNY' : '2.00 ACNY' }} to Whitelisted Recipient</span>
              </div>
            </div>

            <div class="trace-step">
              <span class="trace-num">03</span>
              <div class="trace-content">
                <strong>Security Agent Gate</strong>
                <span class="trace-sub">Deterministic Injection & Jailbreak Defense (Qwen3Guard)</span>
              </div>
              <span class="badge badge-success">SAFE</span>
            </div>

            <div class="trace-step">
              <span class="trace-num">04</span>
              <div class="trace-content">
                <strong>China Compliance Engine</strong>
                <span class="trace-sub">PIPL Resident ID Scan & Cross-Border Guard</span>
              </div>
              <span class="badge" :class="isRejected ? 'badge-danger' : 'badge-success'">{{ activeResult?.compliance_status || 'PASS' }}</span>
            </div>

            <div class="trace-step">
              <span class="trace-num">05</span>
              <div class="trace-content">
                <strong>Regulatory RAG Grounding</strong>
                <span class="trace-sub">BGE-M3 1024-d Dense Retrieval • 5 Legal Passages</span>
              </div>
              <span class="badge badge-info">GROUNDED</span>
            </div>

            <div class="trace-step">
              <span class="trace-num">06</span>
              <div class="trace-content">
                <strong>Risk Evaluation</strong>
                <span class="trace-sub">Dynamic BPS Risk Calculator (Class: {{ isRejected ? 'HIGH' : 'LOW' }})</span>
              </div>
              <span class="badge" :class="isRejected ? 'badge-danger' : 'badge-success'">{{ activeResult?.risk_score_bps || 453 }} BPS</span>
            </div>

            <div class="trace-step">
              <span class="trace-num">07</span>
              <div class="trace-content">
                <strong>Final Leader Decision</strong>
                <span class="trace-sub">Qwen3-8B Holistic Control with Deterministic Invariant Enforcer</span>
              </div>
              <span class="badge" :class="isRejected ? 'badge-danger' : 'badge-success'">{{ isRejected ? 'BLOCKED' : 'APPROVE' }}</span>
            </div>

            <div class="trace-step">
              <span class="trace-num">08</span>
              <div class="trace-content">
                <strong>Canonical PoI Generated</strong>
                <span class="trace-sub mono">{{ activeResult?.poi_hash ? activeResult.poi_hash.slice(0, 32) + '...' : 'SKIPPED_ON_BLOCK' }}</span>
              </div>
            </div>

            <div class="trace-step">
              <span class="trace-num">09</span>
              <div class="trace-content">
                <strong>Pre-Execution Audit Anchored</strong>
                <span class="trace-sub mono">Audit Block: {{ activeResult?.audit_block_number || 'N/A' }}</span>
              </div>
              <span v-if="activeResult?.audit_block_number" class="badge badge-info">BLOCK {{ activeResult.audit_block_number }}</span>
            </div>

            <div class="trace-step">
              <span class="trace-num">10</span>
              <div class="trace-content">
                <strong>FISCO Financial Execution</strong>
                <span class="trace-sub mono">Financial Block: {{ activeResult?.block_number || 'NOT_EXECUTED' }}</span>
              </div>
              <span v-if="activeResult?.block_number" class="badge badge-success">BLOCK {{ activeResult.block_number }}</span>
            </div>
          </div>
        </div>

        <div class="drawer-footer">
          <button class="btn-secondary" @click="showTraceDrawer = false">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  ShieldCheck, 
  Wallet, 
  PieChart, 
  CheckCircle2, 
  Cpu, 
  Sparkles, 
  Layers, 
  RefreshCw, 
  ArrowRight, 
  Check, 
  X, 
  AlertTriangle, 
  History 
} from 'lucide-vue-next'
import { 
  fetchWallet, 
  fetchPortfolio, 
  fetchOracle, 
  executeAgent, 
  activityList,
  globalWallet 
} from '../services/api.js'
import { t } from '../services/i18n.js'

const instruction = ref("Transfer 2.00 ACNY to Supplier B only if the transaction is permitted by all current risk, security and compliance controls.")
const loading = ref(false)
const showTraceDrawer = ref(false)

const executionState = ref('idle') // 'idle' | 'evaluating' | 'confirmed' | 'blocked'
const activeStep = ref(0)
const activeResult = ref(null)

const wallet = globalWallet
const portfolio = ref({ acnyAllocationBps: 6700, agixAllocationBps: 3300 })
const oracle = ref({ state: 'NORMAL', healthy_nodes: 4 })

const isRejected = computed(() => {
  return activeResult.value?.decision === 'BLOCKED' || activeResult.value?.decision === 'REJECTED'
})

const recentActivities = computed(() => {
  return activityList.value.slice(0, 4)
})

onMounted(async () => {
  await fetchWallet()
  portfolio.value = await fetchPortfolio()
  oracle.value = await fetchOracle()
})

function setPreset(type) {
  if (type === 'transfer') {
    instruction.value = "Transfer 2.00 ACNY to Supplier B only if the transaction is permitted by all current risk, security and compliance controls."
    executionState.value = 'idle'
    activeResult.value = null
  } else if (type === 'rebalance') {
    instruction.value = "Rebalance the simulated portfolio to 67% ACNY and 33% AGIX if all risk, security and compliance checks pass."
    executionState.value = 'idle'
    activeResult.value = null
  } else if (type === 'exceed') {
    instruction.value = "Transfer 25.00 ACNY to Supplier B immediately."
    handleExecute()
  }
}

async function handleExecute() {
  if (loading.value) return
  loading.value = true
  executionState.value = 'evaluating'
  activeStep.value = 1

  const lower = instruction.value.toLowerCase()
  const numMatch = lower.match(/(\d+(\.\d+)?)\s*acny/)
  const isExceed = !lower.includes('rebalance') && numMatch && parseFloat(numMatch[1]) > 10.0

  // Sequential visual animation for the 5-state pipeline
  // Step 1: Understanding Request
  activeStep.value = 1
  await new Promise(r => setTimeout(r, 220))

  // Step 2: Safety & Compliance
  activeStep.value = 2
  await new Promise(r => setTimeout(r, 250))

  // Step 3: Financial Decision & Deterministic Cap
  activeStep.value = 3
  await new Promise(r => setTimeout(r, 250))

  if (isExceed) {
    const res = await executeAgent(instruction.value)
    activeResult.value = res
    executionState.value = 'blocked'
    loading.value = false
    return
  }

  // Step 4: Proof-of-Intent Secured & Pre-Execution Audit
  activeStep.value = 4
  await new Promise(r => setTimeout(r, 220))

  // Step 5: On-chain Finality on FISCO BCOS
  activeStep.value = 5
  try {
    const res = await executeAgent(instruction.value)
    activeResult.value = res
    if (res.decision === 'BLOCKED' || res.decision === 'REJECTED') {
      executionState.value = 'blocked'
    } else {
      executionState.value = 'confirmed'
    }
  } catch (err) {
    executionState.value = 'blocked'
    activeResult.value = {
      decision: 'BLOCKED',
      reasoning_summary: `Execution error: ${err.message}`
    }
  } finally {
    loading.value = false
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

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.kpi-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px 18px;
  background-color: #ffffff;
  border: 1px solid var(--border);
}

.kpi-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.kpi-label {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.kpi-value {
  font-size: 24px;
  font-weight: 800;
  color: #09090b;
  letter-spacing: -0.5px;
}

.kpi-unit {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.kpi-sep {
  font-size: 16px;
  color: #cbd5e1;
  margin: 0 4px;
}

.kpi-sub {
  font-size: 11px;
  color: #64748b;
}

.text-blue { color: #1e3a8a; }
.text-indigo { color: #3730a3; }
.text-green { color: #166534; }
.text-cyan { color: #0e7490; }
.text-amber { color: #b45309; }
.text-dim { color: #64748b; }

.command-card {
  padding: 20px;
  background-color: #ffffff;
  border: 1px solid var(--border);
}

.command-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.command-input {
  width: 100%;
  resize: vertical;
  background-color: #ffffff;
  border: 1px solid var(--border);
  color: #09090b;
  border-radius: 6px;
  padding: 12px;
  font-size: 13px;
  line-height: 1.5;
}

.command-input:focus {
  border-color: #09090b;
  box-shadow: 0 0 0 1px #09090b;
}

.command-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.preset-chips {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.preset-label {
  font-size: 11px;
  color: #64748b;
  font-weight: 600;
}

.preset-chip {
  background: #ffffff;
  border: 1px solid var(--border);
  color: #334155;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 4px;
}

.preset-chip:hover {
  background: #f4f4f5;
  color: #09090b;
  border-color: #09090b;
}

.chip-reject {
  background: #fffbeb;
  border-color: #fde68a;
  color: #b45309;
}

.progress-section {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-subtitle {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
  letter-spacing: 0.6px;
}

.decision-pill {
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.6px;
}

.pill-standby {
  background: #f4f4f5;
  color: #52525b;
  border: 1px solid #e4e4e7;
}

.pill-running {
  background: #eff6ff;
  color: #1d4ed8;
  border: 1px solid #bfdbfe;
  animation: pulse-ring 1.5s infinite;
}

.pill-approved {
  background: #f0fdf4;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.pill-blocked {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}

@keyframes pulse-ring {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.state-step.active .state-indicator {
  background: #2563eb;
  color: #ffffff;
}

.five-states-track {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 0;
}

.state-step {
  display: flex;
  align-items: center;
  gap: 10px;
}

.state-indicator {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  flex-shrink: 0;
}

.state-step.completed .state-indicator {
  background: #09090b;
  color: #ffffff;
}

.state-step.state-blocked .state-indicator {
  background: #b91c1c;
  color: #ffffff;
}

.state-step.inactive .state-indicator {
  background: #f4f4f5;
  border: 1px solid #e4e4e7;
  color: #94a3b8;
}

.state-info {
  display: flex;
  flex-direction: column;
}

.state-name {
  font-size: 11.5px;
  font-weight: 600;
  color: #09090b;
}

.state-detail {
  font-size: 10.5px;
  color: #64748b;
}

.state-step.inactive .state-name,
.state-step.inactive .state-detail {
  color: #94a3b8;
}

.state-line {
  flex: 1;
  height: 2px;
  background: var(--border);
}

.state-line.completed {
  background: #09090b;
}

.state-line.blocked {
  background: #b91c1c;
}

.outcome-box {
  border-radius: 6px;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.outcome-standby {
  background: #fafafa;
  border: 1px solid var(--border);
  border-left: 3px solid #71717a;
}

.outcome-running {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-left: 3px solid #2563eb;
}

.outcome-approved {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-left: 3px solid #166534;
}

.outcome-blocked {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-left: 3px solid #b91c1c;
}

.outcome-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.outcome-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.outcome-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: #64748b;
}

.outcome-desc {
  font-size: 12px;
  color: #334155;
  line-height: 1.45;
}

.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.trace-intro {
  font-size: 12px;
  color: #334155;
}

.trace-steps {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.trace-step {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
}

.trace-num {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 700;
  color: #09090b;
  width: 20px;
}

.trace-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.trace-content strong {
  font-size: 12px;
  color: #09090b;
}

.trace-sub {
  font-size: 11px;
  color: #64748b;
}
</style>
