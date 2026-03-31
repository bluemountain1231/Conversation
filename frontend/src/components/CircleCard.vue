<template>
  <div class="circle-card" ref="cardRef" @click="$router.push(`/circle/${circle.id}`)">
    <div class="cc-left">
      <el-avatar :size="52" :src="circle.avatar || defaultAvatar" shape="square" class="cc-avatar" />
    </div>
    <div class="cc-body">
      <h4 class="cc-name">{{ circle.name }}</h4>
      <p class="cc-desc">{{ circle.description || '暂无介绍' }}</p>
      <div class="cc-stats">
        <span>{{ circle.memberCount }} 成员</span>
        <span>·</span>
        <span>{{ circle.postCount }} 帖子</span>
      </div>
    </div>
    <el-button v-if="showAction" :type="circle.joined ? 'default' : 'primary'" size="small" round
      @click.stop="handleToggle" :loading="loading" class="cc-btn">
      {{ circle.joined ? '已加入' : '加入' }}
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { joinCircle, leaveCircle } from '../api/circle'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { gsap } from '../utils/animations'

const props = defineProps({ circle: { type: Object, required: true }, showAction: { type: Boolean, default: true } })
const emit = defineEmits(['update'])
const loading = ref(false)
const userStore = useUserStore()
const router = useRouter()
const cardRef = ref(null)
const defaultAvatar = 'https://api.dicebear.com/7.x/shapes/svg?seed=circle'

async function handleToggle() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return router.push('/login') }
  loading.value = true
  try {
    const res = props.circle.joined ? await leaveCircle(props.circle.id) : await joinCircle(props.circle.id)
    emit('update', res.data)
  } catch {} finally { loading.value = false }
}

onMounted(() => { if (cardRef.value) gsap.fromTo(cardRef.value, { opacity: 0, y: 16 }, { opacity: 1, y: 0, duration: 0.4, ease: 'power2.out' }) })
</script>

<style scoped>
.circle-card {
  display: flex; align-items: center; gap: 16px;
  padding: 16px 20px; background: var(--bg-card);
  border-radius: var(--radius-lg); cursor: pointer;
  margin-bottom: 12px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}
.circle-card::after {
  content: '';
  position: absolute; top: 0; left: 0; bottom: 0; width: 3px;
  background: var(--gradient-primary);
  opacity: 0; transition: opacity 0.3s;
}
.circle-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-3px);
}
.circle-card:hover::after { opacity: 1; }
.cc-avatar { border-radius: var(--radius-md) !important; flex-shrink: 0; }
.cc-body { flex: 1; min-width: 0; }
.cc-name { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; transition: color 0.2s; }
.circle-card:hover .cc-name { color: var(--primary); }
.cc-desc { color: var(--text-muted); font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 6px; }
.cc-stats { display: flex; gap: 6px; color: var(--text-muted); font-size: 12px; font-weight: 500; }
.cc-btn { flex-shrink: 0; }
</style>
