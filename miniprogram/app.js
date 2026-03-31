App({
  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.loadUserInfo()
    }
  },

  globalData: {
    userInfo: null,
    baseUrl: 'https://frp-six.com:36605'
  },

  async loadUserInfo() {
    const api = require('./utils/api')
    try {
      const res = await api.getMe()
      this.globalData.userInfo = res.data
      wx.setStorageSync('userInfo', res.data)
    } catch (e) {
      wx.removeStorageSync('token')
      wx.removeStorageSync('userInfo')
    }
  },

  checkLogin() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.navigateTo({ url: '/pages/login/login' })
      return false
    }
    return true
  }
})
