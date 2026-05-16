// 媒体管理 API — 上传/分页/删除/分类
import request from './request'
import type { ApiResponse } from './request'

export interface Media {
  id: number
  filename: string
  path: string              // 服务端存储相对路径
  url: string               // 完整访问 URL
  fileSize: number
  mimeType: string
  mediaType: string         // CONTENT | AVATAR | PRIVATE
  createTime: string
}

// GET /api/admin/media — 分页查询媒体列表
export function getMediaPage(params: {
  page?: number
  size?: number
}): Promise<ApiResponse<{ records: Media[]; total: number; size: number; current: number }>> {
  return request.get('/admin/media', { params })
}

// POST /api/admin/upload — 上传文件（multipart/form-data，支持指定媒体分类）
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

// DELETE /api/admin/media/{id} — 删除媒体文件
export function deleteMedia(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/media/${id}`)
}

// PUT /api/admin/media/{id} — 更新媒体分类类型
export function updateMediaType(id: number, mediaType: string): Promise<ApiResponse<null>> {
  return request.put(`/admin/media/${id}`, { mediaType })
}
