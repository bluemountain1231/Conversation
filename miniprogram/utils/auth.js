const getToken = () => wx.getStorageSync('token')
const setToken = (token) => wx.setStorageSync('token', token)
const removeToken = () => wx.removeStorageSync('token')

const getUserInfo = () => wx.getStorageSync('userInfo')
const setUserInfo = (info) => wx.setStorageSync('userInfo', info)
const removeUserInfo = () => wx.removeStorageSync('userInfo')

const isLoggedIn = () => !!getToken()

const logout = () => {
  removeToken()
  removeUserInfo()
  const app = getApp()
  app.globalData.userInfo = null
}

const requireAuth = (callback) => {
  if (isLoggedIn()) {
    callback && callback()
    return true
  }
  wx.navigateTo({ url: '/pages/login/login' })
  return false
}

module.exports = {
  getToken, setToken, removeToken,
  getUserInfo, setUserInfo, removeUserInfo,
  isLoggedIn, logout, requireAuth
}
