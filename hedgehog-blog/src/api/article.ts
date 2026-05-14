import request from './request'

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
  tags: Tag[]
  category: Category
  author: Author
  liked: boolean
}

export interface Tag {
  id: number
  name: string
  slug: string
}

export interface Category {
  id: number
  name: string
  slug: string
  description: string
}

export interface Author {
  id: number
  username: string
  nickname: string
  avatar: string
  bio: string
}

export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export function getArticles(params: {
  page?: number
  size?: number
  categoryId?: number
  tagId?: number
  keyword?: string
}): Promise<PageData<Article>> {
  return request.get('/articles', { params })
}

export function getArticleBySlug(slug: string): Promise<Article> {
  return request.get(`/articles/${slug}`)
}

export function getCategories(): Promise<Category[]> {
  return request.get('/categories')
}

export function getTags(): Promise<Tag[]> {
  return request.get('/tags')
}
