import request, { type ApiResponse } from './request'

export interface UserPageParams {
  page: number
  size: number
  keyword?: string
}

export interface UserRecord {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  status: number
  createTime: string
}

export interface UserPageResult {
  records: UserRecord[]
  total: number
}

export interface UserSaveParams {
  id?: number
  username: string
  password?: string
  nickname?: string
  email?: string
  phone?: string
  status?: number
}

export function getUserPage(params: UserPageParams): Promise<ApiResponse<UserPageResult>> {
  return request({ url: '/users', method: 'get', params }) as Promise<ApiResponse<UserPageResult>>
}

export function getUserById(id: number): Promise<ApiResponse<UserRecord>> {
  return request({ url: `/users/${id}`, method: 'get' }) as Promise<ApiResponse<UserRecord>>
}

export function saveUser(data: UserSaveParams): Promise<ApiResponse<null>> {
  return request({ url: '/users', method: 'post', data }) as Promise<ApiResponse<null>>
}

export function updateUser(id: number, data: UserSaveParams): Promise<ApiResponse<null>> {
  return request({ url: `/users/${id}`, method: 'put', data }) as Promise<ApiResponse<null>>
}

export function deleteUser(id: number): Promise<ApiResponse<null>> {
  return request({ url: `/users/${id}`, method: 'delete' }) as Promise<ApiResponse<null>>
}
