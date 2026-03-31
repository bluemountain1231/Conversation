import request from './request'

export function getPosts(page = 0, size = 10) {
  return request.get('/posts', { params: { page, size } })
}

export function getPostById(id) {
  return request.get(`/posts/${id}`)
}

export function createPost(data) {
  return request.post('/posts', data)
}

export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}

export function toggleLike(id) {
  return request.post(`/posts/${id}/like`)
}

export function toggleFavorite(id) {
  return request.post(`/posts/${id}/favorite`)
}

export function getUserPosts(userId, page = 0, size = 10) {
  return request.get(`/posts/user/${userId}`, { params: { page, size } })
}

export function getUserFavorites(page = 0, size = 10) {
  return request.get('/posts/favorites', { params: { page, size } })
}

export function getFeed(page = 0, size = 10) {
  return request.get('/posts/feed', { params: { page, size } })
}
