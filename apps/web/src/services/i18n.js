import { ref } from 'vue'

export const currentLang = ref('en')

export function toggleLang() {
  currentLang.value = 'en'
}

export const t = (key) => {
  const dict = translations.en
  return dict[key] || key
}

export const translations = {
  en: {
    // Topbar
    brandSub: 'Autonomous Financial Intelligence • FISCO BCOS Consortium',
    liveMode: 'LIVE REST API',
    demoMode: 'DEMO EVIDENCE',
    systemStatus: 'SYSTEM: OPERATIONAL',
    demoBanner: 'DEMO EVIDENCE MODE ACTIVE: Serving authentic Phase-I on-chain cryptographic audit records and verified smart contract logs.',
    
    // Sidebar
    navTitle: 'COMMAND NAVIGATION',
    navOverview: 'Overview',
    navWallet: 'Agent Wallet',
    navPortfolio: 'Portfolio',
    navActivity: 'Activity & Proof',
    navSecurity: 'Security & Compliance',
    navEmergency: 'Emergency Control',
    navTrustCenter: 'Trust Center',
    consensusLbl: 'CONSENSUS:',
    chainIdLbl: 'CHAIN ID:',
    signingLbl: 'SIGNING:',
    isolatedLbl: 'ISOLATED',

    // Overview
    overviewTitle: 'Financial Operations Overview',
    overviewSub: 'Autonomous treasury management, verifiable compliance controls, and deterministic execution on FISCO BCOS.',
    tagProtected: 'PROTECTED SIGNING BOUNDARY',
    walletBalanceLbl: 'WALLET BALANCE',
    walletBalanceSub: 'Simulated Consortium Reserve (ACNY)',
    portfolioAllocLbl: 'PORTFOLIO ALLOCATION',
    portfolioAllocSub: 'Target 67/33 • Balanced Allocation',
    safetyStatusLbl: 'SAFETY STATUS',
    safetyStatusSub: '100% Policy Invariants Enforced',
    networkHealthLbl: 'NETWORK HEALTH',
    networkHealthSub: 'Consortium PBFT Consensus Active',
    engineTitle: 'ASK AEGIS — AUTONOMOUS EXECUTION ENGINE',
    viewTrace: 'View Technical Trace',
    quickActionsLbl: 'Quick Actions:',
    btnExecute: 'Execute Intent',
    pipelineTitle: 'EXECUTION PIPELINE',
    step1Name: '1. Understanding Request',
    step1Desc: 'Intent parsed & locked',
    step2Name: '2. Safety & Compliance',
    step2Desc: 'Regulatory policy verified',
    step3Name: '3. Financial Decision',
    step3Desc: 'Leader authorized',
    step4Name: '4. Proof Secured',
    step4Desc: 'PoI & Audit committed',
    step5Name: '5. Transaction Completed',
    step5Desc: 'On-chain finality',
    recentActivityTitle: 'RECENT FINANCIAL ACTIVITY',
    viewAllTx: 'View All Transactions'
  }
}

