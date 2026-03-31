import request from './request'

export function getAdminUsers(page = 0, size = 10, keyword) {
  return request.get('/admin/users', { params: { page, size, keyword } })
}

export function updateUserRole(id, role) {
  return request.put(`/admin/users/${id}/role`, { role })
}

export function toggleBanUser(id) {
  return request.put(`/admin/users/${id}/ban`)
}

export function getAdminPosts(page = 0, size = 10, status) {
  return request.get('/admin/posts', { params: { page, size, status } })
}

export function updatePostStatus(id, status) {
  return request.put(`/admin/posts/${id}/status`, { status })
}

export function getAdminCircles(page = 0, size = 10) {
  return request.get('/admin/circles', { params: { page, size } })
}

export function deleteAdminCircle(id) {
  return request.delete(`/admin/circles/${id}`)
}

export function getAdminLogs(page = 0, size = 20) {
  return request.get('/admin/logs', { params: { page, size } })
}

export function getStatsOverview() {
  return request.get('/stats/admin/overview')
}

export function getStatsTrends() {
  return request.get('/stats/admin/trends')
}

export function getUserStats(userId) {
  return request.get(`/stats/user/${userId}`)
}
