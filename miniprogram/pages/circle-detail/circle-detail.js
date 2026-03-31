const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    circleId: null,
    circle: null,
    posts: [],
    members: [],
    page: 0,
    size: 10,
    hasMore: true,
    loading: false,
    circleLoading: true,
    joinLoading: false,
    error: false
  },

  onLoad(options) {
    const id = options.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1500)
      return
    }
    this.setData({ circleId: id })
    this.initData()
  },

  async initData() {
    await Promise.all([
      this.loadCircle(),
      this.loadPosts(true),
      this.loadMembers()
    ])
  },

  async loadCircle() {
    const id = this.data.circleId
    if (!id) return
    this.setData({ circleLoading: true, error: false })
    try {
      const res = await api.getCircle(id)
      const circle = res.data || res
      this.setData({
        circle: {
          ...circle,
          avatarUrl: circle.avatar ? util.getImageUrl(circle.avatar) : '',
          memberCountText: util.formatNumber(circle.memberCount || 0),
          postCountText: util.formatNumber(circle.postCount || 0)
        },
        circleLoading: false
      })
      wx.setNavigationBarTitle({ title: circle.name || '圈子详情' })
    } catch (e) {
      this.setData({ circleLoading: false, error: true })
    }
  },

  async loadPosts(reset) {
    const id = this.data.circleId
    if (!id) return
    const page = reset ? 0 : this.data.page
    if (!reset && !this.data.hasMore) return
    this.setData({ loading: true })
    try {
      const res = await api.getCirclePosts(id, page, this.data.size)
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

  async loadMembers() {
    const id = this.data.circleId
    if (!id) return
    try {
      const res = await api.getCircleMembers(id, 0, 8)
      const data = res.data || res
      const raw = data.content || data.members || []
      const members = raw.map(m => ({
        ...m,
        avatarUrl: (m.avatar || m.avatarUrl) ? util.getImageUrl(m.avatar || m.avatarUrl) : '',
        userId: m.userId || m.id
      }))
      this.setData({ members })
    } catch (e) { /* silently fail, members are supplementary */ }
  },

  async onJoinToggle() {
    if (!auth.requireAuth()) return
    const circle = this.data.circle
    if (!circle || !circle.id || this.data.joinLoading) return
    const joined = circle.joined
    this.setData({ joinLoading: true })
    try {
      if (joined) {
        await api.leaveCircle(circle.id)
      } else {
        await api.joinCircle(circle.id)
      }
      const newCount = Math.max(0, (circle.memberCount || 0) + (joined ? -1 : 1))
      this.setData({
        'circle.joined': !joined,
        'circle.memberCount': newCount,
        'circle.memberCountText': util.formatNumber(newCount),
        joinLoading: false
      })
      wx.showToast({ title: joined ? '已退出圈子' : '已加入圈子', icon: 'success' })
      if (!joined) this.loadMembers()
    } catch (e) {
      this.setData({ joinLoading: false })
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

  onMemberTap(e) {
    const member = e.currentTarget.dataset.member
    const uid = member?.userId || member?.id
    if (uid) {
      wx.navigateTo({ url: `/pages/user-profile/user-profile?id=${uid}` })
    }
  },

  async onPullDownRefresh() {
    this.setData({ page: 0, hasMore: true })
    await Promise.all([
      this.loadCircle(),
      this.loadPosts(true),
      this.loadMembers()
    ])
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadPosts(false)
    }
  }
})
