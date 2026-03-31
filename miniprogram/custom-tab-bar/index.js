const auth = require('../utils/auth')

Component({
  data: {
    selected: 0,
    list: [
      { text: '首页', icon: '⌂', pagePath: '/pages/index/index' },
      { text: '发现', icon: '✦', pagePath: '/pages/circles/circles' },
      { text: '消息', icon: '🔔', pagePath: '/pages/notifications/notifications', badge: 0 },
      { text: '我的', icon: '👤', pagePath: '/pages/profile/profile' }
    ]
  },
  methods: {
    switchTab(e) {
      const { index } = e.currentTarget.dataset
      const item = this.data.list[index]
      if (this.data.selected === index) return
      wx.switchTab({ url: item.pagePath })
    },
    onCreatePost() {
      if (!auth.isLoggedIn()) {
        wx.navigateTo({ url: '/pages/login/login' })
        return
      }
      wx.navigateTo({ url: '/pages/post-create/post-create' })
    }
  }
})
