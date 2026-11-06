const { get, post, put } = require('./request')

/** 油站 */
const nearby = (lng, lat, keyword) => get('/api/public/station/nearby', { lng, lat, keyword })
const stationDetail = id => get(`/api/public/station/${id}`)

/** 订单 */
const placeOrder = data => post('/api/app/order/place', data)
const payOrder = (orderNo, payType) => post('/api/app/order/pay', { orderNo, payType })
const cancelOrder = orderNo => post(`/api/app/order/cancel/${orderNo}`)
const myOrders = (current, size, status) => get('/api/app/order/page', { current, size, status })
const orderDetail = orderNo => get(`/api/app/order/${orderNo}`)

/** 会员 */
const memberInfo = () => get('/api/app/member/info')
const updateProfile = data => put('/api/app/member/profile', data)
const balanceLogs = () => get('/api/app/member/balance-logs')

/** 预约加油 */
const createReserve = data => post('/api/app/reserve/create', data)
const cancelReserve = id => post(`/api/app/reserve/cancel/${id}`)
const activeReserve = () => get('/api/app/reserve/active')
const myReserves = (current, size) => get('/api/app/reserve/page', { current, size })

/** 优惠券 */
const couponCenter = () => get('/api/app/coupon/center')
const myCoupons = status => get('/api/app/coupon/mine', { status })
const receiveCoupon = couponId => post(`/api/app/coupon/receive/${couponId}`)
const usableCoupons = amount => get('/api/app/coupon/usable', { amount })

module.exports = {
  nearby,
  stationDetail,
  placeOrder,
  payOrder,
  cancelOrder,
  myOrders,
  orderDetail,
  memberInfo,
  updateProfile,
  balanceLogs,
  createReserve,
  cancelReserve,
  activeReserve,
  myReserves,
  couponCenter,
  myCoupons,
  receiveCoupon,
  usableCoupons
}
