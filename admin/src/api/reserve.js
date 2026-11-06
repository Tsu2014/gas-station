import request from '@/utils/request'

/** 看板：待确认预约列表 */
export function reserveBoard() {
  return request({ url: '/admin/reserve/board', method: 'get' })
}

/** 历史预约订单分页（车牌号/时间查询） */
export function reservePage(params) {
  return request({ url: '/admin/reserve/page', method: 'get', params })
}

/** 确认已加油 */
export function confirmReserve(id) {
  return request({ url: `/admin/reserve/${id}/confirm`, method: 'post' })
}

/** 拒绝预约（带回复） */
export function rejectReserve(id, reply) {
  return request({ url: `/admin/reserve/${id}/reject`, method: 'post', data: { reply } })
}
