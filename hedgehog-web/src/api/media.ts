import request from './request'
import type { ApiResponse } from './request'

export interface Media {
  id: number
  filename: string
  path: string
  url: string
  fileSize: number
  mimeType: string
  createTime: string
}

export function getMediaPage(params: {
  page?: number
  size?: number
}): Promise<ApiResponse<{ records: Media[]; total: number; size: number; current: number }>> {
  return request.get('/admin/media', { params })
}

export function uploadFile(file: File): Promise<ApiResponse<Media>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteMedia(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/media/${id}`)
}
