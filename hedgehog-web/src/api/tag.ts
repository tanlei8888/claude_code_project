// 标签管理 API — 公开查询 + 管理员 CRUD
import request from './request'
import type { ApiResponse } from './request'

export interface Tag {
  id: number
  name: string
  slug: string
}

// GET /api/tags — 获取全部标签列表（公开接口）
export function getTags(): Promise<ApiResponse<Tag[]>> {
  return request.get('/tags')
}

// POST /api/admin/tags — 创建标签
export function createTag(data: any): Promise<ApiResponse<null>> {
  return request.post('/admin/tags', data)
}

// PUT /api/admin/tags/{id} — 更新标签
export function updateTag(id: number, data: any): Promise<ApiResponse<null>> {
  return request.put(`/admin/tags/${id}`, data)
}

// DELETE /api/admin/tags/{id} — 删除标签
export function deleteTag(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/tags/${id}`)
}
