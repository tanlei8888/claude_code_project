// Axios 请求实例 — baseURL 指向 /api，自动注入 JWT，统一处理响应/错误
import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { message } from '@/utils/message'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器：自动从 localStorage 取出 token 注入 Authorization 头
request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：code !== 200 统一提示；401 额外清空 token 跳登录；网络异常提示兜底
request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      // 401 未授权 → 清除登录状态并跳转登录页
      if (data.code === 401) {
        removeToken()
        router.push('/login')
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
