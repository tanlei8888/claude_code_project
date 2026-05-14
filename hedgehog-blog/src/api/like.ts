import request from './request'

export function toggleLike(articleId: number): Promise<boolean> {
  return request.post(`/likes/${articleId}`)
}

export function getLikeStatus(articleIds: number[]): Promise<Record<number, boolean>> {
  return request.get('/likes/status', { params: { articleIds: articleIds.join(',') } })
}
