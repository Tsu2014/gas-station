import request from '@/utils/request'

export function summary() {
  return request({ url: '/admin/dashboard/summary', method: 'get' })
}
