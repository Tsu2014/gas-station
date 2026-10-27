const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    member: null,
    logs: []
  },

  onShow() {
    if (app.isLogin()) {
      this.load()
    }
  },

  load() {
    api.memberInfo().then(res => this.setData({ member: res })).catch(() => {})
    api.balanceLogs().then(res => this.setData({ logs: (res || []).slice(0, 5) })).catch(() => {})
  },

  login() {
    app.login().then(() => {
      wx.showToast({ title: '登录成功' })
      this.load()
    })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确认退出登录？',
      success: res => {
        if (res.confirm) {
          app.logout()
          this.setData({ member: null, logs: [] })
        }
      }
    })
  },

  goOrders() {
    wx.switchTab({ url: '/pages/order/list' })
  },

  goCoupons() {
    wx.switchTab({ url: '/pages/coupon/index' })
  },

  typeText(type) {
    return ['', '充值', '消费', '退款', '调整'][type] || ''
  }
})
