// 文章管理 API — 管理员 CRUD + 状态/置顶操作
import request from './request'
import type { ApiResponse } from './request'

export interface Article {
  id: number
  title: string
  slug: string
  summary: string
  contentMd: string
  contentHtml: string
  coverImage: string
  categoryId: number
  status: number          // 0=草稿 1=已发布 2=定时发布 3=私密
  isTop: number           // 0=否 1=是
  viewCount: number
  likeCount: number
  commentCount: number
  publishTime: string     // 定时发布时间
  authorId: number
  createTime: string
  updateTime: string
  tags: { id: number; name: string; slug: string }[]
  category: { id: number; name: string; slug: string } | null
  author: { id: number; nickname: string; username: string } | null
}

// GET /api/admin/articles — 分页查询文章（支持状态、分类、关键词筛选）
export function getArticlePage(params: {
  page?: number
  size?: number
  status?: number
  categoryId?: number
  keyword?: string
}): Promise<ApiResponse<{ records: Article[]; total: number; size: number; current: number }>> {
  return request.get('/admin/articles', { params })
}

// GET /api/admin/articles/{id} — 查询文章详情
export function getArticleById(id: number): Promise<ApiResponse<Article>> {
  return request.get(`/admin/articles/${id}`)
}

// POST /api/admin/articles — 创建文章
export function createArticle(data: any): Promise<ApiResponse<null>> {
  return request.post('/admin/articles', data)
}

// PUT /api/admin/articles/{id} — 更新文章
export function updateArticle(id: number, data: any): Promise<ApiResponse<null>> {
  return request.put(`/admin/articles/${id}`, data)
}

// DELETE /api/admin/articles/{id} — 删除文章
export function deleteArticle(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/articles/${id}`)
}

// PUT /api/admin/articles/{id}/status — 修改文章状态（发布/草稿/定时/私密）
export function updateArticleStatus(id: number, status: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/articles/${id}/status`, null, { params: { status } })
}

// PUT /api/admin/articles/{id}/top — 切换置顶状态
export function toggleArticleTop(id: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/articles/${id}/top`)
}
