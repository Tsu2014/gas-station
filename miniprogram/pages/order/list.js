const api = require('../../utils/api')
const app = getApp()

const STATUS_TABS = [
  { label: '全部', value: null },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已退款', value: 4 }
]

Page({
  data: {
    tabs: STATUS_TABS,
    activeTab: 0,
    orders: [],
    statusText: ['待支付', '已支付', '已完成', '已取消', '已退款']
  },

  onShow() {
    if (!app.isLogin()) {
      app.login().then(() => this.load()).catch(() => {})
      return
    }
    this.load()
  },

  load() {
    const status = this.data.tabs[this.data.activeTab].value
    api.myOrders(1, 20, status === null ? '' : status).then(res => {
      this.setData({ orders: res.records || [] })
    }).catch(() => {
      this.setData({ orders: [] })
    })
  },

  switchTab(e) {
    this.setData({ activeTab: Number(e.currentTarget.dataset.index) })
    this.load()
  },

  goDetail(e) {
    wx.navigateTo({ url: '/pages/order/detail?orderNo=' + e.currentTarget.dataset.orderno })
  },

  pay(e) {
    const orderNo = e.currentTarget.dataset.orderno
    api.payOrder(orderNo, 'balance').then(() => {
      wx.showToast({ title: '支付成功' })
      this.load()
    })
  },

  cancel(e) {
    const orderNo = e.currentTarget.dataset.orderno
    wx.showModal({
      title: '提示',
      content: '确认取消该订单？',
      success: res => {
        if (res.confirm) {
          api.cancelOrder(orderNo).then(() => {
            wx.showToast({ title: '已取消' })
            this.load()
          })
        }
      }
    })
  }
})
