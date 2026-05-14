import request from './request'
import type { ApiResponse } from './request'

export interface Tag {
  id: number
  name: string
  slug: string
}

export function getTags(): Promise<ApiResponse<Tag[]>> {
  return request.get('/tags')
}

export function createTag(data: any): Promise<ApiResponse<null>> {
  return request.post('/admin/tags', data)
}

export function updateTag(id: number, data: any): Promise<ApiResponse<null>> {
  return request.put(`/admin/tags/${id}`, data)
}

export function deleteTag(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/tags/${id}`)
}
