<template>
  <div class="circle-detail-page" v-loading="loading">
    <template v-if="circle">
      <el-card class="circle-header-card" shadow="never">
        <div class="circle-top">
          <el-avatar :size="72" :src="circle.avatar || defaultAvatar" shape="square" />
          <div class="circle-meta">
            <h1>{{ circle.name }}</h1>
            <p class="desc">{{ circle.description || '暂无介绍' }}</p>
            <div class="stats">
              <span>{{ circle.memberCount }} 成员</span>
              <span>{{ circle.postCount }} 帖子</span>
              <span>创建者: {{ circle.creatorName }}</span>
            </div>
          </div>
          <div class="actions">
            <el-button v-if="circle.joined" @click="handleLeave" :loading="actionLoading">退出圈子</el-button>
            <el-button v-else type="primary" @click="handleJoin" :loading="actionLoading">加入圈子</el-button>
            <el-button v-if="circle.joined" type="primary" @click="$router.push(`/post/create?circleId=${circle.id}`)">
              发帖
            </el-button>
          </div>
        </div>
      </el-card>

      <el-tabs v-model="activeTab" class="detail-tabs">
        <el-tab-pane label="圈内帖子" name="posts">
          <PostCard v-for="post in posts" :key="post.id" :post="post" @update="handlePostUpdate" />
          <el-empty v-if="posts.length === 0 && !postsLoading" description="暂无帖子" />
          <div v-if="!postsNoMore && posts.length > 0" class="load-more">
            <el-button text @click="loadPosts" :loading="postsLoading">加载更多</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="成员" name="members">
          <div v-for="m in members" :key="m.id" class="member-item" @click="$router.push(`/profile/${m.id}`)">
            <el-avatar :size="36" :src="m.avatar" />
            <span>{{ m.username }}</span>
          </div>
          <el-empty v-if="members.length === 0" description="暂无成员" />
        </el-tab-pane>
      </el-tabs>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCircleById, getCirclePosts, getCircleMembers, joinCircle, leaveCircle } from '../api/circle'
import PostCard from '../components/PostCard.vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const userStore = useUserStore()
const defaultAvatar = 'https://api.dicebear.com/7.x/shapes/svg?seed=circle'

const circle = ref(null)
const loading = ref(true)
const actionLoading = ref(false)
const activeTab = ref('posts')

const posts = ref([])
const postsPage = ref(0)
const postsLoading = ref(false)
const postsNoMore = ref(false)

const members = ref([])

async function loadCircle() {
  loading.value = true
  try {
    const res = await getCircleById(route.params.id)
    circle.value = res.data
  } catch {} finally { loading.value = false }
}

async function loadPosts() {
  if (postsLoading.value || postsNoMore.value) return
  postsLoading.value = true
  try {
    const res = await getCirclePosts(route.params.id, postsPage.value, 10)
    const items = res.data.content || []
    if (items.length < 10) postsNoMore.value = true
    posts.value.push(...items)
    postsPage.value++
  } catch {} finally { postsLoading.value = false }
}

async function loadMembers() {
  try {
    const res = await getCircleMembers(route.params.id)
    members.value = res.data.content || []
  } catch {}
}

function handlePostUpdate(p) {
  const idx = posts.value.findIndex(x => x.id === p.id)
  if (idx !== -1) posts.value[idx] = p
}

async function handleJoin() {
  if (!userStore.isLoggedIn) return ElMessage.warning('请先登录')
  actionLoading.value = true
  try {
    const res = await joinCircle(circle.value.id)
    circle.value = res.data
    ElMessage.success('加入成功')
  } catch {} finally { actionLoading.value = false }
}

async function handleLeave() {
  actionLoading.value = true
  try {
    const res = await leaveCircle(circle.value.id)
    circle.value = res.data
    ElMessage.success('已退出')
  } catch {} finally { actionLoading.value = false }
}

onMounted(() => { loadCircle(); loadPosts(); loadMembers() })
</script>

<style scoped>
.circle-detail-page { max-width: 800px; margin: 0 auto; }
.circle-header-card {
  border-radius: var(--radius-xl) !important;
  margin-bottom: 20px; overflow: hidden;
  position: relative;
}
.circle-header-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0; height: 120px;
  background: var(--gradient-primary); opacity: 0.06;
}
.circle-top { display: flex; gap: 24px; align-items: flex-start; position: relative; z-index: 1; }
.circle-top :deep(.el-avatar) {
  border: 4px solid white !important;
  box-shadow: var(--shadow-lg) !important;
  border-radius: var(--radius-lg) !important;
}
.circle-meta { flex: 1; }
.circle-meta h1 { font-size: 26px; font-weight: 800; margin-bottom: 8px; color: var(--text-primary); }
.desc { color: var(--text-secondary); margin-bottom: 10px; line-height: 1.6; }
.stats { color: var(--text-muted); font-size: 13px; display: flex; gap: 20px; font-weight: 500; }
.actions { display: flex; gap: 8px; flex-shrink: 0; }
.detail-tabs { margin-top: 8px; }
.detail-tabs :deep(.el-tabs__header) {
  background: var(--bg-card); border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  padding: 0 20px; box-shadow: var(--shadow-sm);
}
.member-item {
  display: flex; align-items: center; gap: 14px;
  padding: 12px 16px; cursor: pointer;
  border-radius: var(--radius-md); transition: all 0.25s;
}
.member-item:hover { background: var(--primary-bg); transform: translateX(4px); }
.load-more { text-align: center; padding: 20px; }
</style>
