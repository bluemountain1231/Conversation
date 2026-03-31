const api = require('../../utils/api')
const auth = require('../../utils/auth')

Page({
  data: {
    mode: 'login',
    username: '',
    email: '',
    phone: '',
    password: '',
    loading: false
  },

  onLoad() {
    wx.setNavigationBarTitle({ title: '登录' })
  },

  switchMode(e) {
    const target = e.currentTarget.dataset.mode
    if (this.data.mode === target) return
    const mode = target
    this.setData({
      mode,
      username: '',
      email: '',
      phone: '',
      password: '',
      loading: false
    })
    wx.setNavigationBarTitle({
      title: mode === 'login' ? '登录' : '注册'
    })
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onEmailInput(e) {
    this.setData({ email: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  },

  validate() {
    const { mode, username, email, password } = this.data
    if (mode === 'register' && !username.trim()) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return false
    }
    if (!email.trim()) {
      wx.showToast({ title: '请输入邮箱', icon: 'none' })
      return false
    }
    if (!this.validateEmail(email.trim())) {
      wx.showToast({ title: '邮箱格式不正确', icon: 'none' })
      return false
    }
    if (!password.trim()) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return false
    }
    if (password.trim().length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' })
      return false
    }
    return true
  },

  async onSubmit() {
    if (!this.validate()) return
    if (this.data.loading) return

    const { mode, username, email, phone, password } = this.data
    this.setData({ loading: true })

    try {
      let res
      if (mode === 'login') {
        res = await api.login({ email: email.trim(), password: password.trim() })
      } else {
        const payload = {
          username: username.trim(),
          email: email.trim(),
          password: password.trim()
        }
        if (phone.trim()) payload.phone = phone.trim()
        res = await api.register(payload)
      }

      auth.setToken(res.data.token)
      auth.setUserInfo(res.data.user)
      const app = getApp()
      app.globalData.userInfo = res.data.user

      wx.showToast({
        title: mode === 'login' ? '登录成功' : '注册成功',
        icon: 'success'
      })

      setTimeout(() => {
        const pages = getCurrentPages()
        if (pages.length > 1) {
          wx.navigateBack()
        } else {
          wx.switchTab({ url: '/pages/index/index' })
        }
      }, 800)
    } catch (err) {
      this.setData({ loading: false })
    }
  }
})
