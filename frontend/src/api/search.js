import request from './request'

export function search(keyword, type = 'all', page = 0, size = 10) {
  return request.get('/search', { params: { keyword, type, page, size } })
}
