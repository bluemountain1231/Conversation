const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    avatar: '',
    avatarUrl: '',
    username: '',
    bio: '',
    loading: false,
    saving: false
  },

  onLoad() {
    if (!auth.requireAuth(() => {})) return
    this.loadUser()
  },

  loadUser() {
    this.setData({ loading: true })
    api.getMe()
      .then(res => {
        const u = res.data || {}
        const avatar = u.avatar || ''
        this.setData({
          avatar,
          avatarUrl: avatar ? util.getImageUrl(avatar) : '',
          username: u.username || '',
          bio: u.bio || '',
          loading: false
        })
      })
      .catch(() => this.setData({ loading: false }))
  },

  onAvatarTap() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const file = res.tempFiles[0]
        if (file.size > 5 * 1024 * 1024) {
          wx.showToast({ title: '图片不能超过5MB', icon: 'none' })
          return
        }
        const path = file.tempFilePath
        wx.showLoading({ title: '上传中...', mask: true })
        api.uploadImage(path)
          .then(upRes => {
            wx.hideLoading()
            const url = upRes.data?.url || upRes.data
            if (url) {
              this.setData({ avatar: url, avatarUrl: util.getImageUrl(url) })
              wx.showToast({ title: '头像已更新', icon: 'success' })
            } else {
              wx.showToast({ title: '上传失败：服务器未返回地址', icon: 'none' })
            }
          })
          .catch((err) => {
            wx.hideLoading()
            const msg = (err && err.message) || '上传失败'
            wx.showToast({ title: msg, icon: 'none' })
          })
      }
    })
  },

  onUsernameInput(e) {
    this.setData({ username: (e.detail.value || '').trim() })
  },

  onBioInput(e) {
    this.setData({ bio: (e.detail.value || '').trim() })
  },

  validate() {
    const { username } = this.data
    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return false
    }
    if (username.length < 2) {
      wx.showToast({ title: '用户名至少2个字符', icon: 'none' })
      return false
    }
    if (username.length > 20) {
      wx.showToast({ title: '用户名最多20个字符', icon: 'none' })
      return false
    }
    return true
  },

  onSave() {
    if (!this.validate() || this.data.saving) return
    this.setData({ saving: true })
    const { avatar, username, bio } = this.data
    api.updateMe({ avatar, username, bio })
      .then(res => {
        const user = res.data || { avatar, username, bio }
        auth.setUserInfo(user)
        const app = getApp()
        if (app && typeof app.loadUserInfo === 'function') {
          app.loadUserInfo()
        }
        wx.showToast({ title: '保存成功', icon: 'success' })
        wx.navigateBack()
      })
      .catch(() => {})
      .finally(() => this.setData({ saving: false }))
  }
})
