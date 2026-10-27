const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    tabs: ['领券中心', '我的优惠券'],
    activeTab: 0,
    center: [],
    mine: []
  },

  onShow() {
    if (!app.isLogin()) {
      app.login().then(() => this.load()).catch(() => {})
      return
    }
    this.load()
  },

  load() {
    api.couponCenter().then(res => this.setData({ center: res || [] })).catch(() => {})
    api.myCoupons(0).then(res => this.setData({ mine: res || [] })).catch(() => {})
  },

  switchTab(e) {
    this.setData({ activeTab: Number(e.currentTarget.dataset.index) })
  },

  receive(e) {
    const id = e.currentTarget.dataset.id
    api.receiveCoupon(id).then(() => {
      wx.showToast({ title: '领取成功' })
      this.load()
    })
  }
})
