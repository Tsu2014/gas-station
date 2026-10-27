const api = require('../../utils/api')

Page({
  data: {
    stationId: null,
    station: null,
    prices: [],
    guns: [],
    selectedGun: null,
    selectedPrice: null
  },

  onLoad(options) {
    this.setData({ stationId: options.id })
    this.loadDetail(options.id)
  },

  loadDetail(id) {
    api.stationDetail(id).then(res => {
      const prices = res.prices || []
      this.setData({
        station: res.station,
        prices: prices,
        guns: res.guns || [],
        selectedPrice: prices.length > 0 ? prices[0] : null
      })
    })
  },

  selectPrice(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ selectedPrice: this.data.prices[index], selectedGun: null })
  },

  selectGun(e) {
    const index = e.currentTarget.dataset.index
    const gun = this.data.guns[index]
    if (gun.status !== 1) {
      wx.showToast({ title: '该油枪当前不可用', icon: 'none' })
      return
    }
    const price = this.data.prices.find(item => item.fuelType === gun.fuelType)
    this.setData({ selectedGun: gun, selectedPrice: price || this.data.selectedPrice })
  },

  goRefuel() {
    const gun = this.data.selectedGun
    const price = this.data.selectedPrice
    if (!price) {
      wx.showToast({ title: '该油站暂未设置油价', icon: 'none' })
      return
    }
    const url = `/pages/refuel/index?stationId=${this.data.stationId}` +
      `&fuelType=${price.fuelType}&unitPrice=${price.salePrice}` +
      (gun ? `&gunId=${gun.id}&gunNo=${gun.gunNo}` : '') +
      `&stationName=${this.data.station.name}`
    wx.navigateTo({ url })
  }
})
