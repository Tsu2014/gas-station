import request from '@/utils/request'

export function orderPage(params) {
  return request({ url: '/admin/order/page', method: 'get', params })
}

export function orderDetail(id) {
  return request({ url: `/admin/order/${id}`, method: 'get' })
}

export function finishOrder(id) {
  return request({ url: `/admin/order/${id}/finish`, method: 'post' })
}

export function refundOrder(id) {
  return request({ url: `/admin/order/${id}/refund`, method: 'post' })
}
