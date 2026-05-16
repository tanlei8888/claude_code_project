// 认证相关 API — 登录、注册、获取用户信息、修改资料、获取头像列表
import request from './request'

// 登录成功返回数据：token 和用户基本信息
export interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  role: string
}

// 当前登录用户完整信息
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  bio: string
  role: string
}

// POST /api/auth/login — 用户名+密码登录，返回 JWT 令牌与用户信息
export function login(username: string, password: string): Promise<LoginResponse> {
  return request.post('/auth/login', { username, password })
}

// POST /api/auth/register — 用户名+密码+昵称注册
export function register(username: string, password: string, nickname: string): Promise<void> {
  return request.post('/auth/register', { username, password, nickname })
}

// GET /api/auth/info — 获取当前登录用户的详细信息
export function getInfo(): Promise<UserInfo> {
  return request.get('/auth/info')
}

// PUT /api/auth/profile — 修改个人资料（昵称/邮箱/头像/简介/密码等）
export function updateProfile(data: Record<string, string>): Promise<void> {
  return request.put('/auth/profile', data)
}

export interface AvatarItem {
  id: number
  url: string
  filename: string
}

// GET /api/auth/avatars — 获取可用头像列表供用户选择
export function getAvatars(): Promise<AvatarItem[]> {
  return request.get('/auth/avatars')
}
