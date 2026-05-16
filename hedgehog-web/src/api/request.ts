// Axios 请求封装 — 统一 baseURL、超时与拦截器，返回 ApiResponse<T> 结构
import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 后端统一响应结构
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

const instance = axios.create({
  baseURL: '/api',            // Vite proxy 转发至 localhost:8080
  timeout: 15000,             // 15 秒超时
})

// 请求拦截器：自动携带 JWT token
instance.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：code 非 200 统一弹窗提示；401 清空 token 并跳转登录
instance.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      removeToken()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  },
)

export default instance
