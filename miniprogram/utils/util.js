const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

  const y = date.getFullYear()
  const m = (date.getMonth() + 1).toString().padStart(2, '0')
  const d = date.getDate().toString().padStart(2, '0')
  return `${y}-${m}-${d}`
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const truncate = (str, len = 100) => {
  if (!str) return ''
  const text = str.replace(/<[^>]+>/g, '')
  return text.length > len ? text.substring(0, len) + '...' : text
}

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const app = getApp()
  return app.globalData.baseUrl + url
}

module.exports = { formatTime, formatNumber, truncate, getImageUrl }
