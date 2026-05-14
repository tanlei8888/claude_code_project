import request from './request'
import type { ApiResponse } from './request'

export interface DashboardData {
  articleCount: number
  categoryCount: number
  tagCount: number
  commentCount: number
  totalViews: number
  recentArticles: Array<{
    id: number
    title: string
    status: number
    viewCount: number
    createTime: string
  }>
}

export function getDashboard(): Promise<ApiResponse<DashboardData>> {
  return request.get('/admin/dashboard')
}
