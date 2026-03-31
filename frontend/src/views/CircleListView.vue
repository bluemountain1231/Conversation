<template>
  <div class="circle-list-page">
    <div class="page-header">
      <h1>发现圈子</h1>
      <el-button type="primary" @click="$router.push('/circle/create')" v-if="userStore.isLoggedIn">
        <el-icon><Plus /></el-icon>创建圈子
      </el-button>
    </div>

    <el-tabs v-model="tab" @tab-change="handleTabChange">
      <el-tab-pane label="全部圈子" name="all" />
      <el-tab-pane v-if="userStore.isLoggedIn" label="我的圈子" name="my" />
    </el-tabs>

    <div v-if="loading && list.length === 0"><el-skeleton :rows="4" animated /></div>
    <div v-else>
      <CircleCard v-for="c in list" :key="c.id" :circle="c" @update="handleUpdate" />
      <el-empty v-if="list.length === 0" description="暂无圈子" />
      <div v-if="!noMore && tab === 'all' && list.length > 0" class="load-more">
        <el-button text @click="loadCircles" :loading="loading">加载更多</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CircleCard from '../components/CircleCard.vue'
import { getCircles, getMyCircles } from '../api/circle'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const tab = ref('all')
const list = ref([])
const loading = ref(false)
const page = ref(0)
const noMore = ref(false)

async function loadCircles() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await getCircles(page.value, 10)
    const items = res.data.content || []
    if (items.length < 10) noMore.value = true
    list.value.push(...items)
    page.value++
  } catch {} finally { loading.value = false }
}

async function loadMyCircles() {
  loading.value = true
  try {
    const res = await getMyCircles()
    list.value = res.data || []
  } catch {} finally { loading.value = false }
}

function handleTabChange(t) {
  list.value = []
  page.value = 0
  noMore.value = false
  if (t === 'my') loadMyCircles()
  else loadCircles()
}

function handleUpdate(updated) {
  const idx = list.value.findIndex(c => c.id === updated.id)
  if (idx !== -1) list.value[idx] = updated
}

onMounted(() => loadCircles())
</script>

<style scoped>
.circle-list-page { max-width: 700px; margin: 0 auto; }
.page-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 24px; padding: 20px 24px;
  background: var(--bg-card); border-radius: var(--radius-xl);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
}
.page-header h1 {
  font-size: 26px; font-weight: 800;
  background: var(--gradient-primary);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.load-more { text-align: center; padding: 20px; }
</style>
