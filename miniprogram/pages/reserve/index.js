const api = require('../../utils/api')
const app = getApp()

/** 兼容 iOS 的时间解析："2026-11-06T12:30:00" -> Date */
function parseTime(str) {
  if (!str) return null
  return new Date(String(str).replace('T', ' ').replace(/-/g, '/'))
}

function fmtTime(str) {
  return str ? String(str).replace('T', ' ').slice(0, 16) : ''
}

Page({
  data: {
    member: null,
    stations: [],
    stationIndex: -1,
    prices: [],
    fuelIndex: 0,
    litersOptions: [10, 20, 30, 40, 50],
    liters: 30,
    litersOption: 30,
    minutesOptions: [
      { label: '30分钟内', value: 30 },
      { label: '1小时内', value: 60 },
      { label: '2小时内', value: 120 }
    ],
    minutes: 30,
    customMinutes: '',
    plateNo: '',
    setDefault: false,
    remark: '',
    active: null,
    countdown: '',
    records: [],
    estAmount: '0.00',
    submitting: false
  },

  onShow() {
    if (!app.isLogin()) {
      app.login().then(() => this.init()).catch(() => {})
      return
    }
    this.init()
  },

  onPullDownRefresh() {
    Promise.all([this.loadMember(), this.loadStations(), this.loadActive(), this.loadRecords()])
      .finally(() => wx.stopPullDownRefresh())
  },

  onUnload() {
    this.clearTimer()
  },

  onHide() {
    this.clearTimer()
  },

  init() {
    this.loadMember()
    this.loadStations()
    this.loadActive()
    this.loadRecords()
  },

  clearTimer() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
  },

  loadMember() {
    return api.memberInfo().then(member => {
      this.setData({
        member,
        plateNo: this.data.plateNo || member.plateNo || '',
        setDefault: false
      })
      this.applyDefaultStation(member.defaultStationId)
    }).catch(() => {})
  },

  loadStations() {
    return api.nearby(null, null, '').then(list => {
      this.setData({ stations: list || [] })
      if (this.data.stationIndex < 0 && list && list.length > 0) {
        this.chooseStationByIndex(0)
      }
      if (this.data.member) {
        this.applyDefaultStation(this.data.member.defaultStationId)
      }
    }).catch(() => {})
  },

  applyDefaultStation(defaultStationId) {
    if (!defaultStationId || this.data.stations.length === 0) return
    const index = this.data.stations.findIndex(s => s.id === defaultStationId)
    if (index >= 0 && index !== this.data.stationIndex) {
      this.chooseStationByIndex(index)
    }
  },

  chooseStationByIndex(index) {
    const station = this.data.stations[index]
    if (!station) return
    this.setData({ stationIndex: index, fuelIndex: 0, prices: [], estAmount: '0.00' })
    api.stationDetail(station.id).then(detail => {
      this.setData({ prices: (detail && detail.prices) || [] })
      this.recalc()
    }).catch(() => {})
  },

  onStationChange(e) {
    this.chooseStationByIndex(Number(e.detail.value))
  },

  onFuelChange(e) {
    this.setData({ fuelIndex: Number(e.currentTarget.dataset.index) })
    this.recalc()
  },

  onLitersTap(e) {
    const value = Number(e.currentTarget.dataset.value)
    this.setData({ liters: value, litersOption: value })
    this.recalc()
  },

  onLitersInput(e) {
    this.setData({ liters: e.detail.value, litersOption: -1 })
    this.recalc()
  },

  recalc() {
    const price = this.currentPrice()
    const liters = Number(this.data.liters)
    const estAmount = price && liters ? (Number(price.salePrice) * liters).toFixed(2) : '0.00'
    this.setData({ estAmount })
  },

  onMinutesTap(e) {
    this.setData({ minutes: Number(e.currentTarget.dataset.value), customMinutes: '' })
  },

  onCustomMinutes(e) {
    this.setData({ customMinutes: e.detail.value })
  },

  onPlateInput(e) {
    this.setData({ plateNo: e.detail.value })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  toggleSetDefault() {
    this.setData({ setDefault: !this.data.setDefault })
  },

  currentStation() {
    return this.data.stations[this.data.stationIndex] || null
  },

  currentPrice() {
    return this.data.prices[this.data.fuelIndex] || null
  },

  loadActive() {
    return api.activeReserve().then(active => {
      if (active) {
        active.expectTimeText = fmtTime(active.expectTime)
        active.createTimeText = fmtTime(active.createTime)
      }
      this.setData({ active: active || null })
      this.clearTimer()
      if (active) {
        this.tickCountdown()
        this.timer = setInterval(() => {
          this.tickCountdown()
          // 轮询状态，油站确认/拒绝后实时更新页面
          if (!this._polling) {
            this._polling = true
            api.activeReserve().then(latest => {
              this._polling = false
              if (!latest) {
                this.setData({ active: null })
                this.clearTimer()
                this.loadRecords()
              }
            }).catch(() => { this._polling = false })
          }
        }, 5000)
      }
    }).catch(() => {})
  },

  tickCountdown() {
    const active = this.data.active
    if (!active) return
    const expect = parseTime(active.expectTime)
    if (!expect) return
    const diff = expect.getTime() - Date.now()
    let countdown
    if (diff <= 0) {
      countdown = '已过预计到达时间'
    } else {
      const minutes = Math.ceil(diff / 60000)
      countdown = minutes < 60 ? `预计 ${minutes} 分钟后到达` : `预计 ${Math.floor(minutes / 60)} 小时 ${minutes % 60} 分后到达`
    }
    this.setData({ countdown })
  },

  loadRecords() {
    return api.myReserves(1, 20).then(res => {
      const records = (res.records || []).map(item => {
        item.expectTimeText = fmtTime(item.expectTime)
        item.createTimeText = fmtTime(item.createTime)
        item.statusText = ['待确认', '已加油', '已拒绝', '已取消'][item.status] || '未知'
        return item
      })
      this.setData({ records })
    }).catch(() => {})
  },

  submit() {
    if (this.data.submitting) return
    const station = this.currentStation()
    const price = this.currentPrice()
    const plateNo = (this.data.plateNo || '').trim()
    if (!plateNo) {
      wx.showToast({ title: '请填写车牌号', icon: 'none' })
      return
    }
    if (!station) {
      wx.showToast({ title: '请选择加油站', icon: 'none' })
      return
    }
    if (!price) {
      wx.showToast({ title: '请选择油品', icon: 'none' })
      return
    }
    const liters = Number(this.data.liters)
    if (!liters || liters <= 0) {
      wx.showToast({ title: '请填写预加油量', icon: 'none' })
      return
    }
    const minutes = this.data.customMinutes ? Number(this.data.customMinutes) : this.data.minutes
    if (!minutes || minutes < 5) {
      wx.showToast({ title: '预计到达至少 5 分钟后', icon: 'none' })
      return
    }
    if (minutes > 720) {
      wx.showToast({ title: '预计到达不能超过 12 小时', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    api.createReserve({
      stationId: station.id,
      plateNo,
      fuelType: price.fuelType,
      liters,
      expectMinutes: minutes,
      setDefaultStation: this.data.setDefault,
      remark: this.data.remark
    }).then(() => {
      wx.showToast({ title: '预约成功', icon: 'success' })
      this.setData({ remark: '', customMinutes: '' })
      return this.loadActive()
    }).then(() => {
      this.loadRecords()
      this.loadMember()
    }).catch(() => {}).finally(() => {
      this.setData({ submitting: false })
    })
  },

  cancelActive() {
    const active = this.data.active
    if (!active) return
    wx.showModal({
      title: '取消预约',
      content: '确认取消该预约加油订单？',
      success: res => {
        if (!res.confirm) return
        api.cancelReserve(active.id).then(() => {
          wx.showToast({ title: '已取消', icon: 'success' })
          this.setData({ active: null })
          this.clearTimer()
          this.loadRecords()
        })        .catch(() => {
          this.loadActive()
          this.loadRecords()
        })
      }
    })
  }
})
