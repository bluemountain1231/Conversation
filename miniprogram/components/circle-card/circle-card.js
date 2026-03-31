const util = require('../../utils/util')
const api = require('../../utils/api')

Component({
  properties: {
    circle: {
      type: Object,
      value: {}
    }
  },

  data: {
    avatarUrl: '',
    memberText: '',
    badgeText: '',
    badgeType: ''
  },

  observers: {
    'circle': function (circle) {
      if (!circle || !circle.id) return
      let badgeText = ''
      let badgeType = ''
      if (circle.memberCount >= 1000) {
        badgeText = '热门'
        badgeType = 'trending'
      } else if (circle.postCount >= 50) {
        badgeText = '活跃'
        badgeType = 'active'
      } else if (circle.memberCount < 100) {
        badgeText = '新圈'
        badgeType = 'new'
      }
      this.setData({
        avatarUrl: util.getImageUrl(circle.avatar),
        memberText: util.formatNumber(circle.memberCount) + ' 成员',
        badgeText,
        badgeType
      })
    }
  },

  methods: {
    onTap() {
      this.triggerEvent('tap', { id: this.data.circle.id })
    },
    async onJoin() {
      const { circle } = this.data
      if (!circle || !circle.id) return
      try {
        await api.joinCircle(circle.id)
        wx.showToast({ title: '已加入', icon: 'success' })
        this.triggerEvent('join', { id: circle.id })
      } catch (e) {}
    }
  }
})
