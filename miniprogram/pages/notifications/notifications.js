const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    notifications: [],
    loading: false,
    hasMore: true,
    page: 0,
    size: 20,
    loggedIn: false
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 })
    }
    if (!auth.isLoggedIn()) {
      this.setData({ loggedIn: false, notifications: [] })
      return
    }
    this.setData({ loggedIn: true })
    this.loadNotifications(true)
  },

  onPullDownRefresh() {
    if (!auth.isLoggedIn()) {
      wx.stopPullDownRefresh()
      return
    }
    this.loadNotifications(true).then(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && auth.isLoggedIn()) {
      this.loadNotifications(false)
    }
  },

  loadNotifications(reset) {
    if (!auth.isLoggedIn()) return Promise.resolve()
    const page = reset ? 0 : this.data.page
    if (!reset && page >= 1 && !this.data.hasMore) return Promise.resolve()
    this.setData({ loading: true })
    return api.getNotifications(page, this.data.size)
      .then(res => {
        const raw = res.data?.content || []
        const list = raw.map(n => ({ ...n, createdAt: util.formatTime(n.createdAt) }))
        const totalPages = res.data?.totalPages || 0
        const hasMore = page + 1 < totalPages
        this.setData({
          notifications: reset ? list : [...this.data.notifications, ...list],
          page: page + 1,
          hasMore,
          loading: false
        })
      })
      .catch(() => this.setData({ loading: false }))
  },

  markAllRead() {
    if (!auth.isLoggedIn()) {
      auth.requireAuth(() => {})
      return
    }
    api.markAllRead().then(() => {
      wx.showToast({ title: '已全部已读', icon: 'success' })
      this.loadNotifications(true)
    })
  },

  onLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  onNotificationTap(e) {
    const item = e.currentTarget.dataset.item
    if (!item) return
    const type = (item.type || '').toUpperCase()
    const relatedId = item.relatedId
    const fromUserId = item.fromUserId

    if (type === 'LIKE' || type === 'COMMENT' || type === 'FAVORITE') {
      if (relatedId) {
        wx.navigateTo({ url: `/pages/post-detail/post-detail?id=${relatedId}` })
      }
    } else if (type === 'FOLLOW' || type === 'MESSAGE') {
      const uid = fromUserId || relatedId
      if (uid) {
        wx.navigateTo({ url: `/pages/user-profile/user-profile?id=${uid}` })
      }
    }
  },

  formatTime(dateStr) {
    return util.formatTime(dateStr)
  }
})
