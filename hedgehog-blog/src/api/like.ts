// 点赞相关 API — 点赞/取消点赞（toggle）、批量查询点赞状态
import request from './request'

// POST /api/likes/{articleId} — 点赞切换：未点则点赞，已点则取消，返回最终点赞状态
export function toggleLike(articleId: number): Promise<boolean> {
  return request.post(`/likes/${articleId}`)
}

// GET /api/likes/status — 批量查询当前用户对指定文章的点赞状态，返回 { articleId: boolean }
export function getLikeStatus(articleIds: number[]): Promise<Record<number, boolean>> {
  return request.get('/likes/status', { params: { articleIds: articleIds.join(',') } })
}
