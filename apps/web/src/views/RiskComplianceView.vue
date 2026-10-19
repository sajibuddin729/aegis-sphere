<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Security & Compliance Center</h1>
        <p class="page-subtitle">Deterministic financial risk controls and machine-readable regulatory policy verification.</p>
      </div>
      <div class="header-status">
        <span class="badge badge-success">
          <ShieldCheck :size="12" />
          ALL 6 CONTROLS ACTIVE
        </span>
      </div>
    </div>

    <!-- 6 Executive Control Cards Grid -->
    <div class="controls-grid">
      <!-- 1. Transaction Risk -->
      <div class="card control-card">
        <div class="card-header">
          <div class="card-title">
            <Activity :size="14" />
            Transaction Risk Engine
          </div>
          <span class="badge badge-success">LOW RISK</span>
        </div>
        <div class="control-body">
          <div class="control-stat">
            <span class="stat-number text-green">453 <small>BPS</small></span>
            <span class="stat-meta">Current Risk Score (4.53%)</span>
          </div>
          <div class="control-items">
            <div class="c-item"><span>Max Allowed Risk:</span> <strong>2,500 BPS (25.0%)</strong></div>
            <div class="c-item"><span>Circuit Breaker:</span> <strong class="text-green">ARMED</strong></div>
          </div>
        </div>
      </div>

      <!-- 2. Compliance State -->
      <div class="card control-card">
        <div class="card-header">
          <div class="card-title">
            <CheckCircle2 :size="14" />
            Regulatory Compliance
          </div>
          <span class="badge badge-success">PASS</span>
        </div>
        <div class="control-body">
          <div class="control-stat">
            <span class="stat-number text-blue">100%</span>
            <span class="stat-meta">China Policy Rules Verified</span>
          </div>
          <div class="control-items">
            <div class="c-item"><span>PIPL PII Scan:</span> <strong class="text-green">0 Leaks</strong></div>
            <div class="c-item"><span>Cross-Border:</span> <strong class="text-green">Compliant</strong></div>
          </div>
        </div>
      </div>

      <!-- 3. Recipient Whitelist -->
      <div class="card control-card">
        <div class="card-header">
          <div class="card-title">
            <Users :size="14" />
            Approved Counterparties
          </div>
          <span class="badge badge-info">ENFORCED</span>
        </div>
        <div class="control-body">
          <div class="control-stat">
            <span class="stat-number">3 Entities</span>
            <span class="stat-meta">Authorized Treasury Whitelist</span>
          </div>
          <div class="control-items">
            <div class="c-item"><span>Unauthorized Accounts:</span> <strong class="text-amber">Vetoed Fail-Closed</strong></div>
            <div class="c-item"><span>Per-Entity Limits:</span> <strong class="text-green">Active</strong></div>
          </div>
        </div>
      </div>

      <!-- 4. Replay Protection -->
      <div class="card control-card">
        <div class="card-header">
          <div class="card-title">
            <Repeat :size="14" />
            Replay Protection
          </div>
          <span class="badge badge-success">ACTIVE</span>
        </div>
        <div class="control-body">
          <div class="control-stat">
            <span class="stat-number text-green">Fsynced</span>
            <span class="stat-meta">Persistent Nonce & Intent Guard</span>
          </div>
          <div class="control-items">
            <div class="c-item"><span>Process Restart Defense:</span> <strong class="text-green">Validated</strong></div>
            <div class="c-item"><span>Expired Intents:</span> <strong class="text-amber">Rejected</strong></div>
          </div>
        </div>
      </div>

      <!-- 5. Proof-of-Intent -->
      <div class="card control-card">
        <div class="card-header">
          <div class="card-title">
            <FileCheck :size="14" />
            Proof-of-Intent
          </div>
          <span class="badge badge-success">LOCKED</span>
        </div>
        <div class="control-body">
          <div class="control-stat">
            <span class="stat-number text-indigo">18 Fields</span>
            <span class="stat-meta">Canonical Zero-Float Hash</span>
          </div>
          <div class="control-items">
            <div class="c-item"><span>On-Chain Registration:</span> <strong class="text-green">PoIRegistry.sol</strong></div>
            <div class="c-item"><span>Commitment Binding:</span> <strong class="text-green">Universal</strong></div>
          </div>
        </div>
      </div>

      <!-- 6. Pre-Execution Audit -->
      <div class="card control-card">
        <div class="card-header">
          <div class="card-title">
            <Shield :size="14" />
            Pre-Execution Audit
          </div>
          <span class="badge badge-success">ENFORCED</span>
        </div>
        <div class="control-body">
          <div class="control-stat">
            <span class="stat-number text-cyan">Block 99 &lt; 100</span>
            <span class="stat-meta">Application-Enforced Ordering</span>
          </div>
          <div class="control-items">
            <div class="c-item"><span>Audit Registry:</span> <strong class="text-green">Committed Prior</strong></div>
            <div class="c-item"><span>Unanchored Signing:</span> <strong class="text-amber">Blocked</strong></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Drawer Inspection Actions -->
    <div class="card inspection-card">
      <div class="insp-left">
        <span class="insp-title">Deep Technical Verifications</span>
        <span class="insp-desc">Inspect machine-grounded regulatory corpus evidence or current effective policy invariants.</span>
      </div>
      <div class="insp-actions">
        <button class="btn-secondary" @click="showRagDrawer = true">
          <BookOpen :size="13" />
          Inspect Regulatory Sources (5)
        </button>
        <button class="btn-secondary" @click="showPolicyDrawer = true">
          <FileText :size="13" />
          Inspect Policy Invariants
        </button>
      </div>
    </div>

    <!-- Institutional Disclaimer Box -->
    <div class="disclaimer-banner">
      <div class="disc-row">
        <strong>COMPLIANCE SCOPE:</strong> Automated project policy controls and machine-readable risk verification baseline.
      </div>
      <div class="disc-row">
        <strong>LEGAL CERTIFICATION:</strong> NO (Automated machine-readable compliance gate for competition demonstration).
      </div>
    </div>

    <!-- Regulatory Sources Slide-Over Drawer -->
    <div v-if="showRagDrawer" class="drawer-backdrop" @click.self="showRagDrawer = false">
      <div class="drawer-panel">
        <div class="drawer-header">
          <div class="drawer-title">Regulatory RAG Grounding Evidence (BGE-M3)</div>
          <button class="btn-ghost" @click="showRagDrawer = false">
            <X :size="16" />
          </button>
        </div>

        <div class="drawer-body">
          <p class="drawer-intro">
            Top-5 supporting Chinese financial legal passages retrieved from 291 indexed regulatory corpus chunks via BGE-M3 1024-d dense embeddings.
          </p>

          <div class="sources-list">
            <div class="source-card">
              <div class="source-top">
                <span class="source-code">CN-CSL</span>
                <span class="badge badge-info">ART. 21, 31</span>
              </div>
              <strong class="source-name">Cybersecurity Law of the People's Republic of China</strong>
              <p class="source-desc">Mandates categorized protection of network security and critical information infrastructure operational resilience.</p>
            </div>

            <div class="source-card">
              <div class="source-top">
                <span class="source-code">CN-DSL</span>
                <span class="badge badge-info">HIERARCHICAL</span>
              </div>
              <strong class="source-name">Data Security Law of the People's Republic of China</strong>
              <p class="source-desc">Establishes categorized data protection, national core data management, and risk monitoring mechanisms.</p>
            </div>

            <div class="source-card">
              <div class="source-top">
                <span class="source-code">CN-PIPL</span>
                <span class="badge badge-info">ART. 13, 28</span>
              </div>
              <strong class="source-name">Personal Information Protection Law (PIPL)</strong>
              <p class="source-desc">Strict handling rules for sensitive financial personal information, resident identity numbers, and biometric data.</p>
            </div>

            <div class="source-card">
              <div class="source-top">
                <span class="source-code">CN-AML</span>
                <span class="badge badge-info">FINANCIAL AML</span>
              </div>
              <strong class="source-name">Anti-Money Laundering Law for Financial Institutions</strong>
              <p class="source-desc">Customer identification requirements and reporting thresholds for large-scale or high-velocity abnormal transactions.</p>
            </div>

            <div class="source-card">
              <div class="source-top">
                <span class="source-code">CN-CROSSBORDER</span>
                <span class="badge badge-info">STANDARD PROVISIONS</span>
              </div>
              <strong class="source-name">Provisions on Promoting Cross-Border Data Flows</strong>
              <p class="source-desc">Security assessment thresholds and regulatory exemptions for standardized domestic financial consortium transmissions.</p>
            </div>
          </div>
        </div>

        <div class="drawer-footer">
          <button class="btn-secondary" @click="showRagDrawer = false">Close</button>
        </div>
      </div>
    </div>

    <!-- Policy Invariants Drawer -->
    <div v-if="showPolicyDrawer" class="drawer-backdrop" @click.self="showPolicyDrawer = false">
      <div class="drawer-panel">
        <div class="drawer-header">
          <div class="drawer-title">Effective Financial Policy Invariants</div>
          <button class="btn-ghost" @click="showPolicyDrawer = false">
            <X :size="16" />
          </button>
        </div>

        <div class="drawer-body">
          <div class="policy-manifest">
            <div class="manifest-row">
              <span>Policy Manifest:</span>
              <strong class="mono">aegis-effective-financial-policy-v1</strong>
            </div>
            <div class="manifest-row">
              <span>Composite Hash:</span>
              <strong class="mono text-blue">0xc7a89fd05228298a42a48f113e168f044e919fabdf240178db3c49a628cfdf38</strong>
            </div>
          </div>

          <div class="invariants-table">
            <div class="inv-row">
              <span>Max Per-Transaction Limit:</span>
              <strong>10.00 ACNY (1,000 minor units)</strong>
            </div>
            <div class="inv-row">
              <span>Daily Volume Cap:</span>
              <strong>100.00 ACNY (10,000 minor units)</strong>
            </div>
            <div class="inv-row">
              <span>Max Calculated Risk Threshold:</span>
              <strong>2,500 BPS (25.00%)</strong>
            </div>
            <div class="inv-row">
              <span>Minimum ACNY Reserve Floor:</span>
              <strong>6,500 BPS (65.00%)</strong>
            </div>
            <div class="inv-row">
              <span>Maximum Synthetic AGIX Cap:</span>
              <strong>4,000 BPS (40.00%)</strong>
            </div>
            <div class="inv-row">
              <span>Signer Key Isolation:</span>
              <strong class="text-green">Zero AI Key Exposure (Enforced)</strong>
            </div>
          </div>
        </div>

        <div class="drawer-footer">
          <button class="btn-secondary" @click="showPolicyDrawer = false">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  ShieldCheck, 
  Activity, 
  CheckCircle2, 
  Users, 
  Repeat, 
  FileCheck, 
  Shield, 
  BookOpen, 
  FileText, 
  X 
} from 'lucide-vue-next'

const showRagDrawer = ref(false)
const showPolicyDrawer = ref(false)
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

.controls-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.control-card {
  display: flex;
  flex-direction: column;
  padding: 18px;
}

.control-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.control-stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-number {
  font-size: 24px;
  font-weight: 800;
  color: #09090b;
  letter-spacing: -0.5px;
}

.stat-number small {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.stat-meta {
  font-size: 11px;
  color: #64748b;
}

.control-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 11.5px;
}

.c-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.c-item span {
  color: #64748b;
}

.c-item strong {
  color: #09090b;
}

.inspection-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #ffffff;
  border: 1px solid var(--border);
}

.insp-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.insp-title {
  font-size: 13px;
  font-weight: 700;
  color: #09090b;
}

.insp-desc {
  font-size: 11.5px;
  color: #64748b;
}

.insp-actions {
  display: flex;
  gap: 10px;
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
  font-size: 11.5px;
  color: #78350f;
}

.drawer-intro {
  font-size: 12px;
  color: #334155;
}

.sources-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.source-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.source-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.source-code {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 700;
  color: #1e3a8a;
}

.source-name {
  font-size: 12px;
  font-weight: 600;
  color: #09090b;
}

.source-desc {
  font-size: 11px;
  color: #475569;
  line-height: 1.4;
}

.policy-manifest {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
}

.manifest-row {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.manifest-row span {
  font-size: 11px;
  color: #64748b;
}

.manifest-row strong {
  color: #09090b;
}

.invariants-table {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
}

.inv-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  padding: 6px 0;
  border-bottom: 1px solid var(--border-subtle);
}

.inv-row:last-child {
  border-bottom: none;
}

.inv-row span {
  color: #64748b;
}

.inv-row strong {
  color: #09090b;
}

.text-blue { color: #1e3a8a; }
.text-green { color: #166534; }
.text-indigo { color: #3730a3; }
.text-cyan { color: #0e7490; }
.text-amber { color: #b45309; }
</style>
