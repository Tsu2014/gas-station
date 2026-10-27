const config = require('./config')

App({
  globalData: {
    token: '',
    member: null
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
    }
  },

  getToken() {
    return this.globalData.token || wx.getStorageSync('token') || ''
  },

  isLogin() {
    return !!this.getToken()
  },

  /** 微信登录：code 换取 openid 并注册/登录会员 */
  login() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: res => {
          wx.request({
            url: config.baseUrl + '/api/auth/mini-login',
            method: 'POST',
            data: { code: res.code },
            success: result => {
              const body = result.data
              if (body && body.code === 0) {
                this.globalData.token = body.data.token
                this.globalData.member = body.data
                wx.setStorageSync('token', body.data.token)
                resolve(body.data)
              } else {
                wx.showToast({ title: (body && body.msg) || '登录失败', icon: 'none' })
                reject(body)
              }
            },
            fail: reject
          })
        },
        fail: reject
      })
    })
  },

  logout() {
    this.globalData.token = ''
    this.globalData.member = null
    wx.removeStorageSync('token')
  }
})
