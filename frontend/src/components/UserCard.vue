<template>
  <div class="user-card" @click="$router.push(`/profile/${user.id}`)">
    <el-avatar :size="42" :src="user.avatar" />
    <div class="uc-info">
      <div class="uc-name">{{ user.username }}</div>
      <div class="uc-meta">{{ user.postCount }} 帖子 · {{ user.followerCount }} 粉丝</div>
    </div>
    <el-button v-if="showFollow" :type="user.followed ? 'default' : 'primary'" size="small" round
      @click.stop="handleFollow" :loading="loading">
      {{ user.followed ? '已关注' : '+ 关注' }}
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { toggleFollow } from '../api/follow'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const props = defineProps({ user: { type: Object, required: true }, showFollow: { type: Boolean, default: true } })
const emit = defineEmits(['update'])
const loading = ref(false)
const userStore = useUserStore()
const router = useRouter()

async function handleFollow() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return router.push('/login') }
  loading.value = true
  try { const res = await toggleFollow(props.user.id); emit('update', { ...props.user, followed: res.data.followed }) } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.user-card {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 8px; cursor: pointer;
  border-radius: var(--radius-md);
  transition: all 0.25s;
}
.user-card:hover {
  background: var(--primary-bg);
  transform: translateX(4px);
}
.uc-info { flex: 1; min-width: 0; }
.uc-name { font-weight: 600; font-size: 14px; color: var(--text-primary); transition: color 0.2s; }
.user-card:hover .uc-name { color: var(--primary); }
.uc-meta { color: var(--text-muted); font-size: 12px; margin-top: 2px; font-weight: 500; }
</style>
