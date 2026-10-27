const api = require('../../utils/api')

Page({
  data: {
    order: null,
    statusText: ['待支付', '已支付', '已完成', '已取消', '已退款']
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo })
  },

  onShow() {
    this.load()
  },

  load() {
    api.orderDetail(this.data.orderNo).then(res => {
      this.setData({ order: res })
    })
  },

  pay() {
    api.payOrder(this.data.orderNo, 'balance').then(() => {
      wx.showToast({ title: '支付成功' })
      this.load()
    })
  },

  cancel() {
    wx.showModal({
      title: '提示',
      content: '确认取消该订单？',
      success: res => {
        if (res.confirm) {
          api.cancelOrder(this.data.orderNo).then(() => {
            wx.showToast({ title: '已取消' })
            this.load()
          })
        }
      }
    })
  }
})
