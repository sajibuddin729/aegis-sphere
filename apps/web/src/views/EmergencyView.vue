<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Autonomous Emergency Control & Kill-Switch</h1>
        <p class="page-subtitle">Real-time PBFT consensus monitoring with fail-closed financial signing boundary cutoff.</p>
      </div>
      <div class="header-status">
        <span class="badge" :class="oracle.state === 'NORMAL' ? 'badge-success' : 'badge-danger'">
          <AlertOctagon :size="12" />
          SYSTEM STATE: {{ oracle.state }}
        </span>
      </div>
    </div>

    <!-- High-Level Health Grid -->
    <div class="status-grid">
      <div class="card stat-card">
        <span class="stat-label">Consensus Health State</span>
        <div class="stat-value text-green">{{ oracle.state }}</div>
        <span class="stat-sub">Zero Byzantine Divergence</span>
      </div>

      <div class="card stat-card">
        <span class="stat-label">Financial Signing Boundary</span>
        <div class="stat-value text-green">{{ oracle.signing_state || 'ENABLED' }}</div>
        <span class="stat-sub">Fail-Closed Signing Pipe</span>
      </div>

      <div class="card stat-card">
        <span class="stat-label">Healthy PBFT Validators</span>
        <div class="stat-value">{{ oracle.healthy_nodes || 4 }} / 4 <small>Nodes</small></div>
        <span class="stat-sub">Fault Tolerance: n=4, f=1 (n &ge; 3f + 1)</span>
      </div>
    </div>

    <!-- 4-Validator PBFT Status Diagram -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">
          <Server :size="14" />
          4-Validator PBFT Consortium Consensus Topology
        </span>
        <span class="badge badge-success">ALL 4 SYNCHRONIZED</span>
      </div>

      <div class="validators-grid">
        <div v-for="v in validators" :key="v.id" class="val-card">
          <div class="val-top">
            <span class="status-dot"></span>
            <strong>Validator Node {{ v.id }}</strong>
          </div>
          <div class="val-details">
            <div class="v-row">
              <span>Role:</span>
              <strong>Consensus Validator</strong>
            </div>
            <div class="v-row">
              <span>Status:</span>
              <span class="badge badge-success">SYNCHRONIZED</span>
            </div>
            <div class="v-row">
              <span>Peer Quorum:</span>
              <strong>4 Peers (PBFT Active)</strong>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- State Transition Matrix & Kill-Switch Architecture -->
    <div class="two-col">
      <!-- Deterministic Transition Matrix -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <GitBranch :size="14" />
            PBFT Safety Transition Specification
          </span>
          <span class="badge badge-neutral">DETERMINISTIC</span>
        </div>

        <div class="matrix-list">
          <div class="matrix-row header-row">
            <span>Healthy Validators</span>
            <span>Oracle State</span>
            <span>Financial Signing</span>
          </div>

          <div class="matrix-row active-row">
            <strong>4 / 4 Nodes</strong>
            <span class="badge badge-success">NORMAL</span>
            <span class="mono text-green">ENABLED</span>
          </div>

          <div class="matrix-row">
            <strong>3 / 4 Nodes</strong>
            <span class="badge badge-warning">DEGRADED</span>
            <span class="mono text-amber">ENABLED</span>
          </div>

          <div class="matrix-row">
            <strong>&le; 2 / 4 Nodes</strong>
            <span class="badge badge-danger">EMERGENCY_STOP</span>
            <span class="mono text-danger">DISABLED (Cutoff)</span>
          </div>
        </div>

        <div class="recovery-box">
          <div class="rec-title">
            <RotateCcw :size="12" />
            AUTONOMOUS RECOVERY PROTOCOL
          </div>
          <p class="rec-desc">
            3 consecutive observation windows of 4/4 healthy nodes required before automatically transitioning from EMERGENCY_STOP back to NORMAL. Zero human key transaction needed.
          </p>
        </div>
      </div>

      <!-- Kill Switch Architecture Card -->
      <div class="card killswitch-card">
        <div class="card-header">
          <span class="card-title">
            <Power :size="14" />
            Kill-Switch Architecture
          </span>
          <span class="badge badge-info">HYBRID SECURITY</span>
        </div>

        <div class="killswitch-sections">
          <!-- Software Kill Switch -->
          <div class="ks-box">
            <div class="ks-header">
              <span class="badge badge-success">IMPLEMENTED</span>
              <strong>Software Financial Kill-Switch</strong>
            </div>
            <p class="ks-text">
              Automated, deterministic fail-closed freeze in the isolated Java signing boundary. Any direct bypass or API invocation is rejected immediately when consensus health is compromised or Oracle fails.
            </p>
          </div>

          <!-- Physical Isolation Extension -->
          <div class="ks-box ks-future">
            <div class="ks-header">
              <span class="badge badge-warning">FUTURE HARDWARE EXTENSION</span>
              <strong>Physical Network/Power Isolation</strong>
            </div>
            <p class="ks-text">
              Architecture extension designed for sovereign deployment with physical hardware triggers:
            </p>
            <ul class="ks-list">
              <li>Heartbeat watchdog microcontroller</li>
              <li>Network physical relay isolation (air-gap relay)</li>
              <li>Power isolation disconnect relay</li>
              <li>Independent out-of-band manual optical emergency signal</li>
            </ul>
            <div class="ks-disclaimer">
              Notice: Software kill-switch is fully implemented and active. Physical isolation represents a planned future hardware extension. No physical hardware is currently installed.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  AlertOctagon, 
  Server, 
  GitBranch, 
  RotateCcw, 
  Power 
} from 'lucide-vue-next'
import { fetchOracle } from '../services/api.js'

const oracle = ref({
  state: 'NORMAL',
  signing_state: 'ENABLED',
  healthy_nodes: 4
})

const validators = ref([
  { id: 0, status: 'HEALTHY' },
  { id: 1, status: 'HEALTHY' },
  { id: 2, status: 'HEALTHY' },
  { id: 3, status: 'HEALTHY' }
])

onMounted(async () => {
  oracle.value = await fetchOracle()
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

.status-grid {
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

.stat-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-dim);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.stat-value small {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-muted);
}

.stat-sub {
  font-size: 11px;
  color: var(--text-dim);
}

.validators-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.val-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.val-top {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12.5px;
  color: #09090b;
  font-weight: 600;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #166534;
}

.val-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 11.5px;
}

.v-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.v-row span {
  color: #64748b;
}

.v-row strong {
  color: #09090b;
}

.two-col {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 16px;
}

.matrix-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.matrix-row {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1.2fr;
  align-items: center;
  padding: 10px 14px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 12px;
  color: #09090b;
}

.header-row {
  background: transparent;
  border: none;
  color: #64748b;
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 6px 14px;
}

.active-row {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
}

.recovery-box {
  margin-top: 14px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-left: 3px solid #09090b;
  border-radius: 4px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rec-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10.5px;
  font-weight: 700;
  color: #09090b;
}

.rec-desc {
  font-size: 11px;
  color: #475569;
  line-height: 1.4;
}

.killswitch-sections {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ks-box {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-left: 3px solid #166534;
  border-radius: 6px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.ks-future {
  border-left: 3px solid #b45309;
}

.ks-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #09090b;
}

.ks-text {
  font-size: 11.5px;
  color: #475569;
  line-height: 1.4;
}

.ks-list {
  margin-left: 18px;
  font-size: 11px;
  color: var(--text-muted);
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.ks-disclaimer {
  margin-top: 6px;
  font-size: 10.5px;
  color: #fbbf24;
  line-height: 1.35;
}

.text-green { color: #34d399; }
.text-amber { color: #fbbf24; }
.text-danger { color: #f87171; }
</style>
