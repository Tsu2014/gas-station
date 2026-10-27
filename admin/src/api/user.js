import request from '@/utils/request'

export function userPage(params) {
  return request({ url: '/admin/user/page', method: 'get', params })
}

export function saveUser(data) {
  return request({ url: '/admin/user', method: data.id ? 'put' : 'post', data })
}

export function removeUser(id) {
  return request({ url: `/admin/user/${id}`, method: 'delete' })
}
