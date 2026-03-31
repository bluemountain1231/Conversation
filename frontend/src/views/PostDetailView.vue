<template>
  <div class="post-detail-wrapper">
  <!-- Reading Progress Bar -->
  <div class="reading-progress" :style="{ width: readingProgress + '%' }"></div>

  <div class="post-detail-page" v-loading="loading">
    <template v-if="post">
      <!-- Post Meta Bar -->
      <div class="post-meta-bar">
        <div class="pmb-left">
          <span class="pmb-circle" v-if="post.circleName" @click="$router.push(`/circle/${post.circleId}`)">
            <el-icon><Compass /></el-icon> {{ post.circleName }}
          </span>
          <span class="pmb-time"><el-icon><Clock /></el-icon> {{ formatTime(post.createdAt) }}</span>
          <span class="pmb-read"><el-icon><View /></el-icon> {{ readTime }}分钟阅读</span>
        </div>
        <div class="pmb-right">
          <el-button v-if="isOwner" type="danger" text size="small" @click="handleDelete">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </div>
      </div>

      <el-card class="detail-card" shadow="never">
        <h1 class="post-title">{{ post.title }}</h1>

        <div class="author-bar">
          <div class="author-info" @click="$router.push(`/profile/${post.userId}`)">
            <el-avatar :size="44" :src="post.authorAvatar" />
            <div>
              <div class="author-name">{{ post.authorName }}</div>
              <div class="author-meta">发布于 {{ formatTime(post.createdAt) }}</div>
            </div>
          </div>
          <el-button
            v-if="!isOwner && userStore.isLoggedIn"
            :type="post.authorFollowed ? 'default' : 'primary'"
            size="small" round
          >
            {{ post.authorFollowed ? '已关注' : '+ 关注' }}
          </el-button>
        </div>

        <div class="post-content" v-html="post.content"></div>

        <div v-if="imageList.length" class="post-images">
          <el-image
            v-for="(img, idx) in imageList"
            :key="idx"
            :src="img"
            fit="cover"
            class="detail-image"
            :preview-src-list="imageList"
            :initial-index="idx"
          />
        </div>

        <!-- Tags -->
        <div class="post-tags" v-if="post.circleName">
          <el-tag effect="plain" round size="small">{{ post.circleName }}</el-tag>
        </div>

        <!-- Interaction Bar -->
        <div class="interaction-bar">
          <div class="post-stats">
            <button class="stat-btn" :class="{ active: post.liked }" @click="handleLike">
              <el-icon><component :is="post.liked ? 'StarFilled' : 'Star'" /></el-icon>
              <span class="stat-count">{{ post.likeCount }}</span>
              <span class="stat-label">点赞</span>
            </button>
            <button class="stat-btn" @click="scrollToComments">
              <el-icon><ChatLineSquare /></el-icon>
              <span class="stat-count">{{ post.commentCount }}</span>
              <span class="stat-label">评论</span>
            </button>
            <button class="stat-btn" :class="{ active: post.favorited }" @click="handleFavorite">
              <el-icon><component :is="post.favorited ? 'CollectionTag' : 'Collection'" /></el-icon>
              <span class="stat-count">{{ post.favoriteCount }}</span>
              <span class="stat-label">收藏</span>
            </button>
          </div>
          <div class="share-area">
            <button class="share-btn" @click="shareVisible = !shareVisible">
              <el-icon><Share /></el-icon> 分享
            </button>
          </div>
        </div>

        <!-- Share Panel -->
        <Transition name="slide-down">
          <div class="share-panel" v-if="shareVisible">
            <div class="share-option" @click="copyLink">
              <el-icon><Link /></el-icon>
              <span>复制链接</span>
            </div>
            <div class="share-option">
              <el-icon><ChatDotRound /></el-icon>
              <span>微信</span>
            </div>
            <div class="share-option">
              <el-icon><Promotion /></el-icon>
              <span>微博</span>
            </div>
          </div>
        </Transition>
      </el-card>

      <!-- Comment Section -->
      <el-card class="comment-section" shadow="never">
        <h3 style="margin-bottom: 16px">评论 ({{ post.commentCount }})</h3>

        <div v-if="userStore.isLoggedIn" class="comment-input">
          <el-avatar :size="36" :src="userStore.userInfo?.avatar" />
          <div class="input-area">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
            />
            <el-button type="primary" @click="submitComment" :loading="commentSubmitting" style="margin-top:8px;align-self:flex-end">
              发表评论
            </el-button>
          </div>
        </div>
        <el-alert v-else type="info" :closable="false" style="margin-bottom:16px">
          <template #default>
            <router-link to="/login" style="color:#409eff">登录</router-link> 后参与评论
          </template>
        </el-alert>

        <div class="comments-list">
          <CommentItem
            v-for="comment in comments"
            :key="comment.id"
            :comment="comment"
            :post-id="post.id"
            @replied="loadComments"
          />
          <el-empty v-if="!comments.length" description="暂无评论，快来抢沙发" :image-size="80" />
        </div>
      </el-card>
    </template>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostById, toggleLike, toggleFavorite, deletePost } from '../api/post'
import { getComments, addComment } from '../api/comment'
import { useUserStore } from '../stores/user'
import CommentItem from '../components/CommentItem.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref(null)
const comments = ref([])
const loading = ref(true)
const commentContent = ref('')
const commentSubmitting = ref(false)
const readingProgress = ref(0)
const shareVisible = ref(false)

const readTime = computed(() => {
  if (!post.value?.content) return 1
  const text = post.value.content.replace(/<[^>]*>/g, '')
  return Math.max(1, Math.ceil(text.length / 400))
})

function handleScroll() {
  const el = document.documentElement
  const scrollTop = el.scrollTop
  const scrollHeight = el.scrollHeight - el.clientHeight
  readingProgress.value = scrollHeight > 0 ? Math.min(100, (scrollTop / scrollHeight) * 100) : 0
}

function scrollToComments() {
  document.querySelector('.comment-section')?.scrollIntoView({ behavior: 'smooth' })
}

function copyLink() {
  navigator.clipboard?.writeText(window.location.href)
  ElMessage.success('链接已复制')
  shareVisible.value = false
}

const isOwner = computed(() =>
  userStore.userInfo && post.value && userStore.userInfo.id === post.value.userId
)

const imageList = computed(() => {
  if (!post.value?.images) return []
  return post.value.images.split(',').filter(Boolean)
})

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

async function loadPost() {
  loading.value = true
  try {
    const res = await getPostById(route.params.id)
    post.value = res.data
  } catch {
    router.push('/')
  } finally {
    loading.value = false
  }
}

async function loadComments() {
  try {
    const res = await getComments(route.params.id)
    comments.value = res.data
  } catch {}
}

async function submitComment() {
  if (!commentContent.value.trim()) {
    return ElMessage.warning('请输入评论内容')
  }
  commentSubmitting.value = true
  try {
    await addComment(route.params.id, { content: commentContent.value, parentId: null })
    commentContent.value = ''
    ElMessage.success('评论成功')
    await loadComments()
    await loadPost()
  } catch {} finally {
    commentSubmitting.value = false
  }
}

async function handleLike() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push('/login')
  }
  try {
    const res = await toggleLike(post.value.id)
    post.value = res.data
  } catch {}
}

async function handleFavorite() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push('/login')
  }
  try {
    const res = await toggleFavorite(post.value.id)
    post.value = res.data
  } catch {}
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePost(post.value.id)
    ElMessage.success('删除成功')
    router.push('/')
  } catch {}
}

onMounted(() => {
  loadPost()
  loadComments()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.post-detail-wrapper { position: relative; }

/* Reading Progress */
.reading-progress {
  position: fixed; top: 64px; left: 0; height: 3px; z-index: 999;
  background: var(--gradient-primary);
  transition: width 0.15s linear;
  border-radius: 0 2px 2px 0;
}

.post-detail-page { max-width: 800px; margin: 0 auto; }

/* Meta Bar */
.post-meta-bar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 20px; margin-bottom: 16px;
  background: var(--bg-card); border-radius: var(--radius-lg);
  border: 1px solid var(--border-soft); box-shadow: var(--shadow-sm);
}
.pmb-left { display: flex; align-items: center; gap: 16px; }
.pmb-left > span {
  display: flex; align-items: center; gap: 4px;
  font-size: 13px; color: var(--text-muted); font-weight: 500;
}
.pmb-circle {
  color: var(--primary) !important; cursor: pointer;
  padding: 3px 10px; background: var(--primary-bg); border-radius: 6px;
  transition: all 0.2s;
}
.pmb-circle:hover { background: var(--primary); color: #fff !important; }

.detail-card {
  border-radius: var(--radius-xl) !important;
  margin-bottom: 20px;
}

/* Title */
.post-title {
  font-size: 28px; font-weight: 800;
  line-height: 1.4; margin-bottom: 20px;
  color: var(--text-primary); letter-spacing: -0.3px;
}

/* Author Bar */
.author-bar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 0; margin-bottom: 24px;
  border-top: 1px solid var(--border-light);
  border-bottom: 1px solid var(--border-light);
}
.author-info {
  display: flex; align-items: center; gap: 14px; cursor: pointer;
  padding: 4px 8px 4px 4px; border-radius: var(--radius-md);
  transition: background 0.2s;
}
.author-info:hover { background: var(--primary-bg); }
.author-name {
  font-weight: 700; font-size: 15px;
  color: var(--text-primary); transition: color 0.2s;
}
.author-info:hover .author-name { color: var(--primary); }
.author-meta { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

/* Content */
.post-content {
  font-size: 15.5px; line-height: 1.9;
  color: var(--text-secondary); margin-bottom: 24px;
  word-break: break-all;
}
.post-content :deep(img) {
  max-width: 100%; border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm); margin: 12px 0;
}
.post-content :deep(p) { margin-bottom: 12px; }
.post-content :deep(strong) { color: var(--text-primary); }
.post-content :deep(blockquote) {
  border-left: 3px solid var(--primary);
  padding: 8px 16px; margin: 12px 0;
  background: var(--primary-bg); border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.post-images {
  display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 24px;
}
.detail-image {
  width: 200px; height: 200px; border-radius: var(--radius-md);
  object-fit: cover; transition: transform 0.3s; box-shadow: var(--shadow-sm);
}
.detail-image:hover { transform: scale(1.03); }

/* Tags */
.post-tags { margin-bottom: 20px; }

/* Interaction Bar */
.interaction-bar {
  display: flex; justify-content: space-between; align-items: center;
  border-top: 1px solid var(--border-light); padding-top: 16px;
}
.post-stats { display: flex; gap: 8px; }
.stat-btn {
  display: flex; align-items: center; gap: 6px;
  background: none; border: none; cursor: pointer;
  padding: 8px 18px; border-radius: 24px;
  font-size: 13px; font-weight: 500; color: var(--text-muted);
  transition: all 0.25s;
}
.stat-btn:hover { background: var(--bg-hover); color: var(--text-secondary); }
.stat-btn.active { color: var(--primary); background: var(--primary-bg); }
.stat-btn .el-icon { font-size: 18px; }
.stat-count { font-weight: 700; }
.stat-label { font-size: 12px; }

.share-btn {
  display: flex; align-items: center; gap: 5px;
  padding: 8px 16px; border-radius: 20px; border: 1.5px solid var(--border-light);
  background: var(--bg-card); cursor: pointer; font-size: 13px; font-weight: 600;
  color: var(--text-secondary); transition: all 0.25s;
}
.share-btn:hover { border-color: var(--primary); color: var(--primary); background: var(--primary-bg); }

/* Share Panel */
.share-panel {
  display: flex; gap: 12px; padding: 16px;
  margin-top: 16px; background: var(--bg-hover);
  border-radius: var(--radius-md); border: 1px solid var(--border-light);
}
.share-option {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 12px 20px; border-radius: var(--radius-sm);
  cursor: pointer; transition: all 0.2s;
  font-size: 12px; font-weight: 600; color: var(--text-secondary);
}
.share-option:hover { background: var(--primary-bg); color: var(--primary); }
.share-option .el-icon { font-size: 22px; }

.slide-down-enter-active { transition: all 0.3s ease; }
.slide-down-leave-active { transition: all 0.2s ease; }
.slide-down-enter-from { opacity: 0; transform: translateY(-8px); }
.slide-down-leave-to { opacity: 0; transform: translateY(-8px); }

/* Comment Section */
.comment-section { border-radius: var(--radius-xl) !important; }
.comment-section h3 {
  font-size: 18px; font-weight: 700;
  color: var(--text-primary);
  display: flex; align-items: center; gap: 8px;
}
.comment-section h3::before {
  content: ''; width: 3px; height: 18px;
  background: var(--gradient-primary); border-radius: 2px;
}

.comment-input {
  display: flex; gap: 14px; margin-bottom: 24px;
  padding: 16px; background: var(--bg-hover);
  border-radius: var(--radius-lg); border: 1px solid var(--border-light);
}
.input-area { flex: 1; display: flex; flex-direction: column; }
</style>
