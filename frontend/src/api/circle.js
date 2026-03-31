import request from './request'

export function getCircles(page = 0, size = 10) {
  return request.get('/circles', { params: { page, size } })
}

export function getHotCircles() {
  return request.get('/circles/hot')
}

export function getMyCircles() {
  return request.get('/circles/my')
}

export function getCircleById(id) {
  return request.get(`/circles/${id}`)
}

export function createCircle(data) {
  return request.post('/circles', data)
}

export function joinCircle(id) {
  return request.post(`/circles/${id}/join`)
}

export function leaveCircle(id) {
  return request.post(`/circles/${id}/leave`)
}

export function getCirclePosts(id, page = 0, size = 10) {
  return request.get(`/circles/${id}/posts`, { params: { page, size } })
}

export function getCircleMembers(id, page = 0, size = 20) {
  return request.get(`/circles/${id}/members`, { params: { page, size } })
}
