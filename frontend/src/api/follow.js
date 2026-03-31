import request from './request'

export function toggleFollow(userId) {
  return request.post(`/users/${userId}/follow`)
}

export function getRecommendedUsers() {
  return request.get('/recommend/users')
}
