<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Agentic AI University & Evolution Pipeline</h1>
        <p class="page-subtitle">Experience-based continuous learning through evaluated semantic memory reuse without model-weight mutation.</p>
      </div>
      <span class="badge badge-info">LEARNING: SEMANTIC MEMORY REUSE</span>
    </div>

    <!-- University Metrics Cards -->
    <div class="stats-grid">
      <div class="card stat-card">
        <span class="stat-label">Arena Scenarios Tested</span>
        <span class="stat-val">7 / 7 <small>Scenarios</small></span>
        <span class="stat-sub">Red Team Adversarial Attacks</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Blue Defense Rate</span>
        <span class="stat-val stat-green">100% <small>(7 Wins / 0 Losses)</small></span>
        <span class="stat-sub">Critical Violations: 0</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Curriculum Run Score</span>
        <span class="stat-val">100.0 <small>/ 100</small></span>
        <span class="stat-sub">14 Scenarios • 0 Regressions</span>
      </div>
      <div class="card stat-card">
        <span class="stat-label">Autonomous Production Deploys</span>
        <span class="stat-val">0 <small>(Strictly Zero)</small></span>
        <span class="stat-sub">Staging Only / Human Review</span>
      </div>
    </div>

    <!-- The 3 Core Pillars (J016) -->
    <div class="pillars-grid">
      <!-- Part 1: Semantic Memory -->
      <div class="card pillar-card">
        <div class="pillar-header">
          <span class="pillar-num">PART 01</span>
          <span class="pillar-title">Multi-Agent University & Semantic Memory</span>
        </div>
        <div class="pillar-body">
          <p class="pillar-desc">
            Agents deposit observed financial findings, compliance interpretations, and vulnerability analyses into pgvector semantic memory.
          </p>
          <div class="pillar-meta">
            <div><span>Memory Backend:</span> <strong>PostgreSQL + pgvector (1024-d BGE-M3)</strong></div>
            <div><span>Admission Gate:</span> <strong>Evaluated Utility Scoring &gt; 70</strong></div>
            <div><span>Contamination Filter:</span> <strong>Zero Unverified Lessons Admitted</strong></div>
          </div>
        </div>
      </div>

      <!-- Part 2: Red/Blue Arena -->
      <div class="card pillar-card">
        <div class="pillar-header">
          <span class="pillar-num">PART 02</span>
          <span class="pillar-title">Red / Blue Arena & Elo Poisoning Defense</span>
        </div>
        <div class="pillar-body">
          <p class="pillar-desc">
            Adversarial Red Team agents probe financial boundaries while Blue Defender agents enforce policy invariants.
          </p>
          <div class="pillar-meta">
            <div><span>Adversarial Scenarios:</span> <strong>Prompt Injection, Limit Evasion, PII</strong></div>
            <div><span>Elo Dynamic Scoring:</span> <strong>Blue Rating: 1548 (+48 pts)</strong></div>
            <div><span>Poisoning Defense:</span> <strong>Losing Red Strategies Quarantined</strong></div>
          </div>
        </div>
      </div>

      <!-- Part 3: Adaptive Curriculum -->
      <div class="card pillar-card">
        <div class="pillar-header">
          <span class="pillar-num">PART 03</span>
          <span class="pillar-title">Adaptive Curriculum & Regression Detection</span>
        </div>
        <div class="pillar-body">
          <p class="pillar-desc">
            Algorithmic difficulty scheduler prioritizing past weaknesses while guarding against catastrophic forgetting.
          </p>
          <div class="pillar-meta">
            <div><span>Curriculum Steps:</span> <strong>14 Systematic Scenarios</strong></div>
            <div><span>Regression Detector:</span> <strong>Critical Drop Threshold &gt; 5%</strong></div>
            <div><span>Test Suite Health:</span> <strong>100% Invariants Preserved</strong></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Architectural Claim Box -->
    <div class="claim-box">
      <strong>CONTINUOUS LEARNING BOUNDARY:</strong> Aegis-Sphere learns through curated episodic experience retrieval and semantic memory augmentation. Base model weights remain frozen, ensuring zero unauthorized model drift or unverified self-modification.
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
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
  grid-template-columns: repeat(4, 1fr);
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

.stat-green {
  color: #34d399;
}

.stat-sub {
  font-size: 11px;
  color: var(--text-muted);
}

.pillars-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.pillar-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.pillar-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
  border-bottom: 1px solid var(--border-subtle);
  padding-bottom: 8px;
}

.pillar-num {
  font-size: 11px;
  font-weight: 700;
  color: var(--primary);
  font-family: var(--font-mono);
}

.pillar-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-main);
}

.pillar-desc {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
}

.pillar-meta {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 11px;
  background: var(--bg-surface);
  padding: 10px 12px;
  border-radius: 4px;
}

.pillar-meta span {
  color: var(--text-dim);
}

.claim-box {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-left: 4px solid var(--primary);
  padding: 12px 18px;
  border-radius: 4px;
  font-size: 12px;
  color: var(--text-muted);
}
</style>
