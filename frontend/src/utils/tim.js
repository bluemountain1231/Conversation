import TencentCloudChat from '@tencentcloud/chat'
import TIMUploadPlugin from 'tim-upload-plugin'
import request from '../api/request'

let chat = null
let isReady = false
let readyPromise = null

export async function initTIM() {
  if (chat && isReady) return chat
  if (readyPromise) return readyPromise

  readyPromise = (async () => {
    const res = await request.get('/im/usersig')
    const { sdkAppId, userId, userSig } = res.data

    if (!chat) {
      chat = TencentCloudChat.create({ SDKAppID: sdkAppId })
      chat.registerPlugin({ 'tim-upload-plugin': TIMUploadPlugin })
      chat.setLogLevel(1)
    }

    await new Promise((resolve, reject) => {
      const onReady = () => {
        isReady = true
        chat.off(TencentCloudChat.EVENT.SDK_READY, onReady)
        resolve()
      }
      const onNotReady = () => {
        chat.off(TencentCloudChat.EVENT.SDK_NOT_READY, onNotReady)
        reject(new Error('SDK not ready'))
      }

      if (isReady) {
        resolve()
        return
      }

      chat.on(TencentCloudChat.EVENT.SDK_READY, onReady)
      chat.on(TencentCloudChat.EVENT.SDK_NOT_READY, onNotReady)

      chat.login({ userID: userId, userSig }).catch(reject)

      setTimeout(() => {
        if (!isReady) {
          chat.off(TencentCloudChat.EVENT.SDK_READY, onReady)
          chat.off(TencentCloudChat.EVENT.SDK_NOT_READY, onNotReady)
          reject(new Error('SDK ready timeout'))
        }
      }, 15000)
    })

    return chat
  })()

  try {
    return await readyPromise
  } catch (e) {
    readyPromise = null
    throw e
  }
}

export function getTIM() {
  return chat
}

export function isSDKReady() {
  return isReady
}

export async function destroyTIM() {
  if (chat) {
    try { await chat.logout() } catch {}
    chat.destroy()
    chat = null
    isReady = false
    readyPromise = null
  }
}

export async function sendTextMessage(toUserId, text) {
  const c = await initTIM()
  const message = c.createTextMessage({
    to: toUserId,
    conversationType: TencentCloudChat.TYPES.CONV_C2C,
    payload: { text }
  })
  return c.sendMessage(message)
}

export async function getConversationList() {
  const c = await initTIM()
  const res = await c.getConversationList()
  return res.data.conversationList || []
}

export async function getMessageList(conversationID, nextReqMessageID) {
  const c = await initTIM()
  const res = await c.getMessageList({ conversationID, nextReqMessageID, count: 15 })
  return res.data
}

export { TencentCloudChat }
