// 评论相关 API — 获取评论树、发表评论（需登录）
import request from './request'

// 评论结构，支持二层嵌套：顶级评论含 replies 数组
export interface Comment {
  id: number
  articleId: number
  userId: number
  parentId: number | null
  replyToUserId: number | null
  content: string
  status: number
  createTime: string
  user: { id: number; username: string; nickname: string; avatar: string }
  replyToUser: { id: number; username: string; nickname: string; avatar: string } | null
  replies: Comment[]
}

// GET /api/comments — 获取指定文章的评论树（顶级评论分页 + 二级回复）
export function getComments(articleId: number, page = 1, size = 10): Promise<Comment[]> {
  return request.get('/comments', { params: { articleId, page, size } })
}

// POST /api/comments — 发表评论或回复（需登录）
export function createComment(data: {
  articleId: number
  content: string
  parentId?: number
  replyToUserId?: number
}): Promise<void> {
  return request.post('/comments', data)
}
