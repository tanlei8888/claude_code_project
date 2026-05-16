// 文章相关 API — 获取文章列表/详情、分类、标签（均为公开接口）
import request from './request'

// 文章完整信息，含关联的标签、分类、作者及点赞状态
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

// 文章作者简要信息
export interface Author {
  id: number
  username: string
  nickname: string
  avatar: string
  bio: string
}

// 分页数据通用结构
export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// GET /api/articles — 获取已发布文章分页列表，支持分类/标签/关键词筛选
export function getArticles(params: {
  page?: number
  size?: number
  categoryId?: number
  tagId?: number
  keyword?: string
}): Promise<PageData<Article>> {
  return request.get('/articles', { params })
}

// GET /api/articles/{slug} — 根据 slug 获取文章详情（浏览量自动+1）
export function getArticleBySlug(slug: string): Promise<Article> {
  return request.get(`/articles/${slug}`)
}

// GET /api/categories — 获取全部分类列表
export function getCategories(): Promise<Category[]> {
  return request.get('/categories')
}

// GET /api/tags — 获取全部标签列表
export function getTags(): Promise<Tag[]> {
  return request.get('/tags')
}
