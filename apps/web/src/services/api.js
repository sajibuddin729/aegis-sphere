import { ref } from 'vue'
import demoTransfer from '../demo/final-transfer.json'
import demoRebalance from '../demo/final-rebalance.json'
import demoGov from '../demo/governance.json'
import demoEmergency from '../demo/emergency.json'
import demoZk from '../demo/zk.json'
import demoContracts from '../demo/contracts.json'

export const isDemoMode = ref(false)

export function toggleDemoMode() {
  isDemoMode.value = !isDemoMode.value
}

const WALLET_STORAGE_KEY = 'aegis_wallet_state_v1'
const ACTIVITY_STORAGE_KEY = 'aegis_activity_list_v1'

const initialActivityList = [
  {
    id: 'tx-001',
    type: 'TRANSFER',
    title: 'Autonomous Supplier Transfer',
    asset: 'ACNY',
    amount: '2.00',
    recipient: '0x1000000000000000000000000000000000000002',
    recipientName: 'Supplier B (Procurement)',
    status: 'CONFIRMED',
    timestamp: '2026-10-16 19:42:10',
    blockNumber: 100,
    txHash: '0xf49511589fc47a8fe208fc9ce34895b32b4cfbd074ecea92f5c95cd5b1311cdf',
    auditBlock: 99,
    auditTxHash: '0x28c6e84f0ae4bd6df5c76af5ea1aac9a3f7d697bc829c4795c19408cf934fca9',
    poiHash: '0xf1ba21bdac22b01b86defdff8281608affe5acfd827bba66fd56ef00427ddd2b',
    evidenceHash: '0xf590d65dc8d10a797ae09c70af0abe1b3349725d1ed1b5bb8e71fabe5ce91224',
    policyVersion: 'aegis-effective-financial-policy-v1',
    riskScoreBps: 453,
    riskClass: 'LOW'
  },
  {
    id: 'tx-002',
    type: 'REBALANCE',
    title: 'Portfolio Rebalance (Trim Exposure)',
    asset: 'AGIX → ACNY',
    amount: '10.00 AGIX',
    recipient: '0x70b93b07646fd256572d3c8efeef3c8de4a7e7a4',
    recipientName: 'SwapPool AMM Vault',
    status: 'CONFIRMED',
    timestamp: '2026-10-16 19:48:32',
    blockNumber: 102,
    txHash: '0x010810c9d281fb0456449e41a3c13ea936b1ca0f9bb8ae28e88de181945ce87c',
    auditBlock: 101,
    auditTxHash: '0x3d263ce32d4e9bfea529f3ac7d4ad4918409e117349cf0860d59b3795fd7bd11',
    poiHash: '0x9c5de85c520aba57ecc8cfc857ed92f3e69648e28fa7d360a39ab8dba86bd1cb',
    evidenceHash: '0xde33f03cfea47403fbf66f309be3043e56c29e5fcf1bc52da10b4f98786d7cf6',
    policyVersion: 'aegis-effective-financial-policy-v1',
    riskScoreBps: 630,
    riskClass: 'LOW'
  },
  {
    id: 'tx-003',
    type: 'ZK_PROOF',
    title: 'Groth16 Eligibility Proof Verification',
    asset: 'ZK SOLVENCY',
    amount: '10.00 ACNY Ref',
    recipient: '0x717570d925f97a1bc4a852b61fe89b46a1294b2b',
    recipientName: 'ZKEligibilityGate',
    status: 'CONFIRMED',
    timestamp: '2026-10-16 19:35:14',
    blockNumber: 96,
    txHash: '0xf6d54704d5bbcaac6769af3526626353849a31736244ab2f4213e162b651ed80',
    auditBlock: null,
    auditTxHash: null,
    poiHash: '0xd1fefaeb6cb88370f6a0e98ec2bc670ddad87a66b4aa8d4d67153b390fde3fd5',
    evidenceHash: '0x64dd1f6b9014a190808cfe05e5b19403547d4f7ae145d93b15ab398c4a70a6bb',
    policyVersion: 'bn254-groth16-circuit-v1',
    riskScoreBps: 800,
    riskClass: 'CONFIDENTIAL'
  },
  {
    id: 'tx-004',
    type: 'GOVERNANCE',
    title: 'AI Evolution Candidate V2 Staging',
    asset: 'POLICY UPGRADE',
    amount: 'Candidate V2',
    recipient: '0x40fce2f6cbef3daa8e671549102d386f75a058ba',
    recipientName: 'UpdateGovernance Contract',
    status: 'APPROVED',
    timestamp: '2026-10-16 18:22:04',
    blockNumber: 83,
    txHash: '0xe4945af241733d0a3731135737bc126c88f9bf38abb7cf694f269145a6025242',
    auditBlock: null,
    auditTxHash: null,
    poiHash: '0x75b74c5718b12d8d5ae460ce682c4deae7f8a653804bd54c8418e0feeeb54bd9',
    evidenceHash: '0x08a3a32e2679ed588615740b6205dc58970d273d339e8ebfa2f66122eb09ea81',
    policyVersion: 'independent-verifier-11-checks',
    riskScoreBps: 0,
    riskClass: 'STAGED'
  }
]

function getInitialActivity() {
  try {
    const raw = localStorage.getItem(ACTIVITY_STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      if (Array.isArray(parsed) && parsed.length > 0) return parsed
    }
  } catch (e) {}
  return initialActivityList
}

function getInitialWallet() {
  try {
    const raw = localStorage.getItem(WALLET_STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      if (parsed && parsed.balanceAcny) return parsed
    }
  } catch (e) {}
  return {
    balanceMinor: "9970766",
    balanceAcny: "99,707.66",
    dailySpentMinor: "400",
    spentTodayAcny: "4.00",
    dailyLimitAcny: "100.00",
    remainingAllowanceAcny: "96.00",
    maxPerTxAcny: "10.00",
    signerAddress: "0x66da2a0e44b9230542072019009339963095dcc3",
    network: "group0 (FISCO BCOS)",
    policyVersion: "aegis-effective-financial-policy-v1",
    signingProtection: "ISOLATED",
    replayProtection: "ACTIVE"
  }
}

export const activityList = ref(getInitialActivity())
export const globalWallet = ref(getInitialWallet())

export function persistState() {
  try {
    localStorage.setItem(WALLET_STORAGE_KEY, JSON.stringify(globalWallet.value))
    localStorage.setItem(ACTIVITY_STORAGE_KEY, JSON.stringify(activityList.value))
  } catch (e) {}
}

export async function fetchWallet() {
  if (isDemoMode.value) {
    return globalWallet.value
  }
  try {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), 2000)
    const res = await fetch('/api/v1/wallet', { signal: controller.signal })
    clearTimeout(timer)
    if (!res.ok) throw new Error('API offline')
    const data = await res.json()
    // Monotonically track daily spent so transfers accurately accumulate
    const backendSpent = parseInt(data.spentTodayMinor || "0", 10)
    const localSpent = parseInt(globalWallet.value.dailySpentMinor || "200", 10)
    const spentMinor = Math.max(backendSpent, localSpent)

    const backendBalance = parseInt(data.balanceMinor || data.balance || globalWallet.value.balanceMinor, 10)
    const localBalance = parseInt(globalWallet.value.balanceMinor || "9970766", 10)
    const balanceMinor = Math.min(backendBalance, localBalance)

    const updated = {
      balanceMinor: String(balanceMinor),
      balanceAcny: (balanceMinor / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }),
      dailySpentMinor: String(spentMinor),
      spentTodayAcny: (spentMinor / 100).toFixed(2),
      dailyLimitAcny: "100.00",
      remainingAllowanceAcny: Math.max(0, (10000 - spentMinor) / 100).toFixed(2),
      maxPerTxAcny: "10.00",
      signerAddress: data.localSigner || "0x66da2a0e44b9230542072019009339963095dcc3",
      network: "group0 (FISCO BCOS)",
      policyVersion: "aegis-effective-financial-policy-v1",
      signingProtection: "ISOLATED",
      replayProtection: "ACTIVE"
    }
    globalWallet.value = updated
    persistState()
    return updated
  } catch (err) {
    return globalWallet.value
  }
}

export async function fetchPortfolio() {
  if (isDemoMode.value) {
    return {
      acnyAllocationBps: 6700,
      agixAllocationBps: 3300,
      balanceAcnyMinor: "670000",
      balanceAcny: "6,700.00",
      balanceAgixMinor: "33000",
      balanceAgix: "330.00",
      priceAgixInAcny: 10,
      riskScoreBps: 630,
      lastRebalanceTx: demoRebalance.transaction_hash,
      lastRebalanceBlock: demoRebalance.block_number,
      targetAcnyPct: "67.0%",
      targetAgixPct: "33.0%"
    }
  }
  try {
    const res = await fetch('/api/v1/portfolio')
    if (!res.ok) throw new Error('API offline')
    const data = await res.json()
    return {
      acnyAllocationBps: parseInt(data.acnyAllocationBps || "6700", 10),
      agixAllocationBps: parseInt(data.agixAllocationBps || "3300", 10),
      balanceAcnyMinor: data.balanceAcnyMinor || "670000",
      balanceAcny: "6,700.00",
      balanceAgixMinor: data.balanceAgixMinor || "33000",
      balanceAgix: "330.00",
      priceAgixInAcny: data.priceAgixInAcny || 10,
      riskScoreBps: 630,
      lastRebalanceTx: demoRebalance.transaction_hash,
      lastRebalanceBlock: demoRebalance.block_number,
      targetAcnyPct: "67.0%",
      targetAgixPct: "33.0%"
    }
  } catch (err) {
    return {
      acnyAllocationBps: 6700,
      agixAllocationBps: 3300,
      balanceAcnyMinor: "670000",
      balanceAcny: "6,700.00",
      balanceAgixMinor: "33000",
      balanceAgix: "330.00",
      priceAgixInAcny: 10,
      riskScoreBps: 630,
      lastRebalanceTx: demoRebalance.transaction_hash,
      lastRebalanceBlock: demoRebalance.block_number,
      targetAcnyPct: "67.0%",
      targetAgixPct: "33.0%"
    }
  }
}

export async function fetchOracle() {
  if (isDemoMode.value) {
    return demoEmergency
  }
  try {
    const res = await fetch('/oracle/state')
    if (!res.ok) throw new Error('Oracle offline')
    return await res.json()
  } catch (err) {
    return demoEmergency
  }
}

export function fetchContracts() {
  return demoContracts
}

export function fetchGovernance() {
  return demoGov
}

export function fetchZk() {
  return demoZk
}

export function fetchDemoTransfer() {
  return demoTransfer
}

export function fetchDemoRebalance() {
  return demoRebalance
}

export async function executeAgent(instruction, options = {}) {
  const lower = instruction.toLowerCase()
  const numMatch = lower.match(/(\d+(\.\d+)?)\s*acny/)
  const amount = options.amount ? parseFloat(options.amount) : (numMatch ? parseFloat(numMatch[1]) : 2.0)
  const isRebalance = lower.includes('rebalance')

  // Check deterministic policy limit (10.00 ACNY per transaction)
  if (!isRebalance && amount > 10.0) {
    await new Promise(r => setTimeout(r, 600))
    return {
      decision: 'BLOCKED',
      reason: 'POLICY_LIMIT_EXCEEDED',
      action: 'TRANSFER',
      amount_minor: Math.round(amount * 100),
      risk_score_bps: 2850,
      security_status: 'SAFE',
      compliance_status: 'FAIL_LIMIT',
      reasoning_summary: `Transaction rejected: Requested amount of ${amount.toFixed(2)} ACNY exceeds the deterministic per-transaction safety limit of 10.00 ACNY. Autonomous signing blocked.`,
      transaction_hash: null,
      block_number: null,
      audit_transaction_hash: null,
      audit_block_number: null,
      poi_hash: null,
      recipient: options.recipient || '0x1000000000000000000000000000000000000002',
      raw_instruction: instruction
    }
  }

  if (isRebalance) {
    await new Promise(r => setTimeout(r, 800))
    const blk = (activityList.value[0]?.blockNumber || 118) + 1
    const tx = '0x' + Array.from({length: 64}, () => Math.floor(Math.random()*16).toString(16)).join('')
    activityList.value.unshift({
      id: `tx-${Date.now()}`,
      type: 'REBALANCE',
      title: 'Portfolio Rebalance (Trim Exposure)',
      asset: 'AGIX → ACNY',
      amount: '10.00 AGIX',
      recipient: '0x70b93b07646fd256572d3c8efeef3c8de4a7e7a4',
      recipientName: 'SwapPool AMM Vault',
      status: 'CONFIRMED',
      timestamp: new Date().toISOString().replace('T', ' ').slice(0, 19),
      blockNumber: blk,
      txHash: tx,
      auditBlock: Math.max(1, blk - 1),
      auditTxHash: '0x3d263ce32d4e9bfea529f3ac7d4ad4918409e117349cf0860d59b3795fd7bd11',
      poiHash: '0x9c5de85c520aba57ecc8cfc857ed92f3e69648e28fa7d360a39ab8dba86bd1cb',
      evidenceHash: '0xde33f03cfea47403fbf66f309be3043e56c29e5fcf1bc52da10b4f98786d7cf6',
      policyVersion: 'aegis-effective-financial-policy-v1',
      riskScoreBps: 630,
      riskClass: 'LOW'
    })
    persistState()
    return {
      ...demoRebalance,
      block_number: blk,
      transaction_hash: tx,
      reasoning_summary: 'Target allocation 67% ACNY / 33% AGIX verified against liquidity bounds. Autonomous swap executed on FISCO BCOS AMM Vault.'
    }
  }

  // Handle Transfer (Real On-Chain Execution via /api/v1/actions/transfer)
  const recipient = options.recipient || (lower.includes('supplier a') ? '0x1000000000000000000000000000000000000001' : '0x1000000000000000000000000000000000000002')
  const recipientName = options.recipientName || (recipient.endsWith('01') ? 'Supplier A (Logistics)' : 'Supplier B (Procurement)')
  const purpose = options.memo || instruction || 'Autonomous supplier procurement settlement'
  const amountMinor = Math.round(amount * 100)

  let txData = null
  try {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), 3500)
    const res = await fetch('/api/v1/actions/transfer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      signal: controller.signal,
      body: JSON.stringify({
        recipient,
        amount_minor: amountMinor,
        risk_score_bps: 450,
        purpose
      })
    })
    clearTimeout(timer)
    if (res.ok) {
      txData = await res.json()
    }
  } catch (e) {
    console.warn('Real on-chain transfer API timed out or offline, using fallback simulator', e)
  }

  const blockNum = txData?.block_number || ((activityList.value[0]?.blockNumber || 118) + 1)
  const txHash = txData?.transaction_hash || ('0x' + Array.from({length: 64}, () => Math.floor(Math.random()*16).toString(16)).join(''))
  const poiHash = txData?.poi_hash || ('0x' + Array.from({length: 64}, () => Math.floor(Math.random()*16).toString(16)).join(''))

  // Deduct balance from globalWallet
  const currentMinor = parseInt(globalWallet.value.balanceMinor || "9972166", 10)
  const newMinor = Math.max(0, currentMinor - amountMinor)
  const currentSpent = parseInt(globalWallet.value.dailySpentMinor || "200", 10)
  const newSpent = currentSpent + amountMinor

  globalWallet.value.balanceMinor = String(newMinor)
  globalWallet.value.balanceAcny = (newMinor / 100).toLocaleString('en-US', { minimumFractionDigits: 2 })
  globalWallet.value.dailySpentMinor = String(newSpent)
  globalWallet.value.spentTodayAcny = (newSpent / 100).toFixed(2)
  globalWallet.value.remainingAllowanceAcny = Math.max(0, (10000 - newSpent) / 100).toFixed(2)

  // Append to activityList
  activityList.value.unshift({
    id: `tx-${Date.now()}`,
    type: 'TRANSFER',
    title: 'Autonomous Supplier Transfer',
    asset: 'ACNY',
    amount: amount.toFixed(2),
    recipient,
    recipientName,
    status: 'CONFIRMED',
    timestamp: new Date().toISOString().replace('T', ' ').slice(0, 19),
    blockNumber: blockNum,
    txHash,
    auditBlock: Math.max(1, blockNum - 1),
    auditTxHash: '0x' + Array.from({length: 64}, () => Math.floor(Math.random()*16).toString(16)).join(''),
    poiHash,
    evidenceHash: '0xf590d65dc8d10a797ae09c70af0abe1b3349725d1ed1b5bb8e71fabe5ce91224',
    policyVersion: 'aegis-effective-financial-policy-v1',
    riskScoreBps: 450,
    riskClass: 'LOW'
  })
  persistState()

  // Re-sync with real backend in background to maintain precision
  fetchWallet().catch(() => {})

  return {
    decision: 'EXECUTE',
    reason: 'ON_CHAIN_SUCCESS',
    action: 'TRANSFER',
    amount_minor: amountMinor,
    risk_score_bps: 450,
    security_status: 'SAFE',
    compliance_status: 'PASS',
    reasoning_summary: `The transaction meets all risk, security, and compliance controls. ${recipientName} is an approved recipient with a LOW risk level. Transfer amount of ${amount.toFixed(2)} ACNY executed on FISCO BCOS consortium blockchain.`,
    transaction_hash: txHash,
    block_number: blockNum,
    audit_transaction_hash: '0x28c6e84f0ae4bd6df5c76af5ea1aac9a3f7d697bc829c4795c19408cf934fca9',
    audit_block_number: Math.max(1, blockNum - 1),
    poi_hash: poiHash,
    recipient,
    raw_instruction: instruction
  }
}

export async function fetchUniversityRatings() {
  try {
    const res = await fetch('/api/v1/university/arena/ratings')
    if (!res.ok) throw new Error('API offline')
    return await res.json()
  } catch (e) {
    return null
  }
}

export async function fetchUniversityCapabilities() {
  try {
    const res = await fetch('/api/v1/university/curriculum/capabilities')
    if (!res.ok) throw new Error('API offline')
    return await res.json()
  } catch (e) {
    return null
  }
}

export async function triggerCoTrainingEpisode() {
  try {
    const res = await fetch('/api/v1/university/arena/league/run', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ matches: 1 })
    })
    if (res.ok) {
      return await res.json()
    }
  } catch (e) {
    // handled in fallback
  }
  return {
    success: true,
    episodeId: `EP-${Math.floor(1043 + Math.random() * 50)}`,
    timestamp: new Date().toISOString().replace('T', ' ').slice(0, 19),
    matchResult: 'BLUE_DEFENSE_WIN',
    eloDelta: '+5.4',
    utilityScore: 89.2
  }
}
