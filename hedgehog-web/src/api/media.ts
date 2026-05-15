import request from './request'
import type { ApiResponse } from './request'

export interface Media {
  id: number
  filename: string
  path: string
  url: string
  fileSize: number
  mimeType: string
  mediaType: string
  createTime: string
}

export function getMediaPage(params: {
  page?: number
  size?: number
}): Promise<ApiResponse<{ records: Media[]; total: number; size: number; current: number }>> {
  return request.get('/admin/media', { params })
}

export function uploadFile(file: File, mediaType?: string): Promise<ApiResponse<Media>> {
  const formData = new FormData()
  formData.append('file', file)
  if (mediaType) {
    formData.append('mediaType', mediaType)
  }
  return request.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteMedia(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/media/${id}`)
}

export function updateMediaType(id: number, mediaType: string): Promise<ApiResponse<null>> {
  return request.put(`/admin/media/${id}`, { mediaType })
}
