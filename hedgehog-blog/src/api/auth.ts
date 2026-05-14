import request from './request'

export interface LoginResponse {
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
  avatar: string
  bio: string
  role: string
}

export function login(username: string, password: string): Promise<LoginResponse> {
  return request.post('/auth/login', { username, password })
}

export function register(username: string, password: string, nickname: string): Promise<void> {
  return request.post('/auth/register', { username, password, nickname })
}

export function getInfo(): Promise<UserInfo> {
  return request.get('/auth/info')
}

export function updateProfile(data: Record<string, string>): Promise<void> {
  return request.put('/auth/profile', data)
}
