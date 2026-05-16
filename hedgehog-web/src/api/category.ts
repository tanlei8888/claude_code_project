// 分类管理 API — 公开查询 + 管理员 CRUD
import request from './request'
import type { ApiResponse } from './request'

export interface Category {
  id: number
  name: string
  slug: string
  description: string
  sortOrder: number     // 排序值，越小越靠前
}

// GET /api/categories — 获取全部分类列表（公开接口）
export function getCategories(): Promise<ApiResponse<Category[]>> {
  return request.get('/categories')
}

// POST /api/admin/categories — 创建分类
export function createCategory(data: any): Promise<ApiResponse<null>> {
  return request.post('/admin/categories', data)
}

// PUT /api/admin/categories/{id} — 更新分类
export function updateCategory(id: number, data: any): Promise<ApiResponse<null>> {
  return request.put(`/admin/categories/${id}`, data)
}

// DELETE /api/admin/categories/{id} — 删除分类（分类下有文章时后端会拒绝）
export function deleteCategory(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/categories/${id}`)
}
