const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    id: null,
    post: null,
    comments: [],
    loading: true,
    commentText: '',
    commentLoading: false,
    replyTo: null,
    inputFocus: false,
    currentUserId: null
  },

  onLoad(options) {
    if (!options.id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 500)
      return
    }
    const userInfo = auth.getUserInfo()
    this.setData({
      id: options.id,
      currentUserId: userInfo ? userInfo.id : null
    })
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      const [postRes, commentsRes] = await Promise.all([
        api.getPost(this.data.id),
        api.getComments(this.data.id)
      ])
      const post = this.processPost(postRes.data)
      const comments = (commentsRes.data || []).filter(c => !c.parentId)
      this.setData({ post, comments, loading: false })
    } catch (err) {
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  processPost(post) {
    if (!post) return null
    return {
      ...post,
      authorAvatarUrl: post.authorAvatar ? util.getImageUrl(post.authorAvatar) : '',
      displayTime: util.formatTime(post.createdAt),
      displayLikeCount: util.formatNumber(post.likeCount),
      displayFavoriteCount: util.formatNumber(post.favoriteCount),
      displayCommentCount: util.formatNumber(post.commentCount),
      processedImages: (() => {
        let imgs = post.images || []
        if (typeof imgs === 'string') {
          try { imgs = JSON.parse(imgs) } catch (e) { imgs = imgs.split(',').filter(Boolean) }
        }
        return Array.isArray(imgs) ? imgs.map(img => util.getImageUrl(img)) : []
      })(),
      authorId: post.userId,
      authorFollowed: post.authorFollowed || false
    }
  },

  async onLike() {
    if (!auth.requireAuth()) return
    const { post } = this.data
    if (!post) return

    const liked = !post.liked
    this.setData({
      'post.liked': liked,
      'post.likeCount': post.likeCount + (liked ? 1 : -1),
      'post.displayLikeCount': util.formatNumber(post.likeCount + (liked ? 1 : -1))
    })

    try {
      await api.likePost(post.id)
    } catch (err) {
      this.setData({
        'post.liked': !liked,
        'post.likeCount': post.likeCount,
        'post.displayLikeCount': util.formatNumber(post.likeCount)
      })
    }
  },

  async onFavorite() {
    if (!auth.requireAuth()) return
    const { post } = this.data
    if (!post) return

    const favorited = !post.favorited
    this.setData({
      'post.favorited': favorited,
      'post.favoriteCount': post.favoriteCount + (favorited ? 1 : -1),
      'post.displayFavoriteCount': util.formatNumber(post.favoriteCount + (favorited ? 1 : -1))
    })

    try {
      await api.favoritePost(post.id)
    } catch (err) {
      this.setData({
        'post.favorited': !favorited,
        'post.favoriteCount': post.favoriteCount,
        'post.displayFavoriteCount': util.formatNumber(post.favoriteCount)
      })
    }
  },

  onCommentInput(e) {
    this.setData({ commentText: e.detail.value })
  },

  onCommentFocus() {
    this.setData({ inputFocus: true })
  },

  onCommentBlur() {
    this.setData({ inputFocus: false })
  },

  onReply(e) {
    const { id, authorName } = e.detail
    if (!id) return
    this.setData({
      replyTo: {
        id,
        name: authorName || '匿名'
      },
      inputFocus: true
    })
  },

  cancelReply() {
    this.setData({ replyTo: null })
  },

  async sendComment() {
    if (!auth.requireAuth()) return
    const { commentText, replyTo, id } = this.data
    if (!commentText.trim()) {
      wx.showToast({ title: '请输入评论内容', icon: 'none' })
      return
    }
    if (this.data.commentLoading) return

    this.setData({ commentLoading: true })
    wx.showLoading({ title: '发送中...', mask: true })

    try {
      const data = { content: commentText.trim() }
      if (replyTo) data.parentId = replyTo.id
      await api.createComment(id, data)

      wx.hideLoading()
      wx.showToast({ title: '评论成功', icon: 'success' })
      this.setData({ commentText: '', replyTo: null, commentLoading: false })

      const commentsRes = await api.getComments(id)
      const comments = (commentsRes.data || []).filter(c => !c.parentId)
      this.setData({
        comments,
        'post.commentCount': (this.data.post.commentCount || 0) + 1,
        'post.displayCommentCount': util.formatNumber((this.data.post.commentCount || 0) + 1)
      })
    } catch (err) {
      wx.hideLoading()
      this.setData({ commentLoading: false })
    }
  },

  onImageTap(e) {
    const { index } = e.currentTarget.dataset
    const { processedImages } = this.data.post
    if (!processedImages || !processedImages.length) return
    wx.previewImage({
      current: processedImages[index],
      urls: processedImages
    })
  },

  onAuthorTap() {
    const { post } = this.data
    if (post && (post.authorId || post.userId)) {
      wx.navigateTo({ url: `/pages/user-profile/user-profile?id=${post.authorId || post.userId}` })
    }
  },

  async onFollowAuthor(e) {
    e.stopPropagation()
    if (!auth.requireAuth()) return
    const { post } = this.data
    if (!post || !post.authorId || post.authorId === this.data.currentUserId) return

    const followed = !post.authorFollowed
    this.setData({ 'post.authorFollowed': followed })
    try {
      await api.followUser(post.authorId)
    } catch (err) {
      this.setData({ 'post.authorFollowed': !followed })
    }
  },

  onCircleTap() {
    const { post } = this.data
    if (post && post.circleId) {
      wx.navigateTo({ url: `/pages/circle-detail/circle-detail?id=${post.circleId}` })
    }
  },

  onMoreAction() {
    const { post, currentUserId } = this.data
    if (!post) return

    if (post.authorId === currentUserId) {
      wx.showActionSheet({
        itemList: ['删除帖子'],
        success: (res) => {
          if (res.tapIndex === 0) this.confirmDelete()
        }
      })
    }
  },

  confirmDelete() {
    wx.showModal({
      title: '确认删除',
      content: '删除后无法恢复，确定要删除吗？',
      confirmColor: '#EF4444',
      success: async (res) => {
        if (!res.confirm) return
        wx.showLoading({ title: '删除中...', mask: true })
        try {
          await api.deletePost(this.data.id)
          wx.hideLoading()
          wx.showToast({ title: '已删除', icon: 'success' })
          setTimeout(() => wx.navigateBack(), 800)
        } catch (err) {
          wx.hideLoading()
        }
      }
    })
  },

  onShareAppMessage() {
    const { post } = this.data
    return {
      title: post ? post.title : '帖子详情',
      path: `/pages/post-detail/post-detail?id=${this.data.id}`
    }
  }
})
