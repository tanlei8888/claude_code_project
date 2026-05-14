import request from './request'
import type { ApiResponse } from './request'

export interface Category {
  id: number
  name: string
  slug: string
  description: string
  sortOrder: number
}

export function getCategories(): Promise<ApiResponse<Category[]>> {
  return request.get('/categories')
}

export function createCategory(data: any): Promise<ApiResponse<null>> {
  return request.post('/admin/categories', data)
}

export function updateCategory(id: number, data: any): Promise<ApiResponse<null>> {
  return request.put(`/admin/categories/${id}`, data)
}

export function deleteCategory(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/categories/${id}`)
}
