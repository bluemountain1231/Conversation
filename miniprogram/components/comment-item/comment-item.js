const util = require('../../utils/util')

Component({
  properties: {
    comment: {
      type: Object,
      value: {}
    }
  },

  data: {
    avatarUrl: '',
    timeText: '',
    replies: []
  },

  observers: {
    'comment': function (comment) {
      if (!comment || !comment.id) return
      this.setData({
        avatarUrl: util.getImageUrl(comment.authorAvatar),
        timeText: util.formatTime(comment.createdAt),
        replies: (comment.replies || []).map(r => ({
          ...r,
          avatarUrl: util.getImageUrl(r.authorAvatar),
          timeText: util.formatTime(r.createdAt)
        }))
      })
    }
  },

  methods: {
    onReply() {
      this.triggerEvent('reply', {
        id: this.data.comment.id,
        authorName: this.data.comment.authorName,
        userId: this.data.comment.userId
      })
    },

    onReplyToReply(e) {
      const { reply } = e.currentTarget.dataset
      this.triggerEvent('reply', {
        id: reply.id,
        authorName: reply.authorName,
        userId: reply.userId
      })
    },

    onAuthorTap() {
      wx.navigateTo({ url: `/pages/user-profile/user-profile?id=${this.data.comment.userId}` })
    }
  }
})
