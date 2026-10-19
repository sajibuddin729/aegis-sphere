<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Autonomous Portfolio Allocation</h1>
        <p class="page-subtitle">Algorithmic dynamic rebalancing between simulated cash reserves and growth exposure on FISCO BCOS.</p>
      </div>
      <div class="header-status">
        <span class="badge badge-success">
          <CheckCircle2 :size="12" />
          ALLOCATION STATE: OPTIMAL
        </span>
      </div>
    </div>

    <!-- Allocation Cards Grid -->
    <div class="alloc-grid">
      <!-- ACNY Card -->
      <div class="card alloc-card acny-card">
        <div class="card-header">
          <div class="card-title">
            <Coins :size="14" />
            AegisCNY (ACNY)
          </div>
          <span class="badge badge-success">CASH RESERVE</span>
        </div>
        <div class="alloc-main">
          <div class="alloc-percentage text-blue">{{ (portfolio.acnyAllocationBps / 100).toFixed(1) }}%</div>
          <div class="alloc-metrics">
            <div class="metric-row">
              <span class="metric-lbl">Reserve Balance:</span>
              <strong class="metric-val">{{ portfolio.balanceAcny }} ACNY</strong>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">Target Allocation:</span>
              <strong class="metric-val">67.0% (6,700 BPS)</strong>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">Minimum Reserve Floor:</span>
              <strong class="metric-val">65.0% (6,500 BPS)</strong>
            </div>
          </div>
        </div>
      </div>

      <!-- AGIX Card -->
      <div class="card alloc-card agix-card">
        <div class="card-header">
          <div class="card-title">
            <TrendingUp :size="14" />
            AegisGrowthIndex (AGIX)
          </div>
          <span class="badge badge-info">SYNTHETIC GROWTH ASSET</span>
        </div>
        <div class="alloc-main">
          <div class="alloc-percentage text-indigo">{{ (portfolio.agixAllocationBps / 100).toFixed(1) }}%</div>
          <div class="alloc-metrics">
            <div class="metric-row">
              <span class="metric-lbl">Holding Balance:</span>
              <strong class="metric-val">{{ portfolio.balanceAgix }} AGIX</strong>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">ACNY Valuation Equivalent:</span>
              <strong class="metric-val">3,300.00 ACNY (10:1)</strong>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">Maximum Exposure Cap:</span>
              <strong class="metric-val">40.0% (4,000 BPS)</strong>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Rebalance Ledger & Execution Evidence -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">
          <Scale :size="14" />
          Latest Autonomous Rebalance Evidence
        </span>
        <span class="badge badge-success">ON-CHAIN VERIFIED</span>
      </div>

      <div class="evidence-grid">
        <div class="ev-card">
          <span class="ev-lbl">Trade Direction</span>
          <strong class="ev-val text-blue">AGIX → ACNY (Trim Volatility Exposure)</strong>
        </div>

        <div class="ev-card">
          <span class="ev-lbl">Swapped Amount</span>
          <strong class="ev-val">10.00 AGIX (1,000 minor) → 100.00 ACNY</strong>
        </div>

        <div class="ev-card">
          <span class="ev-lbl">Risk Evaluation Score</span>
          <strong class="ev-val text-green">630 BPS (LOW Risk Profile)</strong>
        </div>

        <div class="ev-card">
          <span class="ev-lbl">Execution Mechanism</span>
          <strong class="ev-val">SwapPool AMM Contract (0x70b93b07...)</strong>
        </div>
      </div>

      <!-- Audit vs Financial Proof -->
      <div class="proof-box">
        <div class="proof-node">
          <span class="proof-tag">PRE-EXECUTION AUDIT COMMITMENT</span>
          <div class="proof-row">
            <span class="badge badge-info">BLOCK 101</span>
            <span class="mono text-dim">TxHash: 0x3d263ce32d4e9bfea529f3ac7d4ad4918409e117349cf0860d59b3795fd7bd11</span>
          </div>
        </div>

        <div class="proof-divider">
          <ArrowDown :size="14" />
          <span>Application-Enforced Block Ordering: Audit Block 101 &lt; Financial Block 102</span>
        </div>

        <div class="proof-node">
          <span class="proof-tag">FINANCIAL REBALANCE SWAP</span>
          <div class="proof-row">
            <span class="badge badge-success">BLOCK 102</span>
            <span class="mono text-blue">TxHash: 0x010810c9d281fb0456449e41a3c13ea936b1ca0f9bb8ae28e88de181945ce87c</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Regulatory Simulation Notice Banner -->
    <div class="disclaimer-banner">
      <div class="disc-title">
        <AlertCircle :size="14" />
        REGULATORY DISCLOSURE & INSTRUMENT SPECIFICATION
      </div>
      <p class="disc-text">
        AGIX is a synthetic growth index used exclusively for autonomous portfolio rebalancing simulation on the FISCO BCOS testnet. It does not constitute an unregistered security, commercial investment contract, or speculative token offering.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  CheckCircle2, 
  Coins, 
  TrendingUp, 
  Scale, 
  ArrowDown, 
  AlertCircle 
} from 'lucide-vue-next'
import { fetchPortfolio } from '../services/api.js'

const portfolio = ref({
  acnyAllocationBps: 6700,
  agixAllocationBps: 3300,
  balanceAcny: '6,700.00',
  balanceAgix: '330.00'
})

onMounted(async () => {
  portfolio.value = await fetchPortfolio()
})
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

.alloc-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.alloc-card {
  padding: 20px;
}

.alloc-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.alloc-percentage {
  font-size: 44px;
  font-weight: 800;
  letter-spacing: -1px;
  color: #09090b;
}

.alloc-metrics {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.metric-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.metric-lbl {
  color: #64748b;
}

.metric-val {
  color: #09090b;
  font-weight: 600;
}

.text-blue { color: #1e3a8a; }
.text-indigo { color: #3730a3; }
.text-green { color: #166534; }
.text-dim { color: #64748b; }

.evidence-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.ev-card {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ev-lbl {
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
}

.ev-val {
  font-size: 12.5px;
  color: #09090b;
}

.proof-box {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.proof-node {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.proof-tag {
  font-size: 10.5px;
  font-weight: 700;
  color: #64748b;
}

.proof-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 11.5px;
  color: #09090b;
}

.proof-divider {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #166534;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 0;
}

.disclaimer-banner {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-left: 3px solid #b45309;
  border-radius: 6px;
  padding: 14px 18px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.disc-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  font-weight: 700;
  color: #b45309;
}

.disc-text {
  font-size: 11.5px;
  color: #78350f;
  line-height: 1.45;
}
</style>
