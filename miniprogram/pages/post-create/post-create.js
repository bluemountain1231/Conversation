const api = require('../../utils/api')
const auth = require('../../utils/auth')
const util = require('../../utils/util')

Page({
  data: {
    content: '',
    images: [],
    circles: [],
    circleIndex: 0,
    selectedCircleId: null,
    selectedCircleName: '',
    loading: false,
    uploading: false,
    allowComments: true,
    canSubmit: false,
    userAvatar: '',
    userName: ''
  },

  onLoad() {
    if (!auth.isLoggedIn()) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    const userInfo = wx.getStorageSync('userInfo') || {}
    this.setData({
      userAvatar: util.getImageUrl(userInfo.avatar),
      userName: userInfo.username || '用户'
    })
    this.loadCircles()
  },

  async loadCircles() {
    try {
      let res = await api.getMyCircles()
      let circles = Array.isArray(res.data) ? res.data : (res.data?.content || [])
      if (circles.length === 0) {
        res = await api.getCircles(0, 50)
        circles = res.data?.content || (Array.isArray(res.data) ? res.data : [])
      }
      circles = [{ id: null, name: '不选择圈子' }, ...circles]
      this.setData({ circles })
    } catch (err) {}
  },

  onContentInput(e) {
    const content = e.detail.value
    this.setData({ content, canSubmit: content.trim().length >= 5 })
  },

  checkCanSubmit() {
    this.setData({ canSubmit: this.data.content.trim().length >= 5 })
  },

  onToggleComments(e) {
    this.setData({ allowComments: e.detail.value })
  },

  chooseImage() {
    const remain = 9 - this.data.images.length
    if (remain <= 0) {
      wx.showToast({ title: '最多上传9张图片', icon: 'none' })
      return
    }
    wx.chooseMedia({
      count: remain,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newFiles = res.tempFiles.map(f => ({
          path: f.tempFilePath,
          url: '',
          uploading: false,
          uploaded: false,
          error: false
        }))
        this.setData({ images: [...this.data.images, ...newFiles] })
      }
    })
  },

  removeImage(e) {
    const { index } = e.currentTarget.dataset
    const images = [...this.data.images]
    images.splice(index, 1)
    this.setData({ images })
  },

  previewImage(e) {
    const { index } = e.currentTarget.dataset
    const urls = this.data.images.map(img => img.path)
    wx.previewImage({ current: urls[index], urls })
  },

  onCircleChange(e) {
    const index = parseInt(e.detail.value, 10)
    const circles = this.data.circles
    if (circles.length === 0) return
    const circle = circles[index]
    this.setData({
      circleIndex: index,
      selectedCircleId: circle.id || null,
      selectedCircleName: circle.id ? circle.name : ''
    })
  },

  onClose() {
    if (this.data.content.trim() || this.data.images.length > 0) {
      wx.showModal({
        title: '确定要放弃吗？',
        content: '退出后内容将不会保存',
        confirmText: '放弃',
        confirmColor: '#EF4444',
        success: (res) => {
          if (res.confirm) wx.navigateBack()
        }
      })
    } else {
      wx.navigateBack()
    }
  },

  async uploadImages() {
    const { images } = this.data
    const needUpload = images.filter(img => !img.uploaded && !img.error)
    if (needUpload.length === 0) {
      return images.filter(img => img.uploaded).map(img => img.url)
    }
    this.setData({ uploading: true })
    const urls = []
    for (let i = 0; i < images.length; i++) {
      const img = images[i]
      if (img.uploaded && img.url) { urls.push(img.url); continue }
      this.setData({ [`images[${i}].uploading`]: true })
      try {
        const res = await api.uploadImage(img.path)
        const url = res.data?.url || res.data
        if (!url) throw new Error('上传失败')
        urls.push(url)
        this.setData({
          [`images[${i}].uploading`]: false,
          [`images[${i}].uploaded`]: true,
          [`images[${i}].url`]: url
        })
      } catch (err) {
        this.setData({ [`images[${i}].uploading`]: false, [`images[${i}].error`]: true })
        throw new Error('图片上传失败')
      }
    }
    this.setData({ uploading: false })
    return urls
  },

  async onSubmit() {
    const { content } = this.data
    if (!content.trim() || content.trim().length < 5) {
      wx.showToast({ title: '内容至少5个字', icon: 'none' })
      return
    }
    if (this.data.loading || this.data.uploading) return
    this.setData({ loading: true })
    wx.showLoading({ title: '发布中...', mask: true })
    try {
      let imageUrls = []
      if (this.data.images.length > 0) {
        wx.showLoading({ title: '上传图片中...', mask: true })
        imageUrls = await this.uploadImages()
        wx.showLoading({ title: '发布中...', mask: true })
      }
      const data = {
        title: content.trim().substring(0, 50),
        content: content.trim(),
        images: imageUrls
      }
      if (this.data.selectedCircleId) {
        data.circleId = this.data.selectedCircleId
      }
      await api.createPost(data)
      wx.hideLoading()
      wx.showToast({ title: '发布成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 800)
    } catch (err) {
      wx.hideLoading()
      this.setData({ loading: false })
      if (err.message !== '图片上传失败') {
        wx.showToast({ title: '发布失败，请重试', icon: 'none' })
      }
    }
  }
})
