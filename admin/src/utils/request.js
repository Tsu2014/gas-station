import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  timeout: 20000
})

service.interceptors.request.use(config => {
  const token = store.state.user.token
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => Promise.reject(error))

service.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 0) {
    Message({ message: res.msg || '请求失败', type: 'error', duration: 3000 })
    if (res.code === 401) {
      store.dispatch('user/logout').then(() => {
        router.push('/login')
      })
    }
    return Promise.reject(new Error(res.msg || '请求失败'))
  }
  return res.data
}, error => {
  Message({ message: error.message || '网络异常', type: 'error', duration: 3000 })
  return Promise.reject(error)
})

export default service
