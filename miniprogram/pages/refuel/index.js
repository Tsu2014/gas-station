const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    stationId: null,
    stationName: '',
    gunId: null,
    gunNo: '',
    fuelType: '',
    unitPrice: 0,
    amount: 200,
    liters: '0.00',
    coupons: [],
    selectedCoupon: null,
    payType: 'balance',
    submitting: false
  },

  onLoad(options) {
    const unitPrice = Number(options.unitPrice || 0)
    this.setData({
      stationId: options.stationId,
      stationName: options.stationName || '',
      gunId: options.gunId || null,
      gunNo: options.gunNo || '',
      fuelType: options.fuelType || '',
      unitPrice: unitPrice,
      amount: 200
    })
    if (!app.isLogin()) {
      app.login().then(() => this.loadCoupons()).catch(() => {})
    } else {
      this.loadCoupons()
    }
    this.calcLiters()
  },

  calcLiters() {
    const unitPrice = Number(this.data.unitPrice)
    const liters = unitPrice > 0 ? (Number(this.data.amount) / unitPrice) : 0
    this.setData({ liters: liters.toFixed(2) })
  },

  chooseAmount(e) {
    this.setData({ amount: Number(e.currentTarget.dataset.value) })
    this.calcLiters()
    this.loadCoupons()
  },

  onAmountInput(e) {
    const value = Number(e.detail.value || 0)
    this.setData({ amount: value })
    this.calcLiters()
  },

  onAmountBlur() {
    this.loadCoupons()
  },

  loadCoupons() {
    if (!app.isLogin() || !this.data.amount) {
      return
    }
    api.usableCoupons(this.data.amount).then(list => {
      this.setData({ coupons: list || [], selectedCoupon: null })
    }).catch(() => {
      this.setData({ coupons: [] })
    })
  },

  selectCoupon(e) {
    const index = e.currentTarget.dataset.index
    const coupon = this.data.coupons[index]
    this.setData({
      selectedCoupon: this.data.selectedCoupon && this.data.selectedCoupon.id === coupon.id ? null : coupon
    })
  },

  selectPayType(e) {
    this.setData({ payType: e.currentTarget.dataset.type })
  },

  getPayAmount() {
    const amount = Number(this.data.amount) || 0
    const discount = this.data.selectedCoupon ? Number(this.data.selectedCoupon.discountAmount) : 0
    return Math.max(0, amount - discount)
  },

  submit() {
    if (this.data.submitting) return
    if (!this.data.amount || Number(this.data.amount) <= 0) {
      wx.showToast({ title: '请输入加油金额', icon: 'none' })
      return
    }
    if (!app.isLogin()) {
      app.login().then(() => this.doSubmit()).catch(() => {})
      return
    }
    this.doSubmit()
  },

  doSubmit() {
    this.setData({ submitting: true })
    const data = {
      stationId: Number(this.data.stationId),
      gunId: this.data.gunId ? Number(this.data.gunId) : null,
      fuelType: this.data.fuelType,
      amount: Number(this.data.amount),
      memberCouponId: this.data.selectedCoupon ? this.data.selectedCoupon.id : null
    }
    api.placeOrder(data).then(order => {
      return api.payOrder(order.orderNo, this.data.payType).then(() => order)
    }).then(order => {
      wx.showToast({ title: '支付成功' })
      wx.redirectTo({ url: '/pages/order/detail?orderNo=' + order.orderNo })
    }).catch(() => {
      this.setData({ submitting: false })
    })
  }
})
