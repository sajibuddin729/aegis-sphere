import { createRouter, createWebHistory } from 'vue-router'
import OverviewView from '../views/OverviewView.vue'
import WalletView from '../views/WalletView.vue'
import PortfolioView from '../views/PortfolioView.vue'
import ActivityView from '../views/ActivityView.vue'
import RiskComplianceView from '../views/RiskComplianceView.vue'
import EmergencyView from '../views/EmergencyView.vue'
import TrustCenterView from '../views/TrustCenterView.vue'

const routes = [
  { path: '/', redirect: '/overview' },
  { path: '/overview', name: 'Overview', component: OverviewView },
  { path: '/wallet', name: 'Wallet', component: WalletView },
  { path: '/portfolio', name: 'Portfolio', component: PortfolioView },
  { path: '/activity', name: 'Activity', component: ActivityView },
  { path: '/security', name: 'Security', component: RiskComplianceView },
  { path: '/emergency', name: 'Emergency', component: EmergencyView },
  { path: '/trust-center', name: 'TrustCenter', component: TrustCenterView },

  // Compatibility Redirects to Trust Center Tabs
  { path: '/compliance', redirect: '/security' },
  { path: '/blockchain', redirect: '/trust-center?tab=blockchain' },
  { path: '/poi', redirect: '/trust-center?tab=poi' },
  { path: '/governance', redirect: '/trust-center?tab=governance' },
  { path: '/zk', redirect: '/trust-center?tab=zk' },
  { path: '/university', redirect: '/trust-center?tab=university' },
  { path: '/health', redirect: '/trust-center?tab=health' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
