<template>
  <div class="chat-page">
    <!-- IM Error State -->
    <div v-if="initError" class="chat-error">
      <el-empty description="即时通讯服务暂不可用">
        <template #image>
          <el-icon :size="60" color="var(--text-muted)"><ChatLineSquare /></el-icon>
        </template>
        <el-button type="primary" @click="retryInit">重试连接</el-button>
      </el-empty>
    </div>

    <div v-else class="chat-layout">
      <div class="conv-list">
        <h3>消息</h3>
        <div v-if="targetUser && !conversations.find(c => c.conversationID === targetConvId)"
          class="conv-item active">
          <el-avatar :size="40" :src="targetUser.avatar || defaultAvatar" />
          <div class="conv-info">
            <div class="conv-name">{{ targetUser.username }}</div>
            <div class="conv-last">新会话</div>
          </div>
        </div>
        <div v-for="conv in conversations" :key="conv.conversationID"
          class="conv-item" :class="{ active: activeConvId === conv.conversationID }"
          @click="selectConv(conv)">
          <el-avatar :size="40" :src="getRealAvatar(conv)" />
          <div class="conv-info">
            <div class="conv-name">{{ getRealName(conv) }}</div>
            <div class="conv-last">{{ conv.lastMessage?.messageForShow || '' }}</div>
          </div>
          <el-badge v-if="conv.unreadCount" :value="conv.unreadCount" :max="99" />
        </div>
        <el-empty v-if="!conversations.length && !targetUser" :image-size="60" description="暂无会话" />
      </div>

      <div class="chat-area" v-if="activeConvId">
        <div class="chat-header">
          <span>{{ chatTitle }}</span>
        </div>
        <div class="msg-list" ref="msgListRef">
          <el-empty v-if="!messages.length" :image-size="60" description="发送一条消息开始聊天吧" />
          <div v-for="msg in messages" :key="msg.ID" class="msg-item" :class="{ mine: msg.flow === 'out' }">
            <el-avatar :size="32" :src="getMsgAvatar(msg)" />
            <div class="msg-bubble">
              <div v-if="msg.type === 'TIMTextElem'" class="msg-text">{{ msg.payload.text }}</div>
              <img v-else-if="msg.type === 'TIMImageElem'" :src="msg.payload.imageInfoArray?.[0]?.url" class="msg-img" />
              <div v-else class="msg-text">[不支持的消息类型]</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="sendMsg" />
          <el-button type="primary" @click="sendMsg" :disabled="!inputText.trim()">发送</el-button>
        </div>
      </div>
      <div v-else class="chat-area chat-empty">
        <el-empty description="选择一个会话开始聊天" />
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { initTIM, getTIM, sendTextMessage, getConversationList, getMessageList, TencentCloudChat } from '../utils/tim'
import request from '../api/request'

const route = useRoute()
const userStore = useUserStore()
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'
const conversations = ref([])
const activeConvId = ref(null)
const targetUser = ref(null)
const targetImId = ref(null)
const messages = ref([])
const inputText = ref('')
const msgListRef = ref(null)

const initError = ref(false)
const userInfoCache = reactive({})
let onMessageHandler = null

const targetConvId = computed(() => targetImId.value ? `C2C${targetImId.value}` : null)

const chatTitle = computed(() => {
  if (targetUser.value && activeConvId.value === targetConvId.value) return targetUser.value.username
  const imId = activeConvId.value?.replace('C2C', '')
  if (imId && userInfoCache[imId]) return userInfoCache[imId].username
  const conv = conversations.value.find(c => c.conversationID === activeConvId.value)
  return conv?.userProfile?.nick || conv?.userProfile?.userID || '聊天'
})

function imIdToUserId(imId) {
  return imId ? imId.replace('user_', '') : null
}

async function fetchUserInfo(imId) {
  if (!imId || userInfoCache[imId]) return
  const realId = imIdToUserId(imId)
  if (!realId) return
  try {
    const res = await request.get(`/im/user-info/${realId}`)
    userInfoCache[imId] = res.data
  } catch {}
}

function getRealAvatar(conv) {
  const imId = conv.conversationID?.replace('C2C', '')
  if (imId && userInfoCache[imId]) return userInfoCache[imId].avatar || defaultAvatar
  return conv.userProfile?.avatar || defaultAvatar
}

function getRealName(conv) {
  const imId = conv.conversationID?.replace('C2C', '')
  if (imId && userInfoCache[imId]) return userInfoCache[imId].username
  return conv.userProfile?.nick || conv.userProfile?.userID || '未知'
}

function getMsgAvatar(msg) {
  if (msg.flow === 'out') return userStore.userInfo?.avatar || defaultAvatar
  const imId = msg.from
  if (imId && userInfoCache[imId]) return userInfoCache[imId].avatar || defaultAvatar
  return defaultAvatar
}

async function init() {
  try {
    const chat = await initTIM()

    onMessageHandler = function (event) {
      for (const msg of event.data) {
        if (activeConvId.value && msg.conversationID === activeConvId.value) {
          messages.value.push(msg)
          scrollBottom()
        }
        fetchUserInfo(msg.from)
      }
      refreshConvList()
    }
    chat.on(TencentCloudChat.EVENT.MESSAGE_RECEIVED, onMessageHandler)

    await refreshConvList()

    if (route.params.userId) await openChatWithUser(route.params.userId)
  } catch (e) {
    console.error('IM init failed:', e)
    initError.value = true
  }
}

async function retryInit() {
  initError.value = false
  await init()
}

async function openChatWithUser(userId) {
  try {
    const info = await request.get(`/im/user-info/${userId}`)
    targetUser.value = info.data
    targetImId.value = info.data.imUserId
    activeConvId.value = `C2C${info.data.imUserId}`
    userInfoCache[info.data.imUserId] = info.data

    const conv = conversations.value.find(c => c.conversationID === activeConvId.value)
    if (conv) await loadMessages(conv.conversationID)
  } catch (e) {
    console.error('Open chat failed:', e)
  }
}

async function refreshConvList() {
  try {
    conversations.value = await getConversationList()
    for (const conv of conversations.value) {
      const imId = conv.conversationID?.replace('C2C', '')
      if (imId) fetchUserInfo(imId)
    }
  } catch {}
}

async function selectConv(conv) {
  activeConvId.value = conv.conversationID
  const imId = conv.conversationID?.replace('C2C', '')
  if (imId && userInfoCache[imId]) {
    targetUser.value = userInfoCache[imId]
    targetImId.value = imId
  } else {
    targetUser.value = null
    targetImId.value = imId
  }
  await loadMessages(conv.conversationID)
  const chat = getTIM()
  if (chat) chat.setMessageRead({ conversationID: conv.conversationID }).catch(() => {})
}

async function loadMessages(conversationID) {
  try {
    const data = await getMessageList(conversationID)
    messages.value = data.messageList || []
    for (const msg of messages.value) {
      if (msg.from) fetchUserInfo(msg.from)
    }
    scrollBottom()
  } catch { messages.value = [] }
}

async function sendMsg() {
  if (!inputText.value.trim() || !activeConvId.value) return
  const toId = targetImId.value || activeConvId.value.replace('C2C', '')
  const text = inputText.value
  try {
    const res = await sendTextMessage(toId, text)
    messages.value.push(res.data.message)
    inputText.value = ''
    scrollBottom()
    await refreshConvList()
    const realUserId = imIdToUserId(toId)
    if (realUserId) request.post('/notifications/message', { targetUserId: realUserId, text }).catch(() => {})
  } catch (e) { console.error('Send failed:', e) }
}

function scrollBottom() {
  nextTick(() => { if (msgListRef.value) msgListRef.value.scrollTop = msgListRef.value.scrollHeight })
}

onMounted(() => init())
onUnmounted(() => {
  const chat = getTIM()
  if (chat && onMessageHandler) chat.off(TencentCloudChat.EVENT.MESSAGE_RECEIVED, onMessageHandler)
})
</script>

<style scoped>
.chat-page { height: calc(100vh - 90px); }
.chat-error {
  display: flex; align-items: center; justify-content: center;
  height: 100%; background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}
.chat-layout {
  display: flex; height: 100%; background: var(--bg-card);
  border-radius: var(--radius-xl); overflow: hidden;
  box-shadow: var(--shadow-md); border: 1px solid var(--border-soft);
}
.conv-list {
  width: 320px; border-right: 1px solid var(--border-light);
  padding: 20px 16px; overflow-y: auto; flex-shrink: 0;
  background: var(--bg-hover);
}
.conv-list h3 {
  margin-bottom: 16px; font-size: 20px; font-weight: 800;
  color: var(--text-primary);
  background: var(--gradient-primary);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.conv-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 12px; border-radius: var(--radius-md);
  cursor: pointer; margin-bottom: 4px; transition: all 0.25s;
}
.conv-item:hover { background: var(--primary-bg); }
.conv-item.active { background: var(--primary-bg); border-left: 3px solid var(--primary); }
.conv-info { flex: 1; min-width: 0; }
.conv-name { font-weight: 700; font-size: 14px; color: var(--text-primary); }
.conv-last { font-size: 12px; color: var(--text-muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 3px; }
.chat-area { flex: 1; display: flex; flex-direction: column; }
.chat-empty { justify-content: center; align-items: center; }
.chat-header {
  padding: 18px 24px; border-bottom: 1px solid var(--border-light);
  font-weight: 700; font-size: 16px; color: var(--text-primary);
  background: var(--bg-card);
}
.msg-list { flex: 1; overflow-y: auto; padding: 20px 24px; background: var(--bg-page); }
.msg-item { display: flex; gap: 10px; margin-bottom: 16px; }
.msg-item.mine { flex-direction: row-reverse; }
.msg-bubble { max-width: 60%; }
.msg-text {
  padding: 12px 18px; border-radius: 18px; font-size: 14px;
  line-height: 1.6; word-break: break-word;
  background: var(--bg-card); color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}
.msg-item.mine .msg-text {
  background: var(--gradient-primary); color: #fff;
  box-shadow: var(--shadow-primary);
}
.msg-img { max-width: 200px; border-radius: var(--radius-md); }
.chat-input {
  display: flex; gap: 10px; padding: 16px 24px;
  border-top: 1px solid var(--border-light); background: var(--bg-card);
}
.chat-input .el-input { flex: 1; }
</style>
