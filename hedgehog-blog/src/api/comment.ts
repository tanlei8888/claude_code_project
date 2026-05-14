import request from './request'

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

export function getComments(articleId: number, page = 1, size = 10): Promise<Comment[]> {
  return request.get('/comments', { params: { articleId, page, size } })
}

export function createComment(data: {
  articleId: number
  content: string
  parentId?: number
  replyToUserId?: number
}): Promise<void> {
  return request.post('/comments', data)
}
