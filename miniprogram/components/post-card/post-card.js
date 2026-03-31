const util = require('../../utils/util')
const api = require('../../utils/api')
const auth = require('../../utils/auth')

Component({
  properties: {
    post: {
      type: Object,
      value: {}
    }
  },

  data: {
    contentPreview: '',
    displayImages: [],
    avatarUrl: '',
    timeText: '',
    isPopular: false,
    likeAnimating: false,
    favAnimating: false
  },

  observers: {
    'post': function (post) {
      if (!post || !post.id) return
      let images = post.images || []
      if (typeof images === 'string') {
        try { images = JSON.parse(images) } catch (e) { images = images.split(',').filter(Boolean) }
      }
      if (!Array.isArray(images)) images = []
      this.setData({
        contentPreview: util.truncate(post.content, 120),
        displayImages: images.slice(0, 9).map(img => util.getImageUrl(img)),
        avatarUrl: util.getImageUrl(post.authorAvatar) || '',
        timeText: util.formatTime(post.createdAt),
        isPopular: (post.likeCount || 0) >= 50
      })
    }
  },

  methods: {
    onTap() {
      this.triggerEvent('tap', { id: this.data.post.id })
    },

    async onLike() {
      if (!auth.isLoggedIn()) {
        wx.navigateTo({ url: '/pages/login/login' })
        return
      }
      const post = this.data.post
      if (!post || !post.id) return
      const liked = !post.liked
      const likeCount = Math.max(0, (post.likeCount || 0) + (liked ? 1 : -1))
      this.setData({
        'post.liked': liked,
        'post.likeCount': likeCount,
        likeAnimating: true
      })
      setTimeout(() => this.setData({ likeAnimating: false }), 400)
      this.triggerEvent('like', { id: post.id, liked, likeCount })
      try {
        await api.likePost(post.id)
      } catch (e) {
        this.setData({ 'post.liked': !liked, 'post.likeCount': post.likeCount })
        this.triggerEvent('like', { id: post.id, liked: !liked, likeCount: post.likeCount })
      }
    },

    async onFavorite() {
      if (!auth.isLoggedIn()) {
        wx.navigateTo({ url: '/pages/login/login' })
        return
      }
      const post = this.data.post
      if (!post || !post.id) return
      const favorited = !post.favorited
      const favoriteCount = Math.max(0, (post.favoriteCount || 0) + (favorited ? 1 : -1))
      this.setData({
        'post.favorited': favorited,
        'post.favoriteCount': favoriteCount,
        favAnimating: true
      })
      setTimeout(() => this.setData({ favAnimating: false }), 400)
      this.triggerEvent('favorite', { id: post.id, favorited, favoriteCount })
      try {
        await api.favoritePost(post.id)
      } catch (e) {
        this.setData({ 'post.favorited': !favorited, 'post.favoriteCount': post.favoriteCount })
        this.triggerEvent('favorite', { id: post.id, favorited: !favorited, favoriteCount: post.favoriteCount })
      }
    },

    onMore() {},

    onShare() {
      // placeholder for share functionality
    },

    onAuthorTap() {
      const post = this.data.post
      if (post && post.userId) {
        wx.navigateTo({ url: `/pages/user-profile/user-profile?id=${post.userId}` })
      }
    },

    previewImage(e) {
      const { src } = e.currentTarget.dataset
      wx.previewImage({ current: src, urls: this.data.displayImages })
    }
  }
})
