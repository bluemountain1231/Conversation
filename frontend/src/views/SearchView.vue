<template>
  <div class="search-page">
    <div class="search-header">
      <el-input v-model="keyword" placeholder="搜索帖子、圈子、用户..." size="large" clearable
        @keyup.enter="doSearch" class="search-input">
        <template #append>
          <el-button @click="doSearch" :icon="Search">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-tabs v-model="tab" @tab-change="doSearch">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="帖子" name="post" />
      <el-tab-pane label="圈子" name="circle" />
      <el-tab-pane label="用户" name="user" />
    </el-tabs>

    <div v-loading="loading">
      <template v-if="tab === 'all' || tab === 'post'">
        <h3 v-if="tab === 'all' && postList.length" class="section-title">帖子</h3>
        <PostCard v-for="p in postList" :key="p.id" :post="p" @update="i => handlePostUpdate(i)" />
      </template>

      <template v-if="tab === 'all' || tab === 'circle'">
        <h3 v-if="tab === 'all' && circleList.length" class="section-title">圈子</h3>
        <CircleCard v-for="c in circleList" :key="c.id" :circle="c" @update="i => handleCircleUpdate(i)" />
      </template>

      <template v-if="tab === 'all' || tab === 'user'">
        <h3 v-if="tab === 'all' && userList.length" class="section-title">用户</h3>
        <UserCard v-for="u in userList" :key="u.id" :user="u" @update="i => handleUserUpdate(i)" />
      </template>

      <el-empty v-if="searched && !postList.length && !circleList.length && !userList.length" description="未找到相关结果" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { search } from '../api/search'
import { Search } from '@element-plus/icons-vue'
import PostCard from '../components/PostCard.vue'
import CircleCard from '../components/CircleCard.vue'
import UserCard from '../components/UserCard.vue'

const route = useRoute()
const keyword = ref(route.query.q || '')
const tab = ref('all')
const loading = ref(false)
const searched = ref(false)

const postList = ref([])
const circleList = ref([])
const userList = ref([])

async function doSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  try {
    const res = await search(keyword.value, tab.value)
    postList.value = res.data.posts?.content || []
    circleList.value = res.data.circles?.content || []
    userList.value = res.data.users?.content || []
  } catch {} finally { loading.value = false }
}

function handlePostUpdate(p) {
  const idx = postList.value.findIndex(x => x.id === p.id)
  if (idx !== -1) postList.value[idx] = p
}
function handleCircleUpdate(c) {
  const idx = circleList.value.findIndex(x => x.id === c.id)
  if (idx !== -1) circleList.value[idx] = c
}
function handleUserUpdate(u) {
  const idx = userList.value.findIndex(x => x.id === u.id)
  if (idx !== -1) userList.value[idx] = u
}

onMounted(() => { if (keyword.value) doSearch() })
</script>

<style scoped>
.search-page { max-width: 680px; margin: 0 auto; }
.search-header { margin-bottom: 16px; }
.search-input { width: 100%; }
.section-title { font-size: 16px; margin: 20px 0 12px; color: #606266; }
</style>
