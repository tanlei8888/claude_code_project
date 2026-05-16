// 认证 API — 登录/获取用户信息
import request, { type ApiResponse } from './request'

interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string
  role: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  status: number       // 0=禁用 1=启用
  role: string          // ADMIN / USER
  avatar: string
  bio: string
}

// POST /api/auth/login — 用户登录，返回 JWT token 与基本信息
export function login(data: LoginParams): Promise<ApiResponse<LoginResult>> {
  return request({ url: '/auth/login', method: 'post', data }) as Promise<ApiResponse<LoginResult>>
}

// GET /api/auth/info — 获取当前登录用户详细信息
export function getInfo(): Promise<ApiResponse<UserInfo>> {
  return request({ url: '/auth/info', method: 'get' }) as Promise<ApiResponse<UserInfo>>
}
