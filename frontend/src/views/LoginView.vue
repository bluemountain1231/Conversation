<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
    </div>
    <div class="auth-container">
      <!-- Left: Feature Showcase -->
      <div class="auth-showcase">
        <div class="showcase-content">
          <div class="showcase-logo">IC</div>
          <h1>兴趣圈子</h1>
          <p class="showcase-desc">在这里发现你的热爱，连接志同道合的人</p>
          <div class="showcase-features">
            <div class="feature-item">
              <div class="feature-icon"><el-icon><Compass /></el-icon></div>
              <div>
                <div class="feature-title">发现圈子</div>
                <div class="feature-desc">探索各种兴趣社区，找到属于你的圈子</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><el-icon><ChatLineSquare /></el-icon></div>
              <div>
                <div class="feature-title">实时互动</div>
                <div class="feature-desc">发帖评论点赞收藏，与朋友实时私信</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><el-icon><UserFilled /></el-icon></div>
              <div>
                <div class="feature-title">关注创作者</div>
                <div class="feature-desc">关注感兴趣的人，获取个性化内容推荐</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Login Form -->
      <div class="auth-form-side">
        <el-card class="auth-card" shadow="never">
          <div class="auth-header">
            <h2>欢迎回来</h2>
            <p>登录你的账号继续探索</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" prefix-icon="Message" size="large" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe" label="记住登录状态" />
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>

            <el-form-item>
              <el-button type="primary" size="large" style="width:100%;height:46px;font-size:15px;border-radius:12px" @click="handleLogin" :loading="loading">
                登 录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="auth-divider">
            <span>或者</span>
          </div>

          <div class="social-login">
            <button class="social-btn" title="微信登录">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-7.062-6.122zm-2.18 2.769c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.825 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982z"/></svg>
            </button>
            <button class="social-btn" title="GitHub 登录">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/></svg>
            </button>
            <button class="social-btn" title="Google 登录">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" fill="#4285F4"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/></svg>
            </button>
          </div>

          <div class="auth-footer">
            还没有账号？<router-link to="/register" class="link">立即注册</router-link>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '../api/auth'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({ email: '', password: '' })

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于6个字符', trigger: 'blur' }
  ]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.data.token)
    userStore.setUser(res.data.user)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch {} finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  pointer-events: none; z-index: 0;
}
.bg-orb {
  position: absolute; border-radius: 50%;
  filter: blur(80px); opacity: 0.5;
  animation: orbFloat 20s infinite ease-in-out;
}
.bg-orb-1 { width: 400px; height: 400px; top: -100px; right: -50px; background: rgba(79,70,229,0.12); }
.bg-orb-2 { width: 350px; height: 350px; bottom: -80px; left: -60px; background: rgba(6,182,212,0.10); animation-delay: -7s; }
.bg-orb-3 { width: 250px; height: 250px; top: 40%; left: 60%; background: rgba(168,85,247,0.08); animation-delay: -14s; }
@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 15px) scale(0.95); }
}

.auth-container {
  display: flex; position: relative; z-index: 1;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  max-width: 900px; width: 100%;
  background: var(--bg-card);
}

/* Showcase */
.auth-showcase {
  width: 400px; flex-shrink: 0;
  background: var(--gradient-primary);
  padding: 48px 36px;
  display: flex; flex-direction: column; justify-content: center;
  position: relative; overflow: hidden;
}
.auth-showcase::before {
  content: '';
  position: absolute; top: -50px; right: -50px;
  width: 200px; height: 200px; border-radius: 50%;
  background: rgba(255,255,255,0.1);
}
.auth-showcase::after {
  content: '';
  position: absolute; bottom: -80px; left: -40px;
  width: 250px; height: 250px; border-radius: 50%;
  background: rgba(255,255,255,0.06);
}
.showcase-content { position: relative; z-index: 1; }
.showcase-logo {
  width: 48px; height: 48px; border-radius: 14px;
  background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);
  color: #fff; font-weight: 800; font-size: 18px;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 20px;
}
.showcase-content h1 { font-size: 28px; font-weight: 800; color: #fff; margin-bottom: 8px; }
.showcase-desc { color: rgba(255,255,255,0.8); font-size: 14px; line-height: 1.6; margin-bottom: 32px; }

.showcase-features { display: flex; flex-direction: column; gap: 20px; }
.feature-item {
  display: flex; gap: 14px; align-items: flex-start;
  padding: 14px; border-radius: var(--radius-md);
  background: rgba(255,255,255,0.08); backdrop-filter: blur(4px);
  transition: background 0.3s;
}
.feature-item:hover { background: rgba(255,255,255,0.14); }
.feature-icon {
  width: 36px; height: 36px; border-radius: 10px;
  background: rgba(255,255,255,0.15);
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 18px; flex-shrink: 0;
}
.feature-title { font-size: 14px; font-weight: 700; color: #fff; margin-bottom: 3px; }
.feature-desc { font-size: 12px; color: rgba(255,255,255,0.7); line-height: 1.4; }

/* Form Side */
.auth-form-side {
  flex: 1; display: flex; align-items: center; justify-content: center;
  padding: 24px;
}
.auth-card {
  width: 100%; max-width: 380px;
  border: none !important; box-shadow: none !important;
  background: transparent !important;
}

.auth-header {
  margin-bottom: 28px;
}
.auth-header h2 {
  font-size: 24px; font-weight: 800;
  color: var(--text-primary); margin-bottom: 6px;
}
.auth-header p { color: var(--text-muted); font-size: 14px; }

.form-options {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px;
}
.forgot-link { font-size: 13px; color: var(--primary); font-weight: 500; text-decoration: none; }
.forgot-link:hover { text-decoration: underline; }

.auth-divider {
  display: flex; align-items: center; gap: 16px;
  margin: 20px 0;
}
.auth-divider::before, .auth-divider::after {
  content: ''; flex: 1; height: 1px; background: var(--border-light);
}
.auth-divider span { font-size: 12px; color: var(--text-muted); font-weight: 500; }

.social-login {
  display: flex; justify-content: center; gap: 16px;
  margin-bottom: 20px;
}
.social-btn {
  width: 48px; height: 48px; border-radius: 12px;
  border: 1.5px solid var(--border-light);
  background: var(--bg-card); cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.25s; color: var(--text-secondary);
}
.social-btn:hover {
  border-color: var(--primary); background: var(--primary-bg);
  transform: translateY(-2px); box-shadow: var(--shadow-md);
}

.auth-footer {
  text-align: center; font-size: 14px; color: var(--text-muted);
}
.link { color: var(--primary); font-weight: 600; transition: color 0.2s; }
.link:hover { color: var(--primary-light); }

@media (max-width: 768px) {
  .auth-showcase { display: none; }
  .auth-container { max-width: 440px; }
}
</style>
