import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../api/request'

export const useNotificationStore = defineStore('notification', () => {
  const unreadCount = ref(0)
  const notifications = ref([])

  async function fetchUnreadCount() {
    try {
      const res = await request.get('/notifications/unread-count')
      unreadCount.value = res.data || 0
    } catch {}
  }

  function addNotification(n) {
    notifications.value.unshift(n)
    unreadCount.value++
  }

  async function markAllRead() {
    try {
      await request.put('/notifications/read')
      unreadCount.value = 0
    } catch {}
  }

  return { unreadCount, notifications, fetchUnreadCount, addNotification, markAllRead }
})
