<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
    </div>
    <el-card class="auth-card" shadow="never">
      <div class="auth-header">
        <div class="auth-logo">IC</div>
        <h2>加入兴趣圈子</h2>
        <p>发现社区，分享你的热爱</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" prefix-icon="Message" size="large" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号(选填)" prefix-icon="Phone" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码(至少6位)" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;height:44px;font-size:15px;border-radius:12px" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        已有账号？<router-link to="/login" class="link">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', email: '', phone: '', password: '' })

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于6个字符', trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await register(form)
    userStore.setToken(res.data.token)
    userStore.setUser(res.data.user)
    ElMessage.success('注册成功')
    router.push('/')
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
.bg-orb-1 {
  width: 400px; height: 400px; top: -100px; left: -50px;
  background: rgba(168,85,247,0.12);
}
.bg-orb-2 {
  width: 350px; height: 350px; bottom: -80px; right: -60px;
  background: rgba(6,182,212,0.10);
  animation-delay: -7s;
}
.bg-orb-3 {
  width: 250px; height: 250px; top: 50%; right: 30%;
  background: rgba(79,70,229,0.08);
  animation-delay: -14s;
}
@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 15px) scale(0.95); }
}

.auth-card {
  width: 440px;
  border-radius: var(--radius-xl) !important;
  padding: 28px 24px !important;
  position: relative; z-index: 1;
  background: rgba(255,255,255,0.85) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5) !important;
  box-shadow: var(--shadow-xl) !important;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  width: 56px; height: 56px; border-radius: 16px;
  background: var(--gradient-primary);
  color: #fff; font-weight: 800; font-size: 20px;
  display: inline-flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 24px rgba(79,70,229,0.3);
  margin-bottom: 16px;
}

.auth-header h2 {
  margin: 0 0 6px;
  font-size: 24px; font-weight: 800;
  color: var(--text-primary);
}

.auth-header p {
  color: var(--text-muted);
  font-size: 14px;
}

.auth-footer {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
  margin-top: 4px;
}

.link {
  color: var(--primary);
  font-weight: 600;
  transition: color 0.2s;
}
.link:hover { color: var(--primary-light); }
</style>
