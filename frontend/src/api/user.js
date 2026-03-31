import request from './request'

export function getCurrentUser() {
  return request.get('/users/me')
}

export function updateUser(data) {
  return request.put('/users/me', data)
}

export function getUserById(id) {
  return request.get(`/users/${id}`)
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
