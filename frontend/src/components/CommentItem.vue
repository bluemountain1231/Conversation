<template>
  <div class="comment-item">
    <div class="comment-main">
      <el-avatar :size="36" :src="comment.authorAvatar" @click="$router.push(`/profile/${comment.userId}`)" style="cursor:pointer" />
      <div class="comment-body">
        <div class="comment-header">
          <span class="comment-author" @click="$router.push(`/profile/${comment.userId}`)">{{ comment.authorName }}</span>
          <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <el-button text size="small" class="reply-btn" @click="showReplyInput = !showReplyInput">
          <el-icon><ChatLineRound /></el-icon>回复
        </el-button>

        <div v-if="showReplyInput" class="reply-input">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            :placeholder="`回复 ${comment.authorName}...`"
          />
          <div class="reply-actions">
            <el-button size="small" @click="showReplyInput = false">取消</el-button>
            <el-button size="small" type="primary" @click="submitReply" :loading="submitting">发送</el-button>
          </div>
        </div>

        <div v-if="comment.replies && comment.replies.length" class="replies">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <el-avatar :size="28" :src="reply.authorAvatar" />
            <div class="reply-body">
              <div class="comment-header">
                <span class="comment-author" @click="$router.push(`/profile/${reply.userId}`)">{{ reply.authorName }}</span>
                <span class="comment-time">{{ formatTime(reply.createdAt) }}</span>
              </div>
              <div class="comment-content">{{ reply.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { addComment } from '../api/comment'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const props = defineProps({
  comment: { type: Object, required: true },
  postId: { type: [Number, String], required: true }
})

const emit = defineEmits(['replied'])
const userStore = useUserStore()
const router = useRouter()

const showReplyInput = ref(false)
const replyContent = ref('')
const submitting = ref(false)

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

async function submitReply() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push('/login')
  }
  if (!replyContent.value.trim()) {
    return ElMessage.warning('回复内容不能为空')
  }
  submitting.value = true
  try {
    await addComment(props.postId, {
      content: replyContent.value,
      parentId: props.comment.id
    })
    replyContent.value = ''
    showReplyInput.value = false
    ElMessage.success('回复成功')
    emit('replied')
  } catch {} finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.comment-item {
  padding: 18px 0;
  border-bottom: 1px solid var(--border-light);
  transition: background 0.2s;
}

.comment-main {
  display: flex;
  gap: 14px;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.comment-author {
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  color: var(--text-primary);
  transition: color 0.2s;
}

.comment-author:hover {
  color: var(--primary);
}

.comment-time {
  font-size: 12px;
  color: var(--text-muted);
}

.comment-content {
  font-size: 14px;
  line-height: 1.7;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.reply-btn {
  color: var(--text-muted) !important;
  padding: 0;
  font-weight: 500;
}
.reply-btn:hover {
  color: var(--primary) !important;
}

.reply-input {
  margin-top: 10px;
  padding: 12px;
  background: var(--bg-hover);
  border-radius: var(--radius-md);
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 10px;
}

.replies {
  margin-top: 14px;
  padding: 14px;
  background: linear-gradient(135deg, rgba(79,70,229,0.02), rgba(6,182,212,0.02));
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
}

.reply-item:not(:last-child) {
  border-bottom: 1px solid var(--border-light);
}

.reply-body {
  flex: 1;
}
</style>
