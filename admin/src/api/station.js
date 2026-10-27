import request from '@/utils/request'

/** 油站分页 */
export function stationPage(params) {
  return request({ url: '/admin/station/page', method: 'get', params })
}

/** 全部油站 */
export function stationList() {
  return request({ url: '/admin/station/list', method: 'get' })
}

export function saveStation(data) {
  return request({ url: '/admin/station', method: data.id ? 'put' : 'post', data })
}

export function removeStation(id) {
  return request({ url: `/admin/station/${id}`, method: 'delete' })
}

/** 油枪 */
export function gunList(stationId) {
  return request({ url: '/admin/gun/list', method: 'get', params: { stationId } })
}

export function saveGun(data) {
  return request({ url: '/admin/gun', method: data.id ? 'put' : 'post', data })
}

export function changeGunStatus(id, status) {
  return request({ url: `/admin/gun/${id}/status`, method: 'put', params: { status } })
}

export function removeGun(id) {
  return request({ url: `/admin/gun/${id}`, method: 'delete' })
}

/** 油价 */
export function priceList(stationId) {
  return request({ url: '/admin/fuel-price/list', method: 'get', params: { stationId } })
}

export function savePrice(data) {
  return request({ url: '/admin/fuel-price', method: data.id ? 'put' : 'post', data })
}

export function removePrice(id) {
  return request({ url: `/admin/fuel-price/${id}`, method: 'delete' })
}
