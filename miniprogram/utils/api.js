const BASE_URL = 'https://frp-six.com:36605'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    const header = { 'Content-Type': 'application/json', ...options.header }
    if (token) header['Authorization'] = `Bearer ${token}`

    wx.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header,
      success(res) {
        if (res.statusCode === 401) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.navigateTo({ url: '/pages/login/login' })
          reject(new Error('未授权'))
          return
        }
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          wx.showToast({ title: res.data.message || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

const login = (data) => request({ url: '/api/auth/login', method: 'POST', data })
const register = (data) => request({ url: '/api/auth/register', method: 'POST', data })

const getMe = () => request({ url: '/api/users/me' })
const updateMe = (data) => request({ url: '/api/users/me', method: 'PUT', data })
const getUser = (id) => request({ url: `/api/users/${id}` })
const followUser = (id) => request({ url: `/api/users/${id}/follow`, method: 'POST' })
const getRecommendUsers = () => request({ url: '/api/recommend/users' })

const getPosts = (page = 0, size = 10) => request({ url: `/api/posts?page=${page}&size=${size}` })
const getPost = (id) => request({ url: `/api/posts/${id}` })
const createPost = (data) => request({ url: '/api/posts', method: 'POST', data })
const deletePost = (id) => request({ url: `/api/posts/${id}`, method: 'DELETE' })
const likePost = (id) => request({ url: `/api/posts/${id}/like`, method: 'POST' })
const favoritePost = (id) => request({ url: `/api/posts/${id}/favorite`, method: 'POST' })
const getUserPosts = (userId, page = 0, size = 10) => request({ url: `/api/posts/user/${userId}?page=${page}&size=${size}` })
const getFeed = (page = 0, size = 10) => request({ url: `/api/posts/feed?page=${page}&size=${size}` })
const getFavorites = (page = 0, size = 10) => request({ url: `/api/posts/favorites?page=${page}&size=${size}` })

const getComments = (postId) => request({ url: `/api/posts/${postId}/comments` })
const createComment = (postId, data) => request({ url: `/api/posts/${postId}/comments`, method: 'POST', data })

const getCircles = (page = 0, size = 10) => request({ url: `/api/circles?page=${page}&size=${size}` })
const getHotCircles = () => request({ url: '/api/circles/hot' })
const getMyCircles = () => request({ url: '/api/circles/my' })
const getCircle = (id) => request({ url: `/api/circles/${id}` })
const createCircle = (data) => request({ url: '/api/circles', method: 'POST', data })
const joinCircle = (id) => request({ url: `/api/circles/${id}/join`, method: 'POST' })
const leaveCircle = (id) => request({ url: `/api/circles/${id}/leave`, method: 'POST' })
const getCircleMembers = (id, page = 0, size = 20) => request({ url: `/api/circles/${id}/members?page=${page}&size=${size}` })
const getCirclePosts = (id, page = 0, size = 10) => request({ url: `/api/circles/${id}/posts?page=${page}&size=${size}` })

const uploadImage = (filePath) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.navigateTo({ url: '/pages/login/login' })
      reject(new Error('未登录'))
      return
    }
    wx.uploadFile({
      url: `${BASE_URL}/api/upload/image`,
      filePath,
      name: 'file',
      header: { 'Authorization': `Bearer ${token}` },
      success(res) {
        if (res.statusCode === 401 || res.statusCode === 403) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          wx.navigateTo({ url: '/pages/login/login' })
          reject(new Error('登录已过期'))
          return
        }
        if (res.statusCode !== 200) {
          reject(new Error('服务器错误: ' + res.statusCode))
          return
        }
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) resolve(data)
          else {
            wx.showToast({ title: data.message || '上传失败', icon: 'none' })
            reject(data)
          }
        } catch (e) {
          reject(new Error('响应解析失败'))
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误，请检查网络', icon: 'none' })
        reject(err)
      }
    })
  })
}

const getNotifications = (page = 0, size = 20) => request({ url: `/api/notifications?page=${page}&size=${size}` })
const getUnreadCount = () => request({ url: '/api/notifications/unread-count' })
const markAllRead = () => request({ url: '/api/notifications/read', method: 'PUT' })
const sendMessage = (data) => request({ url: '/api/notifications/message', method: 'POST', data })

const search = (keyword, type = 'all', page = 0, size = 10) =>
  request({ url: `/api/search?keyword=${encodeURIComponent(keyword)}&type=${type}&page=${page}&size=${size}` })

const getUserStats = (id) => request({ url: `/api/stats/user/${id}` })

const getRecommendPosts = (page = 0, size = 10) => request({ url: `/api/recommend/posts?page=${page}&size=${size}` })
const getRecommendCircles = (limit = 5) => request({ url: `/api/recommend/circles?limit=${limit}` })

module.exports = {
  BASE_URL, request,
  login, register,
  getMe, updateMe, getUser, followUser, getRecommendUsers,
  getPosts, getPost, createPost, deletePost, likePost, favoritePost, getUserPosts, getFeed, getFavorites,
  getComments, createComment,
  getCircles, getHotCircles, getMyCircles, getCircle, createCircle, joinCircle, leaveCircle, getCircleMembers, getCirclePosts,
  uploadImage,
  getNotifications, getUnreadCount, markAllRead, sendMessage,
  search, getUserStats,
  getRecommendPosts, getRecommendCircles
}
