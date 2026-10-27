import request from '@/utils/request'

export function memberPage(params) {
  return request({ url: '/admin/member/page', method: 'get', params })
}

export function changeMemberStatus(id, status) {
  return request({ url: `/admin/member/${id}/status`, method: 'put', params: { status } })
}

export function adjustBalance(data) {
  return request({ url: '/admin/member/adjust-balance', method: 'post', data })
}

export function balanceLogs(id) {
  return request({ url: `/admin/member/${id}/balance-logs`, method: 'get' })
}
