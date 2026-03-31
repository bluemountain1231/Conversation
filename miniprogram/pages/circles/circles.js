const api = require('../../utils/api')
const util = require('../../utils/util')

Page({
  data: {
    categories: [
      { name: '摄影', icon: '📷', bg: 'linear-gradient(135deg, #1a1a2e, #16213e)' },
      { name: '科技', icon: '🤖', bg: 'linear-gradient(135deg, #0f3460, #16213e)' },
      { name: '旅行', icon: '✈️', bg: 'linear-gradient(135deg, #e8d5b7, #f0c27f)' },
      { name: '设计', icon: '🎨', bg: 'linear-gradient(135deg, #667eea, #764ba2)' },
      { name: '音乐', icon: '🎵', bg: 'linear-gradient(135deg, #a18cd1, #fbc2eb)' },
      { name: '美食', icon: '🍽️', bg: 'linear-gradient(135deg, #e94560, #533483)' },
    ],
    circles: [],
    recommendedCircles: [],
    page: 0,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadCircles()
    this.loadRecommendedCircles()
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 })
    }
  },

  async onPullDownRefresh() {
    this.setData({ page: 0, hasMore: true, circles: [] })
    await Promise.all([this.loadCircles(), this.loadRecommendedCircles()])
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadCircles()
    }
  },

  async loadCircles() {
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      const res = await api.getCircles(this.data.page, 10)
      const newCircles = res.data.content || res.data || []
      this.setData({
        circles: this.data.page === 0 ? newCircles : [...this.data.circles, ...newCircles],
        page: this.data.page + 1,
        hasMore: newCircles.length === 10
      })
    } catch (e) {}
    this.setData({ loading: false })
  },

  async loadRecommendedCircles() {
    const token = wx.getStorageSync('token')
    if (!token) return
    try {
      const res = await api.getRecommendCircles(6)
      const list = Array.isArray(res.data) ? res.data : []
      if (list.length > 0) {
        this.setData({ circles: list })
      }
    } catch (e) {}
  },

  onSearch() {
    wx.navigateTo({ url: '/pages/search/search' })
  },

  onViewAllCategories() {
    wx.navigateTo({ url: '/pages/search/search' })
  },

  onCategoryTap(e) {
    const { name } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/search/search?keyword=${name}` })
  },

  onCircleTap(e) {
    const { id } = e.detail
    if (id) wx.navigateTo({ url: `/pages/circle-detail/circle-detail?id=${id}` })
  },

  onJoinCircle(e) {
    const { id } = e.detail
    if (!id) return
    const circles = this.data.circles.map(c =>
      c.id === id ? { ...c, joined: true, memberCount: (c.memberCount || 0) + 1 } : c
    )
    this.setData({ circles })
  }
})
