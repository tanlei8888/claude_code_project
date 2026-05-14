import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'

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
      return Promise.reject(new Error(data.message || 'Error'))
    }
    return data.data
  },
  (error) => {
    return Promise.reject(error)
  },
)

export default request
