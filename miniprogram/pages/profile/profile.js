const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    user: null,
    posts: [],
    likedPosts: [],
    savedPosts: [],
    loading: false,
    postsLoading: false,
    likedLoading: false,
    savedLoading: false,
    hasMore: true,
    page: 0,
    size: 20,
    loggedIn: false,
    activeTab: 0
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 3 })
    }
    if (!auth.isLoggedIn()) {
      this.setData({ loggedIn: false, user: null, posts: [], likedPosts: [], savedPosts: [] })
      return
    }
    this.setData({ loggedIn: true })
    this.loadProfile()
  },

  loadProfile() {
    if (!auth.isLoggedIn()) return
    this.setData({ loading: true })
    api.getMe()
      .then(res => {
        const user = res.data
        user.avatarUrl = user.avatar ? util.getImageUrl(user.avatar) : ''
        auth.setUserInfo(user)
        const app = getApp()
        if (app) app.globalData.userInfo = user
        this.setData({ user, loading: false })
        this.loadUserPosts(true)
      })
      .catch(() => this.setData({ loading: false }))
  },

  processPostsForGrid(posts) {
    return posts.map(p => {
      let images = p.images || []
      if (typeof images === 'string') {
        try { images = JSON.parse(images) } catch (e) { images = images.split(',').filter(Boolean) }
      }
      if (!Array.isArray(images)) images = []
      return {
        ...p,
        _coverImage: images.length > 0 ? util.getImageUrl(images[0]) : '',
        _preview: util.truncate(p.content, 60)
      }
    })
  },

  loadUserPosts(reset) {
    if (!auth.isLoggedIn() || !this.data.user) return
    const page = reset ? 0 : this.data.page
    if (!reset && !this.data.hasMore) return
    this.setData({ postsLoading: true })
    api.getUserPosts(this.data.user.id, page, this.data.size)
      .then(res => {
        const content = res.data?.content || res.data?.posts || []
        const totalPages = res.data?.totalPages || 0
        const hasMore = page + 1 < totalPages
        const processed = this.processPostsForGrid(content)
        this.setData({
          posts: reset ? processed : [...this.data.posts, ...processed],
          page: page + 1,
          hasMore,
          postsLoading: false
        })
      })
      .catch(() => this.setData({ postsLoading: false }))
  },

  loadSavedPosts() {
    if (this.data.savedPosts.length > 0) return
    this.setData({ savedLoading: true })
    api.getFavorites(0, 20)
      .then(res => {
        const content = res.data?.content || res.data || []
        this.setData({
          savedPosts: this.processPostsForGrid(content),
          savedLoading: false
        })
      })
      .catch(() => this.setData({ savedLoading: false }))
  },

  switchTab(e) {
    const index = parseInt(e.currentTarget.dataset.index, 10)
    this.setData({ activeTab: index })
    if (index === 2 && this.data.savedPosts.length === 0) {
      this.loadSavedPosts()
    }
  },

  onReachBottom() {
    if (this.data.activeTab === 0 && this.data.hasMore && !this.data.postsLoading && this.data.user) {
      this.loadUserPosts(false)
    }
  },

  onLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  onEditProfile() {
    wx.navigateTo({ url: '/pages/profile-edit/profile-edit' })
  },

  onFavorites() {
    auth.requireAuth(() => {
      wx.navigateTo({ url: '/pages/favorites/favorites' })
    })
  },

  onLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出吗？',
      success: (res) => {
        if (res.confirm) {
          auth.logout()
          this.setData({ user: null, posts: [], likedPosts: [], savedPosts: [], loggedIn: false })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  },

  onPostTap(e) {
    const id = e.currentTarget.dataset.id
    if (id) wx.navigateTo({ url: `/pages/post-detail/post-detail?id=${id}` })
  }
})
