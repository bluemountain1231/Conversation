<template>
  <div class="notification-page">
    <div class="page-header">
      <h1>我的通知</h1>
      <el-button text @click="markRead" v-if="notifStore.unreadCount">全部标为已读</el-button>
    </div>
    <div v-if="loading && list.length === 0"><el-skeleton :rows="5" animated /></div>
    <div v-else>
      <div v-for="n in list" :key="n.id" class="notif-item" @click="handleClick(n)">
        <el-avatar :size="40" :src="'https://api.dicebear.com/7.x/avataaars/svg?seed=' + n.fromUsername" />
        <div class="notif-body">
          <div><span class="from">{{ n.fromUsername }}</span> {{ n.content }}</div>
          <div class="time">{{ n.createdAt }}</div>
        </div>
        <el-tag v-if="!n.isRead" type="danger" size="small">未读</el-tag>
      </div>
      <el-empty v-if="!list.length" description="暂无通知" />
      <div v-if="!noMore && list.length" class="load-more">
        <el-button text @click="load" :loading="loading">加载更多</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../stores/notification'
import request from '../api/request'

const router = useRouter()
const notifStore = useNotificationStore()
const list = ref([])
const loading = ref(false)
const page = ref(0)
const noMore = ref(false)

async function load() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await request.get('/notifications', { params: { page: page.value, size: 20 } })
    const items = res.data.content || []
    if (items.length < 20) noMore.value = true
    list.value.push(...items)
    page.value++
  } catch {} finally { loading.value = false }
}

function handleClick(n) {
  if (n.type === 'LIKE' || n.type === 'COMMENT') router.push(`/post/${n.relatedId}`)
  else if (n.type === 'FOLLOW') router.push(`/profile/${n.fromUserId}`)
  else if (n.type === 'MESSAGE') router.push(`/chat/${n.fromUserId}`)
}

async function markRead() {
  await notifStore.markAllRead()
  list.value.forEach(n => n.isRead = true)
}

onMounted(() => load())
</script>

<style scoped>
.notification-page { max-width: 680px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h1 { font-size: 24px; }
.notif-item { display: flex; align-items: center; gap: 12px; padding: 14px 16px; background: #fff; border-radius: 10px; margin-bottom: 8px; cursor: pointer; }
.notif-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.notif-body { flex: 1; }
.from { font-weight: 600; }
.time { color: #909399; font-size: 12px; margin-top: 4px; }
.load-more { text-align: center; padding: 16px; }
</style>
