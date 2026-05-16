// 评论管理 API — 管理员审核 + 删除
import request from './request'
import type { ApiResponse } from './request'

export interface Comment {
  id: number
  articleId: number
  userId: number
  parentId: number | null           // 父评论 ID，null=顶级评论
  replyToUserId: number | null       // 回复目标用户 ID
  content: string
  status: number                     // 0=待审核 1=已通过 2=已拒绝
  createTime: string
  user: { id: number; username: string; nickname: string } | null
}

// GET /api/admin/comments — 分页查询评论（支持文章、状态筛选）
export function getCommentPage(params: {
  articleId?: number
  status?: number
  page?: number
  size?: number
}): Promise<ApiResponse<{ records: Comment[]; total: number; size: number; current: number }>> {
  return request.get('/admin/comments', { params })
}

// PUT /api/admin/comments/{id} — 审核评论（通过/拒绝）
export function auditComment(id: number, status: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/comments/${id}`, null, { params: { status } })
}

// DELETE /api/admin/comments/{id} — 删除评论
export function deleteComment(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/comments/${id}`)
}
