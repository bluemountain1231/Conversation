const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    userId: null,
    user: null,
    stats: { postCount: 0, followingCount: 0, followerCount: 0 },
    posts: [],
    page: 0,
    size: 10,
    hasMore: true,
    loading: false,
    userLoading: true,
    followLoading: false,
    isSelf: false,
    error: false
  },

  onLoad(options) {
    const id = options.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1500)
      return
    }
    const myInfo = auth.getUserInfo()
    const isSelf = myInfo && (String(myInfo.id) === String(id))
    if (isSelf) {
      wx.switchTab({ url: '/pages/profile/profile' })
      return
    }
    this.setData({ userId: id, isSelf })
    this.initData()
  },

  async initData() {
    await Promise.all([
      this.loadUser(),
      this.loadStats(),
      this.loadPosts(true)
    ])
  },

  async loadUser() {
    const id = this.data.userId
    if (!id) return
    this.setData({ userLoading: true, error: false })
    try {
      const res = await api.getUser(id)
      const user = res.data || res
      this.setData({
        user: {
          ...user,
          avatarUrl: (user.avatar || user.avatarUrl) ? util.getImageUrl(user.avatar || user.avatarUrl) : ''
        },
        userLoading: false
      })
      wx.setNavigationBarTitle({ title: user.username || user.name || '用户主页' })
    } catch (e) {
      this.setData({ userLoading: false, error: true })
    }
  },

  async loadStats() {
    const id = this.data.userId
    if (!id) return
    try {
      const res = await api.getUserStats(id)
      const stats = res.data || res
      this.setData({
        stats: {
          postCount: stats.postCount || 0,
          followingCount: stats.followingCount || 0,
          followerCount: stats.followerCount || 0
        }
      })
    } catch (e) { /* stats are supplementary */ }
  },

  async loadPosts(reset) {
    const id = this.data.userId
    if (!id) return
    const page = reset ? 0 : this.data.page
    if (!reset && !this.data.hasMore) return
    this.setData({ loading: true })
    try {
      const res = await api.getUserPosts(id, page, this.data.size)
      const data = res.data || res
      const content = data.content || data.posts || []
      const totalPages = data.totalPages || 0
      const hasMore = page + 1 < totalPages
      this.setData({
        posts: reset ? content : [...this.data.posts, ...content],
        page: page + 1,
        hasMore,
        loading: false
      })
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  async onFollow() {
    if (!auth.requireAuth()) return
    const user = this.data.user
    if (!user || this.data.followLoading) return
    const uid = user.id || user.userId
    if (!uid) return

    const wasFollowed = user.followed
    this.setData({ followLoading: true })
    try {
      await api.followUser(uid)
      const newFollowerCount = Math.max(0, (this.data.stats.followerCount || 0) + (wasFollowed ? -1 : 1))
      this.setData({
        'user.followed': !wasFollowed,
        'stats.followerCount': newFollowerCount,
        followLoading: false
      })
      wx.showToast({ title: wasFollowed ? '已取消关注' : '关注成功', icon: 'success' })
    } catch (e) {
      this.setData({ followLoading: false })
    }
  },

  onPostTap(e) {
    const id = e.detail?.id || e.detail?.post?.id
    if (id) {
      wx.navigateTo({ url: `/pages/post-detail/post-detail?id=${id}` })
    }
  },

  onLike(e) {
    const detail = e.detail
    if (!detail) return
    const posts = this.data.posts.map(p =>
      p.id === detail.id ? { ...p, liked: detail.liked, likeCount: detail.likeCount } : p
    )
    this.setData({ posts })
  },

  onFavorite(e) {
    const detail = e.detail
    if (!detail) return
    const posts = this.data.posts.map(p =>
      p.id === detail.id ? { ...p, favorited: detail.favorited, favoriteCount: detail.favoriteCount } : p
    )
    this.setData({ posts })
  },

  async onPullDownRefresh() {
    this.setData({ page: 0, hasMore: true })
    await Promise.all([
      this.loadUser(),
      this.loadStats(),
      this.loadPosts(true)
    ])
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadPosts(false)
    }
  }
})
