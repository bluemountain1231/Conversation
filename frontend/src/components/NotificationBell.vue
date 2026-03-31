<template>
  <el-popover placement="bottom-end" :width="360" trigger="click" @show="loadNotifications">
    <template #reference>
      <el-badge :value="store.unreadCount" :hidden="!store.unreadCount" :max="99">
        <el-button text circle><el-icon :size="20"><Bell /></el-icon></el-button>
      </el-badge>
    </template>
    <div class="notif-header">
      <span class="notif-title">通知</span>
      <el-button text size="small" @click="store.markAllRead" v-if="store.unreadCount">全部已读</el-button>
    </div>
    <div class="notif-list" v-if="list.length">
      <div v-for="n in list" :key="n.id" class="notif-item" @click="handleClick(n)">
        <div class="notif-content">
          <span class="notif-from">{{ n.fromUsername }}</span>
          <span>{{ n.content }}</span>
        </div>
        <div class="notif-time">{{ formatTime(n.createdAt) }}</div>
      </div>
    </div>
    <el-empty v-else :image-size="60" description="暂无通知" />
    <div class="notif-footer">
      <router-link to="/notifications">查看全部</router-link>
    </div>
  </el-popover>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../stores/notification'
import request from '../api/request'

const store = useNotificationStore()
const router = useRouter()
const list = ref([])

async function loadNotifications() {
  try {
    const res = await request.get('/notifications', { params: { page: 0, size: 10 } })
    list.value = res.data.content || []
  } catch {}
}

function handleClick(n) {
  if (n.type === 'LIKE' || n.type === 'COMMENT') router.push(`/post/${n.relatedId}`)
  else if (n.type === 'FOLLOW') router.push(`/profile/${n.fromUserId}`)
  else if (n.type === 'MESSAGE') router.push(`/chat/${n.fromUserId}`)
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const diff = Date.now() - d.getTime()
  const min = Math.floor(diff / 60000)
  if (min < 1) return '刚刚'
  if (min < 60) return min + '分钟前'
  const hr = Math.floor(min / 60)
  if (hr < 24) return hr + '小时前'
  return Math.floor(hr / 24) + '天前'
}
</script>

<style scoped>
.notif-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding-bottom: 10px; border-bottom: 1px solid var(--border-light); }
.notif-title { font-weight: 700; font-size: 16px; color: var(--text-primary); }
.notif-list { max-height: 340px; overflow-y: auto; }
.notif-item {
  padding: 12px 8px; border-radius: var(--radius-sm);
  cursor: pointer; transition: all 0.2s;
  margin-bottom: 2px;
}
.notif-item:hover { background: var(--primary-bg); }
.notif-from { font-weight: 700; margin-right: 4px; color: var(--primary); }
.notif-content { font-size: 14px; line-height: 1.5; color: var(--text-secondary); }
.notif-time { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
.notif-footer { text-align: center; padding-top: 10px; border-top: 1px solid var(--border-light); margin-top: 8px; }
.notif-footer a { color: var(--primary); font-size: 13px; font-weight: 600; }
</style>
