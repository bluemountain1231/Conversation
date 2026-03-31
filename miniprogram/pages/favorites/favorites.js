const api = require('../../utils/api')
const auth = require('../../utils/auth')

Page({
  data: {
    posts: [],
    loading: false,
    hasMore: true,
    page: 0,
    size: 10
  },

  onShow() {
    if (!auth.isLoggedIn()) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.loadFavorites(true)
  },

  onPullDownRefresh() {
    this.loadFavorites(true).then(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadFavorites(false)
    }
  },

  loadFavorites(reset) {
    if (!auth.isLoggedIn()) return Promise.resolve()
    const page = reset ? 0 : this.data.page
    if (!reset && !this.data.hasMore) return Promise.resolve()
    this.setData({ loading: true })
    return api.getFavorites(page, this.data.size)
      .then(res => {
        const data = res.data || {}
        const list = data.content || (Array.isArray(data) ? data : [])
        const totalPages = data.totalPages || 0
        const hasMore = page + 1 < totalPages
        this.setData({
          posts: reset ? list : [...this.data.posts, ...list],
          page: page + 1,
          hasMore,
          loading: false
        })
      })
      .catch(() => this.setData({ loading: false }))
  },

  onPostTap(e) {
    const id = e.detail?.id
    if (id) wx.navigateTo({ url: `/pages/post-detail/post-detail?id=${id}` })
  },

  onPostLike(e) {
    const { id, liked, likeCount } = e.detail || {}
    if (!id) return
    const posts = this.data.posts.map(p =>
      p.id === id ? { ...p, liked, likeCount } : p
    )
    this.setData({ posts })
  },

  onPostFavorite(e) {
    const { id, favorited } = e.detail || {}
    if (id && !favorited) {
      const posts = this.data.posts.filter(p => p.id !== id)
      this.setData({ posts })
    }
  }
})
