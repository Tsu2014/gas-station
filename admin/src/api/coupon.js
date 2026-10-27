import request from '@/utils/request'

export function couponPage(params) {
  return request({ url: '/admin/coupon/page', method: 'get', params })
}

export function saveCoupon(data) {
  return request({ url: '/admin/coupon', method: data.id ? 'put' : 'post', data })
}

export function changeCouponStatus(id, status) {
  return request({ url: `/admin/coupon/${id}/status`, method: 'put', params: { status } })
}

export function removeCoupon(id) {
  return request({ url: `/admin/coupon/${id}`, method: 'delete' })
}
