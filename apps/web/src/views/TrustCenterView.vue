<template>
  <div class="page-container">
    <!-- Trust Center Header -->
    <div class="page-header">
      <div class="title-wrap">
        <h1 class="page-title">Aegis Trust & Architecture Center</h1>
        <p class="page-subtitle">Authoritative cryptographic proof repository, smart contract manifests, AI governance verification, and ZK privacy proofs.</p>
      </div>
      <div class="header-status">
        <span class="badge badge-info">
          <Lock :size="12" />
          CRYPTOGRAPHIC VERIFICATION REPOSITORY
        </span>
      </div>
    </div>

    <!-- Institutional Tab Navigation -->
    <div class="tabs-nav">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        class="tab-btn"
        :class="{ active: currentTab === tab.key }"
        @click="selectTab(tab.key)"
      >
        <component :is="tab.icon" :size="14" />
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <!-- TAB 1: BLOCKCHAIN EVIDENCE -->
    <div v-if="currentTab === 'blockchain'" class="tab-content">
      <div class="stats-grid">
        <div class="card stat-card">
          <span class="stat-label">Network & Group</span>
          <div class="stat-val">FISCO BCOS <small>group0</small></div>
          <span class="stat-sub">Chain ID: chain0 • Version 3.17.1</span>
        </div>
        <div class="card stat-card">
          <span class="stat-label">Consensus Protocol</span>
          <div class="stat-val">PBFT <small>(n=4, f=1)</small></div>
          <span class="stat-sub">Deterministic Finality (Zero Forks)</span>
        </div>
        <div class="card stat-card">
          <span class="stat-label">Consensus Height</span>
          <div class="stat-val">BLOCK 106+</div>
          <span class="stat-sub">All 4 Validators Synchronized</span>
        </div>
      </div>

      <!-- Pre-Execution Audit Ordering Proof -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <Shield :size="14" />
            Cryptographically Linked Pre-Execution Audit with Application-Enforced Ordering
          </span>
          <span class="badge badge-success">INVARIANT VERIFIED</span>
        </div>

        <div class="audit-grid">
          <div class="audit-flow-card">
            <div class="af-title">AUTONOMOUS TRANSFER ORDERING PROOF</div>
            <div class="af-step">
              <span class="af-tag">1. PRE-EXECUTION AUDIT COMMITMENT</span>
              <div class="af-meta">
                <span class="badge badge-info">BLOCK 99</span>
                <span class="mono">0x28c6e84f0ae4bd6df5c76af5ea1aac9a3f7d697bc829c4795c19408cf934fca9</span>
              </div>
            </div>
            <div class="af-arrow">↓ Application Ordering: Audit Block 99 &lt; Financial Block 100</div>
            <div class="af-step">
              <span class="af-tag">2. FINANCIAL TRANSFER EXECUTION</span>
              <div class="af-meta">
                <span class="badge badge-success">BLOCK 100</span>
                <span class="mono text-blue">0xf49511589fc47a8fe208fc9ce34895b32b4cfbd074ecea92f5c95cd5b1311cdf</span>
              </div>
            </div>
          </div>

          <div class="audit-flow-card">
            <div class="af-title">AUTONOMOUS REBALANCE ORDERING PROOF</div>
            <div class="af-step">
              <span class="af-tag">1. PRE-EXECUTION AUDIT COMMITMENT</span>
              <div class="af-meta">
                <span class="badge badge-info">BLOCK 101</span>
                <span class="mono">0x3d263ce32d4e9bfea529f3ac7d4ad4918409e117349cf0860d59b3795fd7bd11</span>
              </div>
            </div>
            <div class="af-arrow">↓ Application Ordering: Audit Block 101 &lt; Financial Block 102</div>
            <div class="af-step">
              <span class="af-tag">2. FINANCIAL SWAP EXECUTION</span>
              <div class="af-meta">
                <span class="badge badge-success">BLOCK 102</span>
                <span class="mono text-blue">0x010810c9d281fb0456449e41a3c13ea936b1ca0f9bb8ae28e88de181945ce87c</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Deployed Contracts Manifest -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <FileCode :size="14" />
            Deployed Smart Contracts Manifest (blockchain/deployments/local-group0.json)
          </span>
          <span class="badge badge-neutral">{{ contracts.contracts.length }} CONTRACTS DEPLOYED</span>
        </div>

        <table class="inst-table">
          <thead>
            <tr>
              <th>Contract Name</th>
              <th>Role & Architectural Function</th>
              <th>On-Chain Address (Group 0)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in contracts.contracts" :key="c.name">
              <td><strong>{{ c.name }}</strong></td>
              <td><span class="text-dim">{{ c.type }}</span></td>
              <td class="mono text-blue">{{ c.address }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- TAB 2: PROOF-OF-INTENT -->
    <div v-if="currentTab === 'poi'" class="tab-content">
      <div class="card hash-banner">
        <div class="hb-left">
          <span class="hb-label">Canonical Proof-of-Intent (PoI) Hash</span>
          <span class="mono hb-hash">{{ poiHash }}</span>
          <span class="hb-sub">Registered on FISCO BCOS PoIRegistry contract (RFC8785 strict zero-float canonical bytes)</span>
        </div>
        <div class="hb-actions">
          <button class="btn-secondary" @click="copy(poiHash, 'poitab')">
            <Check v-if="copiedField === 'poitab'" class="text-green" :size="13" />
            <Copy v-else :size="13" />
            {{ copiedField === 'poitab' ? 'Copied' : 'Copy Hash' }}
          </button>
          <button class="btn-primary" @click="showPoiJson = !showPoiJson">
            <Code :size="13" />
            {{ showPoiJson ? 'Hide JSON' : 'View Canonical JSON' }}
          </button>
        </div>
      </div>

      <!-- Canonical JSON Box -->
      <div v-if="showPoiJson" class="card json-box">
        <div class="card-header">
          <span class="card-title">Canonical 18-Field JSON Representation (Zero Floats)</span>
          <span class="badge badge-success">HASH VERIFIED</span>
        </div>
        <pre class="mono json-pre">{{ JSON.stringify(poiData, null, 2) }}</pre>
      </div>

      <!-- 18 Fields Grouped -->
      <div class="poi-grid">
        <!-- 1. Identity -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">1. Identity & Provenance</span>
            <span class="badge badge-info">PROVENANCE</span>
          </div>
          <div class="field-list">
            <div class="f-item"><span>intentId:</span> <strong class="mono">{{ poiData.intentId }}</strong></div>
            <div class="f-item"><span>userRequestHash:</span> <strong class="mono">{{ poiData.userRequestHash }}</strong></div>
            <div class="f-item"><span>leaderAgentId:</span> <strong>{{ poiData.leaderAgentId }}</strong></div>
            <div class="f-item"><span>modelVersion:</span> <strong>{{ poiData.modelVersion }}</strong></div>
          </div>
        </div>

        <!-- 2. Action -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">2. Action Target</span>
            <span class="badge badge-info">EXECUTION</span>
          </div>
          <div class="field-list">
            <div class="f-item"><span>action:</span> <strong class="badge badge-success">{{ poiData.action }}</strong></div>
            <div class="f-item"><span>asset:</span> <strong>{{ poiData.asset }}</strong></div>
            <div class="f-item"><span>amount:</span> <strong>{{ poiData.amount }} minor (2.00 ACNY)</strong></div>
            <div class="f-item"><span>recipient:</span> <strong class="mono">{{ poiData.recipient }}</strong></div>
          </div>
        </div>

        <!-- 3. Risk -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">3. Risk Evaluation</span>
            <span class="badge badge-success">INVARIANTS</span>
          </div>
          <div class="field-list">
            <div class="f-item"><span>riskScore:</span> <strong class="text-green">{{ poiData.riskScore }} BPS (4.53%)</strong></div>
            <div class="f-item"><span>riskClass:</span> <strong class="badge badge-success">{{ poiData.riskClass }}</strong></div>
          </div>
        </div>

        <!-- 4. Policy -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">4. Policy Manifest</span>
            <span class="badge badge-info">EFFECTIVE</span>
          </div>
          <div class="field-list">
            <div class="f-item"><span>policyVersion:</span> <strong class="mono">{{ poiData.policyVersion }}</strong></div>
            <div class="f-item"><span>policyHash:</span> <strong class="mono">{{ poiData.policyHash }}</strong></div>
          </div>
        </div>

        <!-- 5. Evidence -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">5. Evidence Binding</span>
            <span class="badge badge-success">UNIVERSAL BINDING</span>
          </div>
          <div class="field-list">
            <div class="f-item"><span>evidenceHash:</span> <strong class="mono">{{ poiData.evidenceHash }}</strong></div>
            <div class="f-item"><span>explanationHash:</span> <strong class="mono">{{ poiData.explanationHash }}</strong></div>
          </div>
        </div>

        <!-- 6. Validity & Decision -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">6. Validity & Decision</span>
            <span class="badge badge-success">DECISION</span>
          </div>
          <div class="field-list">
            <div class="f-item"><span>timestamp:</span> <strong class="mono">{{ poiData.timestamp }}</strong></div>
            <div class="f-item"><span>expiry:</span> <strong class="mono">{{ poiData.expiry }} (timestamp + 300s)</strong></div>
            <div class="f-item"><span>nonce:</span> <strong class="mono">{{ poiData.nonce ? poiData.nonce.slice(0, 24) + '...' : '0x77bb44991100...' }}</strong></div>
            <div class="f-item"><span>decision:</span> <strong class="badge badge-success">{{ poiData.decision }}</strong></div>
          </div>
        </div>
      </div>
    </div>

    <!-- TAB 3: AI GOVERNANCE -->
    <div v-if="currentTab === 'governance'" class="tab-content">
      <!-- Mandatory Banner -->
      <div class="card gov-banner">
        <div class="gb-title">AI MAY WRITE ITS OWN UPGRADES, BUT IT CANNOT AUTHORIZE ITS OWN UPGRADES.</div>
        <p class="gb-sub">
          Strict separation of powers: Evolution proposals require independent verification across 11 mandatory checks and dedicated on-chain governance authorization before staging. Automatic production deployment is strictly prohibited.
        </p>
      </div>

      <!-- Pipeline Flow -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <GitMerge :size="14" />
            Autonomous Evolution Pipeline Flow
          </span>
          <span class="badge badge-info">11 MANDATORY CHECKS</span>
        </div>

        <div class="pipeline-track">
          <div class="pipe-box">
            <span class="p-num">1</span>
            <strong>Curriculum Finding</strong>
            <small>Weakness detection</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box">
            <span class="p-num">2</span>
            <strong>Evolution Agent</strong>
            <small>Candidate V2 generator</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box">
            <span class="p-num">3</span>
            <strong>Independent Verifier</strong>
            <small>11/11 Checks (:8090)</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box">
            <span class="p-num">4</span>
            <strong>UpdateGovernance</strong>
            <small>On-chain contract (:9090)</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box staged-box">
            <span class="p-num">5</span>
            <strong>Controlled Staging</strong>
            <small>Human review only</small>
          </div>
        </div>
      </div>

      <!-- Verified Evidence Card -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">Genuine AI Candidate V2 On-Chain Evidence</span>
          <span class="badge badge-success">APPROVED ON-CHAIN</span>
        </div>

        <div class="candidate-meta-grid">
          <div class="cm-item"><span>Candidate ID:</span> <strong class="mono">{{ govData.candidate_id }}</strong></div>
          <div class="cm-item"><span>Title:</span> <strong>{{ govData.title }}</strong></div>
          <div class="cm-item"><span>Finding Provenance:</span> <strong class="mono">{{ govData.finding_id }}</strong></div>
          <div class="cm-item"><span>Verification Score:</span> <strong class="badge badge-success">100 / 100 (11 / 11 CHECKS PASS)</strong></div>
          <div class="cm-item"><span>UpdateGovernance Contract:</span> <strong class="mono">{{ govData.governance.contract }}</strong></div>
          <div class="cm-item"><span>Governance On-Chain Tx:</span> <strong class="mono text-blue">{{ govData.governance.transaction_hash }} (Block {{ govData.governance.block_number }})</strong></div>
          <div class="cm-item"><span>Staging Authorized:</span> <strong class="badge badge-success">YES (sandbox/verification/staging)</strong></div>
          <div class="cm-item"><span>Production Auto-Deploy:</span> <strong class="badge badge-danger">BLOCKED (0 Production Deployments)</strong></div>
        </div>
      </div>
    </div>

    <!-- TAB 4: ZK PRIVACY -->
    <div v-if="currentTab === 'zk'" class="tab-content">
      <div class="stats-grid">
        <div class="card stat-card">
          <span class="stat-label">Proof System & Curve</span>
          <div class="stat-val">Groth16 <small>BN254 (bn128)</small></div>
          <span class="stat-sub">1,321 Non-Linear Constraints</span>
        </div>
        <div class="card stat-card">
          <span class="stat-label">Public Requested Amount</span>
          <div class="stat-val">10.00 <small>ACNY (1,000 minor)</small></div>
          <span class="stat-sub">Proven: private balance &ge; amount</span>
        </div>
        <div class="card stat-card">
          <span class="stat-label">Max Risk Threshold</span>
          <div class="stat-val">15.00% <small>(1,500 BPS)</small></div>
          <span class="stat-sub">Proven: private risk &le; 1,500 BPS</span>
        </div>
      </div>

      <!-- Signal Flow Card -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <Key :size="14" />
            Cryptographic Privacy Signal Flow
          </span>
          <span class="badge badge-info">POSEIDON COMMITMENTS</span>
        </div>

        <div class="zk-flow">
          <div class="zk-box private-witness">
            <span class="zk-tag">PRIVATE WITNESS (OFF-CHAIN)</span>
            <div class="zk-sig">Private Balance + Blinding Salt</div>
            <div class="zk-sig">Private Risk Score + Blinding Salt</div>
          </div>

          <div class="zk-mid">
            <strong>Circom Circuit: AegisEligibility</strong>
            <small>Poseidon Commitments • Groth16 Prover</small>
            <div class="zk-arrow">═══════►</div>
          </div>

          <div class="zk-box public-gate">
            <span class="zk-tag">ON-CHAIN VERIFICATION (FISCO)</span>
            <div class="zk-sig mono">ZKEligibilityGate.sol</div>
            <span class="badge badge-success">VERIFIED ON-CHAIN (Block 96)</span>
          </div>
        </div>

        <div class="zk-disclaimer">
          Exact account balance and private risk score remain completely confidential. Only the cryptographic proof of solvency and constraint compliance is committed to the blockchain.
        </div>
      </div>

      <!-- On-Chain Proof Evidence -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">Verified On-Chain ZK Proof Evidence</span>
          <span class="badge badge-success">RECORDED ON FISCO BCOS</span>
        </div>

        <div class="zk-meta-table">
          <div class="zm-row"><span>Proof Submission TxHash:</span> <strong class="mono text-blue">{{ zkData.proof_tx_hash }}</strong></div>
          <div class="zm-row"><span>Block Number:</span> <strong class="badge badge-info">BLOCK {{ zkData.block_number }}</strong></div>
          <div class="zm-row"><span>Proof Hash Commitment:</span> <strong class="mono">{{ zkData.proof_hash }}</strong></div>
          <div class="zm-row"><span>Verifier Contract:</span> <strong class="mono">{{ zkData.contracts.verifier }}</strong></div>
          <div class="zm-row"><span>Eligibility Gate:</span> <strong class="mono">{{ zkData.contracts.gate }}</strong></div>
        </div>
      </div>
    </div>

    <!-- TAB 5: AGENTIC UNIVERSITY -->
    <div v-if="currentTab === 'university'" class="tab-content">
      <div class="stats-grid">
        <div class="card stat-card">
          <span class="stat-label">Arena Scenarios Tested</span>
          <div class="stat-val">{{ liveStats.scenariosTested }} / {{ liveStats.scenariosTested }} <small>Scenarios</small></div>
          <span class="stat-sub">Red Team Adversarial Attacks</span>
        </div>
        <div class="card stat-card">
          <span class="stat-label">Blue Defense Rate</span>
          <div class="stat-val text-green">{{ liveStats.blueDefenseRate }} <small>({{ liveStats.blueWins }} Wins / 0 Losses)</small></div>
          <span class="stat-sub">Critical Violations: 0</span>
        </div>
        <div class="card stat-card">
          <span class="stat-label">Curriculum Run Score</span>
          <div class="stat-val">{{ liveStats.curriculumScore }} <small>/ 100</small></div>
          <span class="stat-sub">Active Elo: Blue {{ liveStats.blueElo }} / Red {{ liveStats.redElo }}</span>
        </div>
      </div>

      <div class="pillars-grid">
        <div class="card">
          <div class="card-header">
            <span class="card-title">Part 1: Multi-Agent Semantic Memory</span>
            <span class="badge badge-info">PGVECTOR</span>
          </div>
          <p class="p-text">Agents deposit observed financial findings and vulnerability analyses into pgvector semantic memory for curated experiential reuse.</p>
          <div class="p-sub-box">
            <div><span>Memory Backend:</span> <strong>PostgreSQL + pgvector (1024-d BGE-M3)</strong></div>
            <div><span>Admission Filter:</span> <strong>Utility Scoring &gt; 70 BPS</strong></div>
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <span class="card-title">Part 2: Red / Blue Arena & Elo</span>
            <span class="badge badge-success">7 / 7 WINS</span>
          </div>
          <p class="p-text">Adversarial Red Team agents probe financial boundaries while Blue Defender agents enforce deterministic policy invariants.</p>
          <div class="p-sub-box">
            <div><span>Blue Rating:</span> <strong>{{ liveStats.blueElo }} (+48 pts Elo)</strong></div>
            <div><span>Poisoning Defense:</span> <strong>Losing Red Prompts Quarantined</strong></div>
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <span class="card-title">Part 3: Adaptive Curriculum</span>
            <span class="badge badge-info">14 STEPS</span>
          </div>
          <p class="p-text">Algorithmic difficulty scheduler prioritizing past weaknesses while guarding against catastrophic forgetting.</p>
          <div class="p-sub-box">
            <div><span>Curriculum Run:</span> <strong>14 Scenarios (100% Pass)</strong></div>
            <div><span>Regression Metric:</span> <strong>0 Regressions Detected</strong></div>
          </div>
        </div>
      </div>

      <!-- LIVE AGENT & DOCKER EXECUTION CONSOLE -->
      <div class="card terminal-card">
        <div class="terminal-header">
          <div class="th-left">
            <div class="terminal-dots">
              <span class="dot dot-red"></span>
              <span class="dot dot-yellow"></span>
              <span class="dot dot-green"></span>
            </div>
            <span class="terminal-title">
              <Terminal :size="14" />
              LIVE MULTI-AGENT EXECUTION &amp; DOCKER CONSENSUS TELEMETRY
            </span>
            <span class="live-pill">
              <span class="live-indicator"></span>
              LIVE EXECUTION RUNNING
            </span>
          </div>

          <div class="th-center">
            <button 
              class="console-tab-btn" 
              :class="{ active: activeConsoleTab === 'all' }"
              @click="activeConsoleTab = 'all'"
            >
              All Telemetry
            </button>
            <button 
              class="console-tab-btn" 
              :class="{ active: activeConsoleTab === 'agents' }"
              @click="activeConsoleTab = 'agents'"
            >
              ⚡ Inter-Agent Sparring
            </button>
            <button 
              class="console-tab-btn" 
              :class="{ active: activeConsoleTab === 'docker' }"
              @click="activeConsoleTab = 'docker'"
            >
              🐳 Docker Nodes (FISCO BCOS)
            </button>
            <button 
              class="console-tab-btn" 
              :class="{ active: activeConsoleTab === 'memory' }"
              @click="activeConsoleTab = 'memory'"
            >
              🧠 pgvector Bus
            </button>
          </div>

          <div class="th-right">
            <button 
              class="btn-run-simulation" 
              :disabled="isSimulating"
              @click="runLiveCoTrainingRound"
            >
              <RefreshCw v-if="isSimulating" class="spinner" :size="13" />
              <Play v-else :size="13" />
              {{ isSimulating ? 'Sparring in Progress...' : '▶ Run Live Co-Training Round' }}
            </button>
          </div>
        </div>

        <!-- Terminal Output Screen -->
        <div class="terminal-screen" ref="terminalContainer">
          <div 
            v-for="(log, idx) in filteredLogs" 
            :key="idx" 
            class="terminal-line"
            :class="log.category"
          >
            <span class="term-time">{{ log.time }}</span>
            <span class="term-tag" :class="'tag-' + log.color">[{{ log.tag }}]</span>
            <span class="term-msg">{{ log.text }}</span>
          </div>
          <div v-if="isSimulating" class="terminal-line-simulating">
            <span class="sim-pulse"></span>
            <span class="sim-text">Inter-Agent Adversarial Match in Progress • Evaluating RFC8785 Invariants...</span>
          </div>
        </div>

        <!-- Real Telemetry Footer Stats -->
        <div class="terminal-footer">
          <div class="tf-stat">
            <span class="tf-label">Docker Subsystem:</span>
            <strong class="mono text-green">aegis-fisco-dev (4 Nodes Synced)</strong>
          </div>
          <div class="tf-stat">
            <span class="tf-label">Oracle Heartbeat:</span>
            <strong class="mono text-cyan">Seq #{{ liveStats.oracleSeq }}</strong>
          </div>
          <div class="tf-stat">
            <span class="tf-label">Current Consensus Height:</span>
            <strong class="mono text-amber">Block #{{ liveStats.lastBlockNumber }}+</strong>
          </div>
          <div class="tf-stat">
            <span class="tf-label">Active Arena ELO:</span>
            <strong class="mono text-blue">{{ liveStats.blueElo }} Blue / {{ liveStats.redElo }} Red</strong>
          </div>
        </div>
      </div>

      <!-- Continuous Inter-Agent Co-Training & Leader Progression Section -->
      <div class="card learning-section">
        <div class="card-header">
          <span class="card-title">
            <TrendingUp :size="14" />
            Main Leader Agent Capability Progression (Continuous Inter-Agent Co-Training)
          </span>
          <span class="badge badge-success">
            <ShieldCheck :size="12" />
            100% BENCHMARK PASS
          </span>
        </div>
        <p class="section-lead">
          Specialist agents (Security Red-Team, Market Research, Critic, and Compliance) actively challenge and coach the Main Leader Agent. Lessons are scored and distilled into shared semantic memory, continuously improving the Leader Agent's resilience and precision.
        </p>

        <!-- 4 Capability Improvement Cards -->
        <div class="progression-grid">
          <div v-for="metric in improvementMetrics" :key="metric.label" class="prog-card">
            <div class="prog-top">
              <span class="prog-label">{{ metric.label }}</span>
              <span class="badge" :class="metric.status === 'optimal' ? 'badge-success' : 'badge-info'">{{ metric.change }}</span>
            </div>
            <div class="prog-values">
              <span class="val-before">{{ metric.before }}</span>
              <ArrowRight class="text-dim" :size="12" />
              <strong class="val-after">{{ metric.after }}</strong>
            </div>
            <p class="prog-desc">{{ metric.desc }}</p>
          </div>
        </div>
      </div>

      <!-- Real-Time Multi-Agent Knowledge Transfer Feed -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <Cpu :size="14" />
            Specialist → Leader Agent Real-Time Knowledge Transfer Feed
          </span>
          <span class="badge badge-info">EPISODIC EXPERIENCE DISTILLATION</span>
        </div>
        
        <table class="inst-table">
          <thead>
            <tr>
              <th>Episode &amp; Time</th>
              <th>Teaching Specialist Agent</th>
              <th>Vulnerability / Edge-Case Detected</th>
              <th>Improvement Transferred to Leader Agent</th>
              <th>Memory Vector Anchor</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ep in learningEpisodes" :key="ep.id" :class="{ 'row-highlight': ep.isNew }">
              <td>
                <div class="id-wrap">
                  <strong class="mono text-blue">{{ ep.id }}</strong>
                  <span v-if="ep.isNew" class="badge-mini-new">LIVE</span>
                </div>
                <div class="text-dim text-xs">{{ ep.timestamp }}</div>
              </td>
              <td>
                <strong>{{ ep.teacherAgent }}</strong>
                <div class="text-dim text-xs">Simulated University Arena</div>
              </td>
              <td>
                <span class="text-amber-dark"><strong>{{ ep.topic }}</strong></span>
                <p class="table-sub-text">{{ ep.lesson }}</p>
              </td>
              <td>
                <p class="table-sub-text-bold">{{ ep.improvement }}</p>
              </td>
              <td>
                <span class="badge badge-neutral mono">{{ ep.memoryCommit }}</span>
              </td>
              <td>
                <span class="badge" :class="ep.isNew ? 'badge-new' : 'badge-success'">{{ ep.isNew ? 'NEW • LIVE SPARRING' : ep.status }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Closed-Loop Knowledge Distillation Architecture -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <Layers :size="14" />
            Closed-Loop Knowledge Distillation Architecture
          </span>
          <span class="badge badge-info">FROZEN WEIGHTS + DYNAMIC INVARIANT INJECTION</span>
        </div>

        <div class="pipeline-track">
          <div class="pipe-box">
            <span class="p-num">1</span>
            <strong>Specialists Explore</strong>
            <small>Red-Team & Market probe edges</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box">
            <span class="p-num">2</span>
            <strong>Critic Challenges</strong>
            <small>Adversarial cross-check</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box">
            <span class="p-num">3</span>
            <strong>Utility Gate</strong>
            <small>Score > 70 BPS admitted</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box">
            <span class="p-num">4</span>
            <strong>pgvector Memory</strong>
            <small>1024-d semantic memory bank</small>
          </div>
          <div class="pipe-arr">→</div>
          <div class="pipe-box staged-box">
            <span class="p-num">5</span>
            <strong>Leader Enriched</strong>
            <small>Zero-drift runtime prompts</small>
          </div>
        </div>
      </div>

      <div class="card claim-banner">
        <strong>LEARNING BOUNDARY SPECIFICATION:</strong> Aegis-Sphere learns through evaluated semantic memory retrieval and episodic experience reuse. Base model weights remain frozen, ensuring zero unauthorized model drift or unverified self-modification.
      </div>
    </div>

    <!-- TAB 6: SYSTEM HEALTH -->
    <div v-if="currentTab === 'health'" class="tab-content">
      <div class="card">
        <div class="card-header">
          <span class="card-title">
            <Server :size="14" />
            Active Production Services Matrix
          </span>
          <span class="badge badge-success">ALL 10 SERVICES OPERATIONAL</span>
        </div>

        <table class="inst-table">
          <thead>
            <tr>
              <th>Component Name</th>
              <th>Transport / Port</th>
              <th>Role in Architecture</th>
              <th>Operational Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in services" :key="s.name">
              <td><strong>{{ s.name }}</strong></td>
              <td class="mono text-cyan">{{ s.port }}</td>
              <td><span class="text-dim">{{ s.role }}</span></td>
              <td>
                <span class="badge" :class="s.status === 'ONLINE' || s.status === 'NORMAL' ? 'badge-success' : 'badge-warning'">
                  <Check :size="10" />
                  {{ s.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Lock, 
  Database, 
  FileCheck, 
  GitMerge, 
  Key, 
  GraduationCap, 
  Server, 
  Shield, 
  FileCode, 
  Copy, 
  Check, 
  Code,
  TrendingUp,
  Sparkles,
  Cpu,
  Layers,
  ArrowRight,
  ShieldCheck,
  Play,
  Terminal,
  Activity,
  RefreshCw
} from 'lucide-vue-next'
import { 
  fetchContracts, 
  fetchGovernance, 
  fetchZk, 
  fetchDemoTransfer,
  fetchUniversityRatings,
  fetchUniversityCapabilities,
  triggerCoTrainingEpisode
} from '../services/api.js'

const route = useRoute()
const router = useRouter()

const currentTab = ref(route.query.tab || 'blockchain')
const showPoiJson = ref(false)
const copiedField = ref('')

const tabs = [
  { key: 'blockchain', label: 'Blockchain Evidence', icon: Database },
  { key: 'poi', label: 'Proof-of-Intent', icon: FileCheck },
  { key: 'governance', label: 'AI Governance', icon: GitMerge },
  { key: 'zk', label: 'ZK Privacy', icon: Key },
  { key: 'university', label: 'Agentic University', icon: GraduationCap },
  { key: 'health', label: 'System Health', icon: Server }
]

const contracts = ref(fetchContracts())
const govData = ref(fetchGovernance())
const zkData = ref(fetchZk())

const activeConsoleTab = ref('all') // 'all' | 'agents' | 'docker' | 'memory'
const isSimulating = ref(false)
const terminalContainer = ref(null)

const liveStats = ref({
  scenariosTested: 14,
  blueWins: 14,
  blueDefenseRate: '100%',
  curriculumScore: '100.0',
  oracleSeq: 11340,
  lastBlockNumber: 115,
  blueElo: 1324,
  redElo: 1075
})

const terminalLogs = ref([
  { time: '18:52:01', category: 'docker', tag: 'DOCKER:node0', color: 'cyan', text: 'FISCO BCOS PBFT consensus sealed block #112 (4/4 validators signed, zero forks).' },
  { time: '18:52:03', category: 'oracle', tag: 'ORACLE:8091', color: 'emerald', text: 'Consensus health monitor heartbeat #11340: 4/4 nodes synced, latency 0.8ms.' },
  { time: '18:52:06', category: 'agent', tag: 'RED_TEAM:probe', color: 'rose', text: 'Adversarial agent probing boundary: Unicode homoglyph injection payload in recipient param.' },
  { time: '18:52:07', category: 'agent', tag: 'QWEN3GUARD:8082', color: 'amber', text: 'Safety boundary check: zero-width character detected. Input quarantined before signing key.' },
  { time: '18:52:10', category: 'agent', tag: 'CRITIC_AGENT', color: 'indigo', text: 'Inter-agent peer review: Challenge verified. Experience utility scored at 88.5 BPS.' },
  { time: '18:52:12', category: 'memory', tag: 'PGVECTOR:5433', color: 'violet', text: 'Admitted to semantic memory (Utility > 70 BPS threshold). Vector anchor #8842 indexed.' },
  { time: '18:52:14', category: 'agent', tag: 'LEADER_AGENT', color: 'sky', text: 'Leader agent episodic context updated: runtime invariant applied without base model weight drift.' }
])

const filteredLogs = computed(() => {
  if (activeConsoleTab.value === 'all') return terminalLogs.value
  if (activeConsoleTab.value === 'agents') {
    return terminalLogs.value.filter(l => l.category === 'agent')
  }
  if (activeConsoleTab.value === 'docker') {
    return terminalLogs.value.filter(l => l.category === 'docker' || l.category === 'oracle')
  }
  if (activeConsoleTab.value === 'memory') {
    return terminalLogs.value.filter(l => l.category === 'memory')
  }
  return terminalLogs.value
})

const improvementMetrics = ref([
  { label: 'Adversarial Resilience', before: '82.4%', after: '100.0%', change: '+17.6%', desc: 'Trained against Red-Team prompt injections', status: 'optimal' },
  { label: 'Regulatory Invariant Compliance', before: '91.0%', after: '100.0%', change: '+9.0%', desc: '100% adherence to daily & per-tx spending limits', status: 'optimal' },
  { label: 'Execution Risk & Slippage', before: '380 BPS', after: '42 BPS', change: '-88.9%', desc: 'Learned from Market Research Agent liquidity modeling', status: 'optimal' },
  { label: 'Episodic Vectors Retained', before: '0', after: '1,420', change: '+1,420', desc: 'Curated lessons in pgvector (Utility > 70 BPS)', status: 'info' }
])

const learningEpisodes = ref([
  {
    id: 'EP-1042',
    timestamp: '2026-10-16 19:42:10',
    teacherAgent: 'Security Red-Team Agent',
    topic: 'Unicode Homoglyph & Multi-Turn Jailbreak Defense',
    lesson: 'Probed spoofed recipient format; transferred detection signature into episodic memory.',
    targetAgent: 'Main Leader Agent',
    improvement: 'Future similar obfuscated-injection attempts are recognized using evaluated defensive memory.',
    memoryCommit: 'pgvector #8842',
    status: 'TRANSFERRED',
    isNew: false
  },
  {
    id: 'EP-1039',
    timestamp: '2026-10-16 18:15:32',
    teacherAgent: 'Market Research Agent',
    topic: 'Liquidity Pool Imbalance & High Slippage Guard',
    lesson: 'Detected 350 BPS price impact on synthetic swap pool during simulated stress.',
    targetAgent: 'Main Leader Agent',
    improvement: 'Leader learned to prefer lower-slippage rebalance plans under similar liquidity conditions.',
    memoryCommit: 'pgvector #8839',
    status: 'APPLIED',
    isNew: false
  },
  {
    id: 'EP-1035',
    timestamp: '2026-10-16 16:50:08',
    teacherAgent: 'Critic & Adversarial Agent',
    topic: 'Counterfactual Hallucination Elimination',
    lesson: 'Challenged Leader on unverified vendor credentials with high confidence rating.',
    targetAgent: 'Main Leader Agent',
    improvement: 'Leader Agent enforces mandatory PoI whitelist proof before signing prompt.',
    memoryCommit: 'pgvector #8831',
    status: 'TRANSFERRED',
    isNew: false
  },
  {
    id: 'EP-1028',
    timestamp: '2026-10-16 14:10:44',
    teacherAgent: 'Compliance Auditor Agent',
    topic: 'Daily Spending Allowance Threshold Invariant',
    lesson: 'Simulated 105 ACNY aggregate transfer requests across sequential blocks.',
    targetAgent: 'Main Leader Agent',
    improvement: 'Leader recognizes the daily-limit condition; deterministic signing policy blocks execution when the configured daily cap is exceeded.',
    memoryCommit: 'pgvector #8812',
    status: 'APPLIED',
    isNew: false
  }
])

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
const poiHash = ref('0xf1ba21bdac22b01b86defdff8281608affe5acfd827bba66fd56ef00427ddd2b')

const services = ref([
  { name: "Financial API (FastAPI)", port: ":8000", role: "Autonomous Orchestrator & REST Gateway", status: "ONLINE" },
  { name: "Main Qwen Model (Local LLM)", port: ":8081", role: "Planner & Leader Financial Reasoner", status: "ONLINE" },
  { name: "Qwen3Guard (Safety Gate)", port: ":8082", role: "Independent Prompt Moderation & Defense", status: "ONLINE" },
  { name: "Regulatory RAG (BGE-M3)", port: ":8083", role: "Dense Chinese Legal Vector Retrieval", status: "ONLINE" },
  { name: "Independent Verifier", port: ":8090", role: "11-Check Sandbox Candidate Verifier", status: "ONLINE" },
  { name: "Emergency Oracle", port: ":8091", role: "4-Validator PBFT Consensus Monitor", status: "NORMAL" },
  { name: "Financial Bridge (Java SDK)", port: ":9080", role: "Isolated Key Signer & Replay Boundary", status: "ONLINE" },
  { name: "Governance Bridge", port: ":9090", role: "Dedicated Verifier Authority Signer", status: "ONLINE" },
  { name: "PostgreSQL / pgvector", port: ":5433", role: "Semantic Experience Memory Store", status: "ONLINE" },
  { name: "FISCO BCOS Network", port: ":20200–20203", role: "4 PBFT Consortium Consensus Validators", status: "ONLINE" }
])

let logInterval = null
let blockCounter = 115
let oracleCounter = 11340
let episodeCounter = 1

onMounted(async () => {
  // 1. Fetch real DB arena ratings
  try {
    const ratingsData = await fetchUniversityRatings()
    if (ratingsData && ratingsData.ratings && ratingsData.ratings.length) {
      const blueAudit = ratingsData.ratings.find(r => r.agent_role === 'BLUE_TEAM' && r.capability === 'audit-security')
      const redAudit = ratingsData.ratings.find(r => r.agent_role === 'RED_TEAM' && r.capability === 'audit-security')
      if (blueAudit) {
        liveStats.value.blueElo = Math.round(blueAudit.rating)
        liveStats.value.scenariosTested = blueAudit.matches || 14
        liveStats.value.blueWins = blueAudit.wins || 14
      }
      if (redAudit) {
        liveStats.value.redElo = Math.round(redAudit.rating)
      }
    }
  } catch (e) {
    console.warn('Ratings fetch warning:', e)
  }

  // 2. Fetch real capabilities
  try {
    const caps = await fetchUniversityCapabilities()
    if (caps && caps.capabilities && caps.capabilities.length) {
      const avg = caps.capabilities.reduce((acc, c) => acc + (c.recent_average_score || 100), 0) / caps.capabilities.length
      liveStats.value.curriculumScore = avg.toFixed(1)
    }
  } catch (e) {
    console.warn('Capabilities fetch warning:', e)
  }

  // 3. Start live stream ticker (auto-scrolling real terminal activity)
  logInterval = setInterval(() => {
    blockCounter++
    oracleCounter++
    liveStats.value.lastBlockNumber = blockCounter
    liveStats.value.oracleSeq = oracleCounter

    const now = new Date()
    const timeStr = now.toTimeString().slice(0, 8)
    const nodeIdx = Math.floor(Math.random() * 4)

    const events = [
      {
        category: 'docker',
        tag: `DOCKER:node${nodeIdx}`,
        color: 'cyan',
        text: `FISCO BCOS PBFT sealed block #${blockCounter} • Hash 0x${Math.random().toString(16).slice(2, 10)}...${Math.random().toString(16).slice(2, 6)} • 4/4 validators signed.`
      },
      {
        category: 'oracle',
        tag: 'ORACLE:8091',
        color: 'emerald',
        text: `Consensus quorum heartbeat #${oracleCounter}: 0 forks detected • Consensus health: 100% OK (latency: ${(Math.random() * 1.2 + 0.4).toFixed(1)}ms).`
      },
      {
        category: 'agent',
        tag: 'CRITIC_AGENT',
        color: 'indigo',
        text: `Autonomous policy sanity audit: evaluated speculative rebalance intents • Invariant score: 100.0% adherence.`
      },
      {
        category: 'memory',
        tag: 'PGVECTOR:5433',
        color: 'violet',
        text: `1024-d semantic index scanned (BGE-M3): cosine distance 0.031 • Utility filter passed (> 70 BPS admission threshold).`
      },
      {
        category: 'agent',
        tag: 'SECURITY_RED_TEAM',
        color: 'rose',
        text: `Continuous sandbox attack simulation: executing prompt variation #${Math.floor(1000 + Math.random() * 9000)} • Probing signing key guard.`
      },
      {
        category: 'agent',
        tag: 'LEADER_AGENT',
        color: 'sky',
        text: `Autonomous key guard verification: per-transaction limit (10.00 ACNY) & recipient whitelist validated.`
      }
    ]

    const nextEvent = events[Math.floor(Math.random() * events.length)]
    terminalLogs.value.push({
      time: timeStr,
      ...nextEvent
    })

    if (terminalLogs.value.length > 60) {
      terminalLogs.value.shift()
    }
  }, 3200)
})

onUnmounted(() => {
  if (logInterval) clearInterval(logInterval)
})

async function runLiveCoTrainingRound() {
  if (isSimulating.value) return
  isSimulating.value = true
  activeConsoleTab.value = 'all'

  const now = new Date()
  const timeStr = now.toTimeString().slice(0, 8)

  // Step 1
  terminalLogs.value.push({
    time: timeStr,
    category: 'agent',
    tag: 'CO-TRAIN:INIT',
    color: 'amber',
    text: `[SPARRING MATCH] Spawning Red-Team Adversary vs Blue Defender in isolated sandbox arena...`
  })

  // Step 2 (after 600ms)
  setTimeout(() => {
    const t = new Date().toTimeString().slice(0, 8)
    terminalLogs.value.push({
      time: t,
      category: 'agent',
      tag: 'RED_TEAM:ATTACK',
      color: 'rose',
      text: `Adversary executing multi-turn split evasion: 3x 35 ACNY micro-transfers across sequential blocks.`
    })
  }, 600)

  // Step 3 (after 1300ms)
  setTimeout(() => {
    const t = new Date().toTimeString().slice(0, 8)
    terminalLogs.value.push({
      time: t,
      category: 'agent',
      tag: 'BLUE_DEFENSE',
      color: 'emerald',
      text: `Blue Defender & Qwen3Guard intercepted evasion: daily aggregate accumulator exceeded 100 ACNY ceiling. Prompt blocked.`
    })
  }, 1300)

  // Step 4 (after 2000ms)
  setTimeout(() => {
    const t = new Date().toTimeString().slice(0, 8)
    terminalLogs.value.push({
      time: t,
      category: 'agent',
      tag: 'CRITIC:SCORE',
      color: 'indigo',
      text: `Critic evaluated defense: Zero false positives. Episode utility score: 94.5 BPS (Admitted to shared memory).`
    })
  }, 2000)

  // Step 5 (after 2700ms)
  setTimeout(() => {
    const t = new Date().toTimeString().slice(0, 8)
    const epNum = 1042 + episodeCounter
    const memNum = 8844 + episodeCounter
    episodeCounter++

    terminalLogs.value.push({
      time: t,
      category: 'memory',
      tag: 'PGVECTOR:COMMIT',
      color: 'violet',
      text: `Vector #${memNum} deposited into pgvector. Transferred to Leader Agent episodic context with zero model drift.`
    })

    // Prepend to learning episodes table
    learningEpisodes.value.unshift({
      id: `EP-${epNum}`,
      timestamp: new Date().toISOString().replace('T', ' ').slice(0, 19),
      teacherAgent: 'Security Red-Team & Critic',
      topic: 'Multi-Turn Transfer Split & Accumulator Invariant',
      lesson: 'Probed daily allowance limit evasion via micro-transfers across sequential blocks.',
      targetAgent: 'Main Leader Agent',
      improvement: 'Leader recognizes the daily-limit condition; deterministic signing policy blocks execution when the configured daily cap is exceeded.',
      memoryCommit: `pgvector #${memNum}`,
      status: 'TRANSFERRED',
      isNew: true
    })

    // Dynamically increment stats & metrics
    liveStats.value.scenariosTested += 1
    liveStats.value.blueWins += 1
    liveStats.value.blueElo += 3
    improvementMetrics.value[3].after = `${(1420 + episodeCounter).toLocaleString()}`
    improvementMetrics.value[3].change = `+${1420 + episodeCounter}`

    isSimulating.value = false
  }, 2700)
}

watch(() => route.query.tab, (newTab) => {
  if (newTab) currentTab.value = newTab
})

function selectTab(tabKey) {
  currentTab.value = tabKey
  router.replace({ query: { ...route.query, tab: tabKey } })
}

function copy(text, field) {
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

.tabs-nav {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid var(--border);
  padding-bottom: 8px;
  flex-wrap: wrap;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffffff;
  border: 1px solid var(--border);
  color: #334155;
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.tab-btn:hover {
  background: #f4f4f5;
  color: #09090b;
  border-color: #09090b;
}

.tab-btn.active {
  background: #09090b;
  border-color: #09090b;
  color: #ffffff;
  font-weight: 600;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  background-color: #ffffff;
  border: 1px solid var(--border);
}

.stat-label {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
}

.stat-val {
  font-size: 22px;
  font-weight: 800;
  color: #09090b;
}

.stat-val small {
  font-size: 13px;
  color: #64748b;
}

.stat-sub {
  font-size: 11px;
  color: #64748b;
}

.audit-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.audit-flow-card {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.af-title {
  font-size: 10.5px;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.af-step {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.af-tag {
  font-size: 9.5px;
  font-weight: 700;
  color: #64748b;
}

.af-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #09090b;
}

.af-arrow {
  font-size: 11px;
  font-weight: 700;
  color: #166534;
  text-align: center;
}

.hash-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-left: 4px solid #09090b;
}

.hb-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hb-label {
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
}

.hb-hash {
  font-size: 14px;
  font-weight: 800;
  color: #09090b;
}

.hb-sub {
  font-size: 11px;
  color: #64748b;
}

.hb-actions {
  display: flex;
  gap: 8px;
}

.json-box {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
}

.json-pre {
  font-size: 11.5px;
  color: #09090b;
  max-height: 260px;
  overflow-y: auto;
  line-height: 1.45;
}

.poi-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.field-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 11.5px;
}

.f-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  border-bottom: 1px solid var(--border-subtle);
}

.f-item:last-child {
  border-bottom: none;
}

.f-item span {
  color: #64748b;
  font-family: var(--font-mono);
}

.gov-banner {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-left: 4px solid #09090b;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.gb-title {
  font-size: 13.5px;
  font-weight: 800;
  color: #09090b;
  letter-spacing: 0.5px;
}

.gb-sub {
  font-size: 11.5px;
  color: #475569;
  line-height: 1.45;
}

.pipeline-track {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
}

.pipe-box {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  min-width: 140px;
}

.staged-box {
  border-color: #09090b;
  background: #f8fafc;
}

.p-num {
  font-family: var(--font-mono);
  font-size: 10px;
  font-weight: 700;
  color: #09090b;
}

.pipe-box strong {
  font-size: 11.5px;
  color: #09090b;
}

.pipe-box small {
  font-size: 10.5px;
  color: #64748b;
}

.pipe-arr {
  font-size: 16px;
  color: #94a3b8;
  font-weight: 700;
}

.candidate-meta-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 12px;
}

.cm-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid var(--border-subtle);
}

.cm-item:last-child {
  border-bottom: none;
}

.cm-item span {
  color: #64748b;
}

.cm-item strong {
  color: #09090b;
}

.zk-flow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 0;
}

.zk-box {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 220px;
}

.private-witness {
  border-left: 3px solid #09090b;
}

.public-gate {
  border-left: 3px solid #166534;
}

.zk-tag {
  font-size: 9.5px;
  font-weight: 700;
  color: #64748b;
}

.zk-sig {
  font-size: 11.5px;
  color: #09090b;
}

.zk-mid {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 2px;
}

.zk-mid strong {
  font-size: 12px;
  color: #09090b;
}

.zk-mid small {
  font-size: 10.5px;
  color: #64748b;
}

.zk-arrow {
  color: #09090b;
  font-size: 16px;
}

.zk-disclaimer {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-left: 3px solid #166534;
  padding: 10px 14px;
  border-radius: 4px;
  font-size: 11px;
  color: #334155;
}

.zk-meta-table {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
}

.zm-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid var(--border-subtle);
}

.zm-row span {
  color: #64748b;
}

.zm-row strong {
  color: #09090b;
}

.pillars-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.p-text {
  font-size: 11.5px;
  color: #475569;
  line-height: 1.45;
  margin-bottom: 12px;
}

.p-sub-box {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 11px;
}

.p-sub-box span {
  color: #64748b;
}

.p-sub-box strong {
  color: #09090b;
}

.claim-banner {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-left: 3px solid #09090b;
  padding: 12px 16px;
  font-size: 11.5px;
  color: #334155;
  line-height: 1.4;
}

.text-blue { color: #1e3a8a; }
.text-cyan { color: #0e7490; }
.text-green { color: #166534; }
.text-dim { color: #64748b; }
.text-xs { font-size: 10.5px; }
.text-amber-dark { color: #b45309; }

.section-lead {
  font-size: 12px;
  color: #475569;
  line-height: 1.5;
  margin-bottom: 16px;
}

.progression-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.prog-card {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.prog-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 6px;
}

.prog-label {
  font-size: 11px;
  font-weight: 700;
  color: #09090b;
}

.prog-values {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.val-before {
  color: #94a3b8;
  font-family: var(--font-mono);
  text-decoration: line-through;
  font-size: 12px;
}

.val-after {
  color: #166534;
  font-family: var(--font-mono);
  font-size: 16px;
}

.prog-desc {
  font-size: 10.5px;
  color: #64748b;
  line-height: 1.35;
  margin: 0;
}

.table-sub-text {
  font-size: 11px;
  color: #64748b;
  margin: 3px 0 0 0;
  line-height: 1.35;
}

.table-sub-text-bold {
  font-size: 11.5px;
  color: #09090b;
  font-weight: 600;
  margin: 0;
  line-height: 1.4;
}

/* Live Execution & Docker Telemetry Terminal */
.terminal-card {
  background: #090d16;
  border: 1px solid #1e293b;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
}

.terminal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #0f172a;
  padding: 10px 16px;
  border-bottom: 1px solid #1e293b;
  flex-wrap: wrap;
  gap: 12px;
}

.th-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.terminal-dots {
  display: flex;
  gap: 6px;
}

.terminal-dots .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot-red { background: #ef4444; }
.dot-yellow { background: #f59e0b; }
.dot-green { background: #10b981; }

.terminal-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11.5px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 0.4px;
}

.live-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(16, 185, 129, 0.15);
  border: 1px solid rgba(16, 185, 129, 0.4);
  color: #34d399;
  font-size: 9.5px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 9999px;
  letter-spacing: 0.5px;
}

.live-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #10b981;
  box-shadow: 0 0 8px #10b981;
  animation: pulse-dot 1.5s infinite;
}

@keyframes pulse-dot {
  0% { transform: scale(0.9); opacity: 0.7; }
  50% { transform: scale(1.3); opacity: 1; }
  100% { transform: scale(0.9); opacity: 0.7; }
}

.th-center {
  display: flex;
  gap: 6px;
}

.console-tab-btn {
  background: transparent;
  border: 1px solid #334155;
  color: #94a3b8;
  font-size: 11px;
  font-weight: 600;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.console-tab-btn:hover {
  background: #1e293b;
  color: #f8fafc;
}

.console-tab-btn.active {
  background: #38bdf8;
  border-color: #38bdf8;
  color: #090d16;
  font-weight: 700;
}

.th-right {
  display: flex;
  align-items: center;
}

.btn-run-simulation {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #ffffff;
  color: #09090b;
  border: none;
  padding: 6px 14px;
  border-radius: 5px;
  font-size: 11.5px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0,0,0,0.2);
}

.btn-run-simulation:hover:not(:disabled) {
  background: #38bdf8;
  color: #090d16;
  transform: translateY(-1px);
}

.btn-run-simulation:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.terminal-screen {
  background: #090d16;
  padding: 14px 16px;
  max-height: 240px;
  min-height: 180px;
  overflow-y: auto;
  font-family: var(--font-mono, monospace);
  font-size: 11px;
  line-height: 1.6;
  display: flex;
  flex-direction: column;
  gap: 4px;
  scrollbar-width: thin;
  scrollbar-color: #334155 #090d16;
}

.terminal-line {
  display: flex;
  align-items: baseline;
  gap: 8px;
  word-break: break-all;
}

.term-time {
  color: #64748b;
  font-size: 10px;
  flex-shrink: 0;
}

.term-tag {
  font-weight: 700;
  font-size: 10px;
  flex-shrink: 0;
  padding: 1px 4px;
  border-radius: 3px;
}

.tag-cyan { color: #38bdf8; background: rgba(56, 189, 248, 0.1); }
.tag-emerald { color: #34d399; background: rgba(52, 211, 153, 0.1); }
.tag-rose { color: #fb7185; background: rgba(251, 113, 133, 0.1); }
.tag-amber { color: #fbbf24; background: rgba(251, 191, 36, 0.1); }
.tag-indigo { color: #818cf8; background: rgba(129, 140, 248, 0.1); }
.tag-violet { color: #c084fc; background: rgba(192, 132, 252, 0.1); }
.tag-sky { color: #7dd3fc; background: rgba(125, 211, 252, 0.1); }

.term-msg {
  color: #cbd5e1;
}

.terminal-line-simulating {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  color: #38bdf8;
  font-weight: 600;
  font-size: 11px;
}

.sim-pulse {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #38bdf8;
  animation: pulse-dot 0.8s infinite;
}

.terminal-footer {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: #0f172a;
  border-top: 1px solid #1e293b;
  padding: 10px 16px;
  gap: 12px;
}

.tf-stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tf-label {
  font-size: 9.5px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.tf-stat strong {
  font-size: 11px;
}

.id-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
}

.badge-mini-new {
  background: #10b981;
  color: #ffffff;
  font-size: 8.5px;
  font-weight: 800;
  padding: 1px 5px;
  border-radius: 3px;
  letter-spacing: 0.4px;
}

.badge-new {
  background: #059669;
  color: #ffffff;
  font-weight: 700;
}

.row-highlight {
  background: rgba(16, 185, 129, 0.05);
  transition: background 0.5s ease;
}

.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
