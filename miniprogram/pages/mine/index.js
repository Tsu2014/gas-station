const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    member: null,
    logs: [],
    plateEditing: false,
    plateInput: ''
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

  goReserve() {
    wx.switchTab({ url: '/pages/reserve/index' })
  },

  editPlate() {
    this.setData({ plateEditing: true, plateInput: (this.data.member && this.data.member.plateNo) || '' })
  },

  closePlate() {
    this.setData({ plateEditing: false })
  },

  onPlateInput(e) {
    this.setData({ plateInput: e.detail.value })
  },

  savePlate() {
    const plateNo = (this.data.plateInput || '').trim().toUpperCase()
    if (!plateNo) {
      wx.showToast({ title: '请输入车牌号', icon: 'none' })
      return
    }
    api.updateProfile({ plateNo }).then(member => {
      this.setData({ member, plateEditing: false })
      wx.showToast({ title: '已保存', icon: 'success' })
    }).catch(() => {})
  },

  goCoupons() {
    wx.switchTab({ url: '/pages/coupon/index' })
  },

  noop() {},

  typeText(type) {
    return ['', '充值', '消费', '退款', '调整'][type] || ''
  }
})
