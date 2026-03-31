<template>
  <div class="post-card" ref="cardRef" @click="goToDetail" @mouseenter="onHover" @mouseleave="onLeave">
    <div class="card-inner">
      <div class="post-header">
        <div class="author-info" @click.stop="goToProfile">
          <el-avatar :size="42" :src="post.authorAvatar" class="author-avatar" />
          <div>
            <div class="author-name">{{ post.authorName }}</div>
            <div class="post-meta">
              <span class="post-time">{{ formatTime(post.createdAt) }}</span>
              <el-tag v-if="post.circleName" size="small" effect="plain" class="circle-tag" @click.stop="$router.push(`/circle/${post.circleId}`)">{{ post.circleName }}</el-tag>
            </div>
          </div>
        </div>
      </div>

      <h3 class="post-title">{{ post.title }}</h3>
      <div class="post-content">{{ truncatedContent }}</div>

      <div v-if="imageList.length" class="post-images">
        <el-image v-for="(img, idx) in imageList.slice(0, 3)" :key="idx" :src="img" fit="cover"
          class="post-image" :preview-src-list="imageList" :initial-index="idx" @click.stop lazy />
        <div v-if="imageList.length > 3" class="more-images">+{{ imageList.length - 3 }}</div>
      </div>

      <div class="post-footer" @click.stop>
        <div class="post-actions">
          <button class="action-btn" :class="{ active: post.liked }" @click="handleLike" ref="likeBtnRef">
            <el-icon><component :is="post.liked ? 'StarFilled' : 'Star'" /></el-icon>
            <span>{{ post.likeCount || 0 }}</span>
          </button>
          <button class="action-btn" @click="goToDetail">
            <el-icon><ChatLineSquare /></el-icon>
            <span>{{ post.commentCount || 0 }}</span>
          </button>
          <button class="action-btn" :class="{ active: post.favorited }" @click="handleFavorite">
            <el-icon><component :is="post.favorited ? 'CollectionTag' : 'Collection'" /></el-icon>
            <span>{{ post.favoriteCount || 0 }}</span>
          </button>
        </div>
        <div class="post-extra">
          <span class="read-time"><el-icon><Clock /></el-icon> {{ readTime }}分钟阅读</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { toggleLike, toggleFavorite } from '../api/post'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { gsap } from '../utils/animations'

const props = defineProps({ post: { type: Object, required: true } })
const emit = defineEmits(['update'])
const router = useRouter()
const userStore = useUserStore()
const cardRef = ref(null)
const likeBtnRef = ref(null)

const imageList = computed(() => {
  if (!props.post.images) return []
  return props.post.images.split(',').filter(Boolean)
})

const truncatedContent = computed(() => {
  const text = (props.post.content || '').replace(/<[^>]*>/g, '')
  return text.length > 160 ? text.substring(0, 160) + '...' : text
})

const readTime = computed(() => {
  const text = (props.post.content || '').replace(/<[^>]*>/g, '')
  return Math.max(1, Math.ceil(text.length / 400))
})

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr), diff = Date.now() - d.getTime(), m = Math.floor(diff / 60000)
  if (m < 1) return '刚刚'
  if (m < 60) return m + '分钟前'
  const h = Math.floor(m / 60)
  if (h < 24) return h + '小时前'
  const day = Math.floor(h / 24)
  if (day < 30) return day + '天前'
  return d.toLocaleDateString('zh-CN')
}

function onHover() {
  gsap.to(cardRef.value, { y: -4, boxShadow: '0 12px 32px rgba(0,0,0,0.1)', duration: 0.3, ease: 'power2.out' })
}
function onLeave() {
  gsap.to(cardRef.value, { y: 0, boxShadow: '0 2px 12px rgba(0,0,0,0.04)', duration: 0.3, ease: 'power2.out' })
}

function goToDetail() { router.push(`/post/${props.post.id}`) }
function goToProfile() { router.push(`/profile/${props.post.userId}`) }

async function handleLike() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return router.push('/login') }
  if (likeBtnRef.value) gsap.fromTo(likeBtnRef.value, { scale: 1 }, { scale: 1.3, duration: 0.15, yoyo: true, repeat: 1 })
  try { const res = await toggleLike(props.post.id); emit('update', res.data) } catch {}
}

async function handleFavorite() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return router.push('/login') }
  try { const res = await toggleFavorite(props.post.id); emit('update', res.data) } catch {}
}

onMounted(() => {
  if (cardRef.value) {
    gsap.fromTo(cardRef.value, { opacity: 0, y: 20 }, { opacity: 1, y: 0, duration: 0.45, ease: 'power2.out' })
  }
})
</script>

<style scoped>
.post-card {
  margin-bottom: 18px; cursor: pointer;
  border-radius: var(--radius-lg); background: var(--bg-card);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
  overflow: hidden; will-change: transform, box-shadow;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}
.post-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: var(--gradient-primary);
  opacity: 0; transition: opacity 0.3s;
}
.post-card:hover::before { opacity: 1; }
.card-inner { padding: 22px 24px; }
.post-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.author-info { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.author-info:hover .author-name { color: var(--primary); }
.author-avatar { flex-shrink: 0; }
.author-name { font-weight: 600; font-size: 15px; color: var(--text-primary); transition: color 0.2s; }
.post-meta { display: flex; align-items: center; gap: 8px; margin-top: 2px; }
.post-time { font-size: 12px; color: var(--text-muted); }
.circle-tag {
  font-size: 11px; cursor: pointer;
  background: var(--primary-bg) !important; color: var(--primary) !important;
  border: none !important;
}
.post-title {
  font-size: 17px; font-weight: 700; margin-bottom: 8px;
  line-height: 1.5; color: var(--text-primary); letter-spacing: -0.2px;
  transition: color 0.2s;
}
.post-card:hover .post-title { color: var(--primary); }
.post-content { color: var(--text-secondary); font-size: 14px; line-height: 1.75; margin-bottom: 14px; word-break: break-word; }
.post-images { display: flex; gap: 8px; margin-bottom: 14px; }
.post-image {
  width: 136px; height: 136px; border-radius: var(--radius-md);
  object-fit: cover; transition: transform 0.3s;
}
.post-image:hover { transform: scale(1.03); }
.more-images {
  width: 136px; height: 136px; border-radius: var(--radius-md);
  background: linear-gradient(135deg, rgba(79,70,229,0.7), rgba(168,85,247,0.7));
  backdrop-filter: blur(4px);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: 700;
}
.post-footer {
  display: flex; justify-content: space-between; align-items: center;
  border-top: 1px solid var(--border-light); padding-top: 14px;
}
.post-actions { display: flex; gap: 6px; }
.post-extra { display: flex; align-items: center; gap: 12px; }
.read-time {
  display: flex; align-items: center; gap: 4px;
  font-size: 12px; color: var(--text-muted); font-weight: 500;
}
.action-btn {
  display: flex; align-items: center; gap: 6px;
  background: none; border: none; cursor: pointer;
  padding: 7px 16px; border-radius: 20px;
  font-size: 13px; font-weight: 500; color: var(--text-muted);
  transition: all 0.25s;
}
.action-btn:hover { background: var(--bg-hover); color: var(--text-secondary); }
.action-btn.active {
  color: var(--primary);
  background: var(--primary-bg);
  font-weight: 600;
}
.action-btn .el-icon { font-size: 17px; }
</style>
