<template>
  <div class="profile-page" v-loading="loading">
    <template v-if="user">
      <el-card class="profile-card" shadow="never">
        <div class="profile-header">
          <el-avatar :size="80" :src="user.avatar" />
          <div class="profile-info">
            <h2>{{ user.username }}</h2>
            <p class="bio">{{ user.bio || '这个人很懒，什么都没留下' }}</p>
            <div class="profile-stats">
              <div class="stat-item">
                <span class="stat-num">{{ user.postCount }}</span>
                <span class="stat-label">帖子</span>
              </div>
              <div class="stat-item">
                <span class="stat-num">{{ user.followerCount || 0 }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <div class="stat-item">
                <span class="stat-num">{{ user.followingCount || 0 }}</span>
                <span class="stat-label">关注</span>
              </div>
            </div>
          </div>
          <el-button v-if="isOwner" @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>编辑资料
          </el-button>
          <el-button v-if="!isOwner && userStore.isLoggedIn" :type="user.followed ? 'default' : 'primary'"
            @click="handleFollow" :loading="followLoading">
            {{ user.followed ? '已关注' : '关注' }}
          </el-button>
          <el-button v-if="!isOwner && userStore.isLoggedIn" @click="$router.push(`/chat/${user.id}`)">
            <el-icon><ChatLineSquare /></el-icon>私信
          </el-button>
        </div>
      </el-card>

      <el-tabs v-model="activeTab" class="profile-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="发布的帖子" name="posts">
          <PostCard
            v-for="post in posts"
            :key="post.id"
            :post="post"
            @update="handlePostUpdate"
          />
          <el-empty v-if="posts.length === 0 && !postsLoading" description="暂无帖子" />
          <div v-if="postsLoading" class="loading-area">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-if="!postsNoMore && posts.length > 0" class="load-more-btn">
            <el-button text @click="loadPosts" :loading="postsLoading">加载更多</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane v-if="isOwner" label="我的收藏" name="favorites">
          <PostCard
            v-for="post in favorites"
            :key="post.id"
            :post="post"
            @update="handleFavUpdate"
          />
          <el-empty v-if="favorites.length === 0 && !favsLoading" description="暂无收藏" />
          <div v-if="favsLoading" class="loading-area">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-if="!favsNoMore && favorites.length > 0" class="load-more-btn">
            <el-button text @click="loadFavorites" :loading="favsLoading">加载更多</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- Edit Profile Dialog -->
      <el-dialog v-model="showEditDialog" title="编辑资料" width="450px">
        <el-form :model="editForm" label-position="top">
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="editForm.bio" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="头像URL">
            <el-input v-model="editForm.avatar" placeholder="输入头像链接或上传图片" />
            <el-upload
              :action="'/api/upload/image'"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarUpload"
              accept="image/*"
              style="margin-top:8px"
            >
              <el-button size="small">上传头像</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateProfile" :loading="updating">保存</el-button>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getUserById, updateUser } from '../api/user'
import { getUserPosts, getUserFavorites } from '../api/post'
import { toggleFollow } from '../api/follow'
import { useUserStore } from '../stores/user'
import PostCard from '../components/PostCard.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()

const user = ref(null)
const loading = ref(true)
const activeTab = ref('posts')

const posts = ref([])
const postsPage = ref(0)
const postsLoading = ref(false)
const postsNoMore = ref(false)

const favorites = ref([])
const favsPage = ref(0)
const favsLoading = ref(false)
const favsNoMore = ref(false)

const followLoading = ref(false)
const showEditDialog = ref(false)
const updating = ref(false)
const editForm = reactive({ username: '', bio: '', avatar: '' })

const isOwner = computed(() =>
  userStore.userInfo && user.value && userStore.userInfo.id === user.value.id
)

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

const profileId = computed(() => route.params.id || userStore.userInfo?.id)

async function loadProfile() {
  if (!profileId.value) return
  loading.value = true
  try {
    const res = await getUserById(profileId.value)
    user.value = res.data
    editForm.username = res.data.username
    editForm.bio = res.data.bio || ''
    editForm.avatar = res.data.avatar || ''
  } catch {} finally {
    loading.value = false
  }
}

async function loadPosts() {
  if (postsLoading.value || postsNoMore.value) return
  postsLoading.value = true
  try {
    const res = await getUserPosts(profileId.value, postsPage.value, 10)
    const newPosts = res.data.content || []
    if (newPosts.length < 10) postsNoMore.value = true
    posts.value.push(...newPosts)
    postsPage.value++
  } catch {} finally {
    postsLoading.value = false
  }
}

async function loadFavorites() {
  if (favsLoading.value || favsNoMore.value) return
  favsLoading.value = true
  try {
    const res = await getUserFavorites(favsPage.value, 10)
    const newFavs = res.data.content || []
    if (newFavs.length < 10) favsNoMore.value = true
    favorites.value.push(...newFavs)
    favsPage.value++
  } catch {} finally {
    favsLoading.value = false
  }
}

function handleTabChange(tab) {
  if (tab === 'favorites' && favorites.value.length === 0) {
    loadFavorites()
  }
}

function handlePostUpdate(updatedPost) {
  const idx = posts.value.findIndex(p => p.id === updatedPost.id)
  if (idx !== -1) posts.value[idx] = updatedPost
}

function handleFavUpdate(updatedPost) {
  const idx = favorites.value.findIndex(p => p.id === updatedPost.id)
  if (idx !== -1) favorites.value[idx] = updatedPost
}

async function handleFollow() {
  followLoading.value = true
  try {
    const res = await toggleFollow(user.value.id)
    user.value.followed = res.data.followed
    user.value.followerCount += res.data.followed ? 1 : -1
  } catch {} finally { followLoading.value = false }
}

function handleAvatarUpload(response) {
  if (response.code === 200) {
    editForm.avatar = response.data.url
    ElMessage.success('头像上传成功')
  }
}

async function handleUpdateProfile() {
  updating.value = true
  try {
    const res = await updateUser(editForm)
    user.value = res.data
    userStore.setUser(res.data)
    showEditDialog.value = false
    ElMessage.success('资料更新成功')
  } catch {} finally {
    updating.value = false
  }
}

watch(() => route.params.id, () => {
  posts.value = []
  favorites.value = []
  postsPage.value = 0
  favsPage.value = 0
  postsNoMore.value = false
  favsNoMore.value = false
  loadProfile()
  loadPosts()
})

onMounted(() => {
  loadProfile()
  loadPosts()
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  border-radius: var(--radius-xl) !important;
  margin-bottom: 20px;
  overflow: hidden;
  position: relative;
}
.profile-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0; height: 100px;
  background: var(--gradient-primary);
  opacity: 0.08;
}

.profile-header {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.profile-header :deep(.el-avatar) {
  border: 4px solid white !important;
  box-shadow: var(--shadow-lg) !important;
}

.profile-info {
  flex: 1;
}

.profile-info h2 {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 6px;
  color: var(--text-primary);
}

.bio {
  color: var(--text-muted);
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.profile-stats {
  display: flex;
  gap: 28px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  transition: background 0.2s;
  cursor: default;
}
.stat-item:hover {
  background: var(--primary-bg);
}

.stat-num {
  font-weight: 800;
  font-size: 18px;
  color: var(--primary);
}

.stat-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
  margin-top: 2px;
}

.profile-tabs {
  margin-top: 8px;
}

.profile-tabs :deep(.el-tabs__header) {
  background: var(--bg-card);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  padding: 0 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}

.loading-area {
  padding: 20px;
}

.load-more-btn {
  text-align: center;
  padding: 20px;
}
</style>
