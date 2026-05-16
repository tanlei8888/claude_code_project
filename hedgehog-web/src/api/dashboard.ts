// 仪表盘 API — 后台首页统计数据
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
    status: number          // 0=草稿 1=已发布 2=定时发布 3=私密
    viewCount: number
    createTime: string
  }>
}

// GET /api/admin/dashboard — 获取仪表盘统计数据（计数 + 最近文章列表）
export function getDashboard(): Promise<ApiResponse<DashboardData>> {
  return request.get('/admin/dashboard')
}
