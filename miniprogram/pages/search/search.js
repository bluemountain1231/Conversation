const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

const ALL_PREVIEW_LIMIT = 3

Page({
  data: {
    keyword: '',
    activeTab: 'all',
    tabs: [
      { key: 'all', label: '全部' },
      { key: 'post', label: '帖子' },
      { key: 'circle', label: '圈子' },
      { key: 'user', label: '用户' }
    ],
    results: {
      posts: { content: [], totalElements: 0 },
      circles: { content: [], totalElements: 0 },
      users: { content: [], totalElements: 0 }
    },
    loading: false,
    page: 0,
    size: 10,
    hasMore: true,
    searched: false,
    searchTimer: null,
    hotKeywords: ['摄影', '读书', '运动', '音乐', '旅行', '美食']
  },

  onLoad() {
    this.setData({ autoFocus: true })
  },

  onInput(e) {
    const keyword = (e.detail.value || '').trim()
    this.setData({ keyword })
    if (this.data.searchTimer) clearTimeout(this.data.searchTimer)
    if (!keyword) {
      this.resetResults()
      return
    }
    const timer = setTimeout(() => this.doSearchAll(true), 400)
    this.setData({ searchTimer: timer })
  },

  onConfirm() {
    if (this.data.searchTimer) clearTimeout(this.data.searchTimer)
    if (!this.data.keyword) {
      wx.showToast({ title: '请输入搜索关键词', icon: 'none' })
      return
    }
    this.doSearchAll(true)
  },

  onClear() {
    if (this.data.searchTimer) clearTimeout(this.data.searchTimer)
    this.setData({ keyword: '' })
    this.resetResults()
  },

  resetResults() {
    this.setData({
      searched: false,
      page: 0,
      hasMore: true,
      results: {
        posts: { content: [], totalElements: 0 },
        circles: { content: [], totalElements: 0 },
        users: { content: [], totalElements: 0 }
      }
    })
  },

  onHotKeywordTap(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ keyword })
    this.doSearchAll(true)
  },

  async doSearchAll(reset) {
    const keyword = this.data.keyword.trim()
    if (!keyword) return
    const tab = this.data.activeTab
    const page = reset ? 0 : this.data.page
    if (!reset && !this.data.hasMore) return

    this.setData({ loading: true, searched: true })
    try {
      const res = await api.search(keyword, tab, page, this.data.size)
      const data = res.data || res

      const posts = this.normalizeList(data.posts)
      const circles = this.normalizeList(data.circles)
      const users = this.normalizeList(data.users)

      circles.content = circles.content.map(c => ({
        ...c,
        avatarUrl: c.avatar ? util.getImageUrl(c.avatar) : ''
      }))
      users.content = users.content.map(u => ({
        ...u,
        avatarUrl: (u.avatar || u.avatarUrl) ? util.getImageUrl(u.avatar || u.avatarUrl) : ''
      }))

      const totalPages = data.totalPages || 0
      const hasMore = page + 1 < totalPages

      if (reset) {
        this.setData({
          results: { posts, circles, users },
          page: page + 1,
          hasMore,
          loading: false
        })
      } else {
        const prev = this.data.results
        this.setData({
          results: {
            posts: {
              content: tab === 'all' || tab === 'post' ? [...prev.posts.content, ...posts.content] : prev.posts.content,
              totalElements: posts.totalElements || prev.posts.totalElements
            },
            circles: {
              content: tab === 'all' || tab === 'circle' ? [...prev.circles.content, ...circles.content] : prev.circles.content,
              totalElements: circles.totalElements || prev.circles.totalElements
            },
            users: {
              content: tab === 'all' || tab === 'user' ? [...prev.users.content, ...users.content] : prev.users.content,
              totalElements: users.totalElements || prev.users.totalElements
            }
          },
          page: page + 1,
          hasMore,
          loading: false
        })
      }
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  normalizeList(raw) {
    if (!raw) return { content: [], totalElements: 0 }
    if (Array.isArray(raw)) return { content: raw, totalElements: raw.length }
    return {
      content: raw.content || [],
      totalElements: raw.totalElements || 0
    }
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab
    if (tab === this.data.activeTab) return
    this.setData({ activeTab: tab, page: 0, hasMore: true })
    if (this.data.keyword && this.data.searched) {
      this.doSearchAll(true)
    }
  },

  onViewMore(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab, page: 0, hasMore: true })
    this.doSearchAll(true)
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
    const content = this.data.results.posts.content.map(p =>
      p.id === detail.id ? { ...p, liked: detail.liked, likeCount: detail.likeCount } : p
    )
    this.setData({ 'results.posts.content': content })
  },

  onFavorite(e) {
    const detail = e.detail
    if (!detail) return
    const content = this.data.results.posts.content.map(p =>
      p.id === detail.id ? { ...p, favorited: detail.favorited, favoriteCount: detail.favoriteCount } : p
    )
    this.setData({ 'results.posts.content': content })
  },

  onCircleTap(e) {
    const circle = e.detail?.circle
    const id = circle?.id || e.detail?.id
    if (id) {
      wx.navigateTo({ url: `/pages/circle-detail/circle-detail?id=${id}` })
    }
  },

  onUserTap(e) {
    const user = e.currentTarget.dataset.user
    const uid = user?.id || user?.userId
    if (uid) {
      wx.navigateTo({ url: `/pages/user-profile/user-profile?id=${uid}` })
    }
  },

  async onFollowUser(e) {
    if (!auth.requireAuth()) return
    const user = e.currentTarget.dataset.user
    const uid = user?.id || user?.userId
    if (!uid) return
    try {
      await api.followUser(uid)
      const content = this.data.results.users.content.map(u =>
        (u.id || u.userId) === uid ? { ...u, followed: !u.followed } : u
      )
      this.setData({ 'results.users.content': content })
      wx.showToast({ title: user.followed ? '已取消关注' : '已关注', icon: 'success' })
    } catch (e) { /* handled by api */ }
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && this.data.keyword && this.data.activeTab !== 'all') {
      this.doSearchAll(false)
    }
  },

  hasAnyResults() {
    const r = this.data.results
    return r.posts.content.length > 0 || r.circles.content.length > 0 || r.users.content.length > 0
  }
})
