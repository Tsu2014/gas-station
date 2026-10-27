const { get, post } = require('./request')

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
const balanceLogs = () => get('/api/app/member/balance-logs')

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
  balanceLogs,
  couponCenter,
  myCoupons,
  receiveCoupon,
  usableCoupons
}
