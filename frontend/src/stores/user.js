import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUser(user) {
    userInfo.value = user
  }

  async function loadUser() {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      if (res.code === 200) {
        userInfo.value = res.data
      }
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, isLoggedIn, setToken, setUser, loadUser, logout }
})
