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
  status: number
  role: string
  avatar: string
  bio: string
}

export function login(data: LoginParams): Promise<ApiResponse<LoginResult>> {
  return request({ url: '/auth/login', method: 'post', data }) as Promise<ApiResponse<LoginResult>>
}

export function getInfo(): Promise<ApiResponse<UserInfo>> {
  return request({ url: '/auth/info', method: 'get' }) as Promise<ApiResponse<UserInfo>>
}
