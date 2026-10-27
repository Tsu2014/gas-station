const api = require('../../utils/api')

Page({
  data: {
    keyword: '',
    stations: [],
    locationText: '正在定位…',
    lng: null,
    lat: null
  },

  onShow() {
    if (this.data.stations.length === 0) {
      this.locate()
    }
  },

  onPullDownRefresh() {
    this.loadStations().then(() => wx.stopPullDownRefresh())
  },

  locate() {
    const that = this
    wx.getLocation({
      type: 'gcj02',
      success(res) {
        that.setData({ lng: res.longitude, lat: res.latitude, locationText: '已定位，为你推荐附近油站' })
        that.loadStations()
      },
      fail() {
        that.setData({ locationText: '定位失败，展示全部油站' })
        that.loadStations()
      }
    })
  },

  loadStations() {
    return api.nearby(this.data.lng, this.data.lat, this.data.keyword).then(list => {
      this.setData({ stations: list || [] })
    }).catch(() => {
      this.setData({ stations: [] })
    })
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.loadStations()
  },

  goDetail(e) {
    wx.navigateTo({ url: '/pages/station/detail?id=' + e.currentTarget.dataset.id })
  },

  scanCode() {
    wx.scanCode({
      success(res) {
        // 二维码内容形如：station=1&gun=2
        const query = res.result || ''
        const params = {}
        query.replace(/([^&=]+)=([^&]*)/g, (m, key, value) => {
          params[key] = decodeURIComponent(value)
        })
        if (params.gun) {
          wx.navigateTo({ url: `/pages/refuel/index?gunId=${params.gun}&stationId=${params.station || ''}` })
        } else if (params.station) {
          wx.navigateTo({ url: '/pages/station/detail?id=' + params.station })
        } else {
          wx.showToast({ title: '无法识别的二维码：' + query, icon: 'none' })
        }
      }
    })
    return that
  }
})
