<template>
  <header class="navbar" ref="navRef">
    <div class="navbar-inner">
      <router-link to="/" class="logo">
        <div class="logo-icon">IC</div>
        <span class="logo-text">兴趣圈子</span>
      </router-link>

      <div class="nav-center">
        <div class="search-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input v-model="searchKeyword" placeholder="搜索帖子、圈子、用户..." class="search-input" @keyup.enter="handleSearch" />
        </div>
      </div>

      <nav class="nav-actions">
        <router-link to="/circles" class="nav-link"><el-icon><Compass /></el-icon><span>圈子</span></router-link>
        <template v-if="userStore.isLoggedIn">
          <router-link to="/chat" class="nav-link"><el-icon><ChatLineSquare /></el-icon><span>私信</span></router-link>
          <NotificationBell />
          <el-button type="primary" round size="default" @click="$router.push('/post/create')" class="publish-btn">
            <el-icon><Edit /></el-icon>发帖
          </el-button>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-trigger">
              <el-avatar :size="34" :src="userStore.userInfo?.avatar" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <div class="dropdown-user-info">
                  <el-avatar :size="40" :src="userStore.userInfo?.avatar" />
                  <div><div class="dui-name">{{ userStore.userInfo?.username }}</div><div class="dui-email">{{ userStore.userInfo?.email }}</div></div>
                </div>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人主页</el-dropdown-item>
                <el-dropdown-item v-if="userStore.userInfo?.role === 'ADMIN'" command="admin"><el-icon><Setting /></el-icon>管理后台</el-dropdown-item>
                <el-dropdown-item command="logout" divided><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="$router.push('/login')" round>登录</el-button>
          <el-button type="primary" @click="$router.push('/register')" round>注册</el-button>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import NotificationBell from './NotificationBell.vue'
import { gsap } from '../utils/animations'

const userStore = useUserStore()
const router = useRouter()
const searchKeyword = ref('')
const navRef = ref(null)

function handleSearch() {
  if (!searchKeyword.value.trim()) return
  router.push({ path: '/search', query: { q: searchKeyword.value } })
}

function handleCommand(cmd) {
  if (cmd === 'profile') router.push(`/profile/${userStore.userInfo?.id}`)
  else if (cmd === 'admin') router.push('/admin')
  else if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    }).then(() => { userStore.logout(); router.push('/') }).catch(() => {})
  }
}

onMounted(() => {
  if (navRef.value) gsap.fromTo(navRef.value, { y: -60 }, { y: 0, duration: 0.5, ease: 'power3.out' })
})
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(20px) saturate(200%);
  -webkit-backdrop-filter: blur(20px) saturate(200%);
  border-bottom: 1px solid rgba(255,255,255,0.3);
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}
.navbar-inner {
  max-width: 1120px; margin: 0 auto; padding: 0 24px;
  height: 64px; display: flex; align-items: center; justify-content: space-between; gap: 24px;
}

.logo { display: flex; align-items: center; gap: 10px; flex-shrink: 0; transition: transform 0.2s; }
.logo:hover { transform: scale(1.02); }
.logo-icon {
  width: 36px; height: 36px; border-radius: 11px;
  background: var(--gradient-primary);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-weight: 800; font-size: 13px; letter-spacing: -0.5px;
  box-shadow: 0 4px 12px rgba(79,70,229,0.3);
}
.logo-text {
  font-size: 18px; font-weight: 800;
  background: var(--gradient-primary);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-center { flex: 1; max-width: 420px; }
.search-wrapper { position: relative; }
.search-icon { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); color: #94a3b8; font-size: 16px; transition: color 0.25s; }
.search-input {
  width: 100%; height: 40px;
  border: 1.5px solid var(--border-light); border-radius: 12px;
  padding: 0 16px 0 40px; font-size: 14px; color: var(--text-primary);
  background: var(--bg-hover); outline: none; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.search-input:focus {
  border-color: var(--primary);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(79,70,229,0.1), 0 2px 8px rgba(79,70,229,0.08);
}
.search-input:focus + .search-icon,
.search-wrapper:focus-within .search-icon { color: var(--primary); }
.search-input::placeholder { color: #b0b8c9; }

.nav-actions { display: flex; align-items: center; gap: 4px; flex-shrink: 0; }
.nav-link {
  display: flex; align-items: center; gap: 5px;
  padding: 7px 14px; border-radius: 10px;
  font-size: 13px; font-weight: 600; color: var(--text-secondary);
  transition: all 0.25s; position: relative;
}
.nav-link:hover { background: var(--primary-bg); color: var(--primary); }
.publish-btn {
  font-weight: 600; padding: 8px 18px !important;
  border-radius: 10px !important;
}

.user-trigger {
  cursor: pointer; padding: 3px; border-radius: 50%;
  transition: all 0.25s;
}
.user-trigger:hover {
  box-shadow: 0 0 0 3px rgba(79,70,229,0.15);
  transform: scale(1.05);
}

.dropdown-user-info {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-light); margin-bottom: 4px;
  background: linear-gradient(135deg, rgba(79,70,229,0.04), rgba(168,85,247,0.04));
}
.dui-name { font-weight: 700; font-size: 14px; color: var(--text-primary); }
.dui-email { font-size: 12px; color: var(--text-muted); }
</style>
