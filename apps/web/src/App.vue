<template>
  <div class="layout">
    <!-- Institutional Top Navigation Bar -->
    <header class="topbar">
      <div class="topbar-left">
        <div class="brand">
          <span class="brand-title">AEGIS-SPHERE</span>
          <span class="brand-sub">{{ t('brandSub') }}</span>
        </div>
      </div>

      <div class="topbar-right">
        <!-- Live vs Demo Mode Toggle -->
        <button 
          class="mode-toggle" 
          :class="{ 'demo-active': isDemoMode }"
          @click="toggleDemoMode"
          title="Toggle between Live REST API Mode and Phase-I Public Evidence Mode"
        >
          <span class="mode-dot"></span>
          {{ isDemoMode ? t('demoMode') : t('liveMode') }}
        </button>

        <div class="system-status">
          <span class="status-dot"></span>
          <span class="status-text">{{ t('systemStatus') }}</span>
        </div>
      </div>
    </header>

    <!-- Demo Evidence Notice Banner -->
    <div v-if="isDemoMode" class="demo-banner">
      <strong>{{ t('demoBanner') }}</strong>
    </div>

    <!-- Main Workspace -->
    <div class="workspace">
      <!-- Institutional Sidebar (7 Items) -->
      <aside class="sidebar">
        <div class="sidebar-section-title">{{ t('navTitle') }}</div>
        <nav class="nav-menu">
          <router-link to="/overview" class="nav-item">
            <LayoutDashboard class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navOverview') }}</span>
            <span class="nav-badge">01</span>
          </router-link>

          <router-link to="/wallet" class="nav-item">
            <Wallet class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navWallet') }}</span>
            <span class="nav-badge">02</span>
          </router-link>

          <router-link to="/portfolio" class="nav-item">
            <PieChart class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navPortfolio') }}</span>
            <span class="nav-badge">03</span>
          </router-link>

          <router-link to="/activity" class="nav-item">
            <History class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navActivity') }}</span>
            <span class="nav-badge">04</span>
          </router-link>

          <router-link to="/security" class="nav-item">
            <ShieldCheck class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navSecurity') }}</span>
            <span class="nav-badge">05</span>
          </router-link>

          <router-link to="/emergency" class="nav-item">
            <AlertOctagon class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navEmergency') }}</span>
            <span class="nav-badge">06</span>
          </router-link>

          <router-link to="/trust-center" class="nav-item">
            <Lock class="nav-icon" :size="16" />
            <span class="nav-text">{{ t('navTrustCenter') }}</span>
            <span class="nav-badge">07</span>
          </router-link>
        </nav>

        <div class="sidebar-footer">
          <div class="footer-row">
            <span>{{ t('consensusLbl') }}</span>
            <strong>PBFT (f=1)</strong>
          </div>
          <div class="footer-row">
            <span>{{ t('chainIdLbl') }}</span>
            <strong class="mono">chain0 / group0</strong>
          </div>
          <div class="footer-row">
            <span>{{ t('signingLbl') }}</span>
            <strong class="text-green">{{ t('isolatedLbl') }}</strong>
          </div>
        </div>
      </aside>

      <!-- Main Content Area -->
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { 
  LayoutDashboard, 
  Wallet, 
  PieChart, 
  History, 
  ShieldCheck, 
  AlertOctagon, 
  Lock
} from 'lucide-vue-next'
import { isDemoMode, toggleDemoMode } from './services/api.js'
import { t } from './services/i18n.js'
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--bg-base);
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 52px;
  padding: 0 24px;
  background-color: var(--bg-sidebar);
  border-bottom: 1px solid var(--border);
  z-index: 50;
}

.topbar-left {
  display: flex;
  align-items: center;
}

.brand {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.brand-title {
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 1.2px;
  color: #09090b;
}

.brand-sub {
  font-size: 11px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 4px;
  color: #09090b;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.3px;
}

.lang-toggle:hover {
  background: #f4f4f5;
  border-color: #09090b;
}

.mode-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 12px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 4px;
  color: var(--text-main);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.4px;
}

.mode-toggle:hover {
  background: #f8fafc;
  border-color: #09090b;
  color: #09090b;
}

.mode-toggle.demo-active {
  background: #fffbeb;
  border-color: #fde68a;
  color: #b45309;
}

.mode-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background-color: #09090b;
}

.mode-toggle.demo-active .mode-dot {
  background-color: #b45309;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 11px;
  font-weight: 700;
  color: #166534;
  letter-spacing: 0.5px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background-color: #166534;
}

.demo-banner {
  background: #fffbeb;
  border-bottom: 1px solid #fde68a;
  padding: 6px 24px;
  font-size: 11px;
  font-weight: 600;
  color: #b45309;
  text-align: center;
}

.workspace {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 230px;
  background-color: #ffffff;
  border-right: 1px solid var(--border);
  padding: 16px 0 20px 0;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-section-title {
  padding: 0 18px 8px 18px;
  font-size: 10px;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.8px;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 0 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 9px 12px;
  color: #475569;
  text-decoration: none;
  font-size: 12.5px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.12s ease;
}

.nav-item:hover {
  background-color: #f4f4f5;
  color: #09090b;
}

.nav-item.router-link-active {
  background-color: #09090b;
  color: #ffffff;
  font-weight: 600;
}

.nav-icon {
  color: #64748b;
  flex-shrink: 0;
}

.nav-item.router-link-active .nav-icon {
  color: #ffffff;
}

.nav-text {
  flex: 1;
}

.nav-badge {
  font-family: var(--font-mono);
  font-size: 10px;
  color: #94a3b8;
}

.nav-item.router-link-active .nav-badge {
  color: #a1a1aa;
}

.sidebar-footer {
  margin: auto 12px 0 12px;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 10px;
}

.footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-row span {
  color: #64748b;
}

.footer-row strong {
  color: #09090b;
  font-weight: 600;
}

.text-green {
  color: #166534 !important;
  font-weight: 700;
}

.content-area {
  flex: 1;
  padding: 24px 32px;
  max-width: 1440px;
  overflow-y: auto;
}
</style>
