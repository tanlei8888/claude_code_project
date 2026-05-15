import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { message } from '@/utils/message'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      if (data.code === 401) {
        removeToken()
        window.location.hash = '#/login'
      }
      message.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data.data
  },
  (error) => {
    const serverMsg = error.response?.data?.message
    const msg = serverMsg || error.message || '网络异常'
    message.error(msg)
    return Promise.reject(new Error(msg))
  },
)

export default request
