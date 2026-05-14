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
  status: number
  isTop: number
  viewCount: number
  likeCount: number
  commentCount: number
  publishTime: string
  authorId: number
  createTime: string
  updateTime: string
  tags: { id: number; name: string; slug: string }[]
  category: { id: number; name: string; slug: string } | null
  author: { id: number; nickname: string; username: string } | null
}

export function getArticlePage(params: {
  page?: number
  size?: number
  status?: number
  categoryId?: number
  keyword?: string
}): Promise<ApiResponse<{ records: Article[]; total: number; size: number; current: number }>> {
  return request.get('/admin/articles', { params })
}

export function getArticleById(id: number): Promise<ApiResponse<Article>> {
  return request.get(`/admin/articles/${id}`)
}

export function createArticle(data: any): Promise<ApiResponse<null>> {
  return request.post('/admin/articles', data)
}

export function updateArticle(id: number, data: any): Promise<ApiResponse<null>> {
  return request.put(`/admin/articles/${id}`, data)
}

export function deleteArticle(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/articles/${id}`)
}

export function updateArticleStatus(id: number, status: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/articles/${id}/status`, null, { params: { status } })
}

export function toggleArticleTop(id: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/articles/${id}/top`)
}
