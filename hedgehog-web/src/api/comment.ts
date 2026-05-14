import request from './request'
import type { ApiResponse } from './request'

export interface Comment {
  id: number
  articleId: number
  userId: number
  parentId: number | null
  replyToUserId: number | null
  content: string
  status: number
  createTime: string
  user: { id: number; username: string; nickname: string } | null
}

export function getCommentPage(params: {
  articleId?: number
  status?: number
  page?: number
  size?: number
}): Promise<ApiResponse<{ records: Comment[]; total: number; size: number; current: number }>> {
  return request.get('/admin/comments', { params })
}

export function auditComment(id: number, status: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/comments/${id}`, null, { params: { status } })
}

export function deleteComment(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/comments/${id}`)
}
