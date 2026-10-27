const config = require('../config')
const app = getApp()

/**
 * 统一请求封装：自动携带 Token，401 自动重新登录并重试
 */
function request(options) {
  const token = app.getToken ? app.getToken() : wx.getStorageSync('token')
  return new Promise((resolve, reject) => {
    wx.request({
      url: config.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'content-type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : ''
      },
      success(res) {
        const body = res.data
        if (body && body.code === 0) {
          resolve(body.data)
          return
        }
        if (body && body.code === 401) {
          app.login().then(() => {
            request(options).then(resolve, reject)
          }).catch(reject)
          return
        }
        wx.showToast({ title: (body && body.msg) || '请求失败', icon: 'none' })
        reject(body)
      },
      fail: reject
    })
  })
}

module.exports = {
  request,
  get: (url, data) => request({ url, method: 'GET', data }),
  post: (url, data) => request({ url, method: 'POST', data }),
  put: (url, data) => request({ url, method: 'PUT', data })
}
