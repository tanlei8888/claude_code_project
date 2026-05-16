// 用户管理 API — 管理员 CRUD
import request, { type ApiResponse } from './request'

export interface UserPageParams {
  page: number
  size: number
  keyword?: string      // 按用户名/昵称模糊搜索
}

export interface UserRecord {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  bio: string
  role: string          // ADMIN / USER
  status: number        // 0=禁用 1=启用
  createTime: string
}

export interface UserPageResult {
  records: UserRecord[]
  total: number
}

export interface UserSaveParams {
  id?: number
  username: string
  password?: string     // 编辑时留空则不修改密码
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  bio?: string
  role?: string
  status?: number
}

// GET /api/users — 分页查询用户（支持关键词搜索）
export function getUserPage(params: UserPageParams): Promise<ApiResponse<UserPageResult>> {
  return request({ url: '/users', method: 'get', params }) as Promise<ApiResponse<UserPageResult>>
}

// GET /api/users/{id} — 查询单个用户详情
export function getUserById(id: number): Promise<ApiResponse<UserRecord>> {
  return request({ url: `/users/${id}`, method: 'get' }) as Promise<ApiResponse<UserRecord>>
}

// POST /api/users — 新增用户
export function saveUser(data: UserSaveParams): Promise<ApiResponse<null>> {
  return request({ url: '/users', method: 'post', data }) as Promise<ApiResponse<null>>
}

// PUT /api/users/{id} — 更新用户
export function updateUser(id: number, data: UserSaveParams): Promise<ApiResponse<null>> {
  return request({ url: `/users/${id}`, method: 'put', data }) as Promise<ApiResponse<null>>
}

// DELETE /api/users/{id} — 删除用户
export function deleteUser(id: number): Promise<ApiResponse<null>> {
  return request({ url: `/users/${id}`, method: 'delete' }) as Promise<ApiResponse<null>>
}
