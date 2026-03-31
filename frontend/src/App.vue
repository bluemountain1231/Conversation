<template>
  <div id="app-container">
    <NavBar v-if="!isAdminRoute" />
    <main :class="isAdminRoute ? '' : 'main-content'">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'
import { useUserStore } from './stores/user'
import { useNotificationStore } from './stores/notification'
import { connectWebSocket, disconnectWebSocket } from './utils/websocket'

const route = useRoute()
const userStore = useUserStore()
const notifStore = useNotificationStore()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const showBackTop = ref(false)

function handleScroll() {
  showBackTop.value = window.scrollY > 400
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(() => userStore.userInfo, (user) => {
  if (user) {
    notifStore.fetchUnreadCount()
    connectWebSocket(user.id, (n) => notifStore.addNotification(n))
  } else {
    disconnectWebSocket()
  }
}, { immediate: true })

onMounted(() => {
  userStore.loadUser()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

:root {
  --primary: #4f46e5;
  --primary-light: #818cf8;
  --primary-bg: #eef2ff;
  --accent: #06b6d4;
  --gradient-primary: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #a855f7 100%);
  --gradient-warm: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
  --gradient-cool: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
  --text-primary: #0f172a;
  --text-secondary: #475569;
  --text-muted: #94a3b8;
  --bg-page: #f1f5f9;
  --bg-card: #ffffff;
  --bg-hover: #f8fafc;
  --border-light: #e2e8f0;
  --border-soft: rgba(0,0,0,0.04);
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.04), 0 1px 2px rgba(0,0,0,0.02);
  --shadow-md: 0 4px 16px rgba(0,0,0,0.06), 0 2px 4px rgba(0,0,0,0.03);
  --shadow-lg: 0 12px 40px rgba(0,0,0,0.08), 0 4px 12px rgba(0,0,0,0.04);
  --shadow-xl: 0 20px 60px rgba(0,0,0,0.1), 0 8px 20px rgba(0,0,0,0.06);
  --shadow-primary: 0 8px 24px rgba(79,70,229,0.25);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  background-color: var(--bg-page);
  background-image:
    radial-gradient(at 20% 0%, rgba(79,70,229,0.04) 0%, transparent 50%),
    radial-gradient(at 80% 100%, rgba(6,182,212,0.04) 0%, transparent 50%);
  background-attachment: fixed;
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.6;
}

#app-container { min-height: 100vh; }
.main-content { max-width: 1080px; margin: 0 auto; padding: 84px 24px 60px; }
a { text-decoration: none; color: inherit; transition: color 0.2s; }

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

.page-enter-active { transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1); }
.page-leave-active { transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); }
.page-enter-from { opacity: 0; transform: translateY(16px); }
.page-leave-to { opacity: 0; transform: translateY(-8px); }

.el-card {
  border: 1px solid var(--border-soft) !important;
  box-shadow: var(--shadow-sm) !important;
  transition: box-shadow 0.3s, transform 0.3s !important;
}
.el-card:hover {
  box-shadow: var(--shadow-md) !important;
}

.el-button--primary {
  background: var(--gradient-primary) !important;
  border: none !important;
  box-shadow: var(--shadow-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}
.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 32px rgba(79,70,229,0.35) !important;
  filter: brightness(1.05);
}
.el-button--primary:active {
  transform: translateY(0);
}

.el-button {
  border-radius: var(--radius-sm) !important;
  font-weight: 500 !important;
  transition: all 0.25s !important;
}

.el-input__wrapper {
  border-radius: var(--radius-sm) !important;
  transition: box-shadow 0.25s !important;
}

.el-tabs__item {
  font-weight: 600 !important;
  transition: color 0.25s !important;
}
.el-tabs__active-bar {
  background: var(--gradient-primary) !important;
  border-radius: 2px !important;
  height: 3px !important;
}

.el-tag {
  border-radius: 6px !important;
  font-weight: 500 !important;
}

.el-avatar {
  border: 2px solid rgba(255,255,255,0.8) !important;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08) !important;
}

.el-dialog {
  border-radius: var(--radius-lg) !important;
}

/* Back to Top */
.back-to-top {
  position: fixed; bottom: 32px; right: 32px; z-index: 999;
  width: 44px; height: 44px; border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff; border: none; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  box-shadow: var(--shadow-primary);
  transition: all 0.3s;
}
.back-to-top:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(79,70,229,0.4);
}

.fade-up-enter-active { transition: all 0.3s ease; }
.fade-up-leave-active { transition: all 0.2s ease; }
.fade-up-enter-from { opacity: 0; transform: translateY(12px); }
.fade-up-leave-to { opacity: 0; transform: translateY(12px); }
</style>
