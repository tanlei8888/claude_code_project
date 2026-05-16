// SEO 元信息管理 — 统一设置页面 title、description、keywords、Open Graph 及文章专属标签
import { useHead } from '@unhead/vue'

interface SeoInput {
  title?: string
  description?: string
  keywords?: string
  ogTitle?: string
  ogDescription?: string
  ogImage?: string
  ogType?: 'website' | 'article'
  ogUrl?: string
  articlePublishedTime?: string
  articleAuthor?: string
  articleTags?: string[]
}

export function useSeo(input: SeoInput) {
  const meta: Record<string, string>[] = []

  // 标准 meta 标签
  if (input.description) {
    meta.push({ name: 'description', content: input.description })
  }
  if (input.keywords) {
    meta.push({ name: 'keywords', content: input.keywords })
  }

  // Open Graph 标签（社交媒体分享预览），ogTitle/ogDesc 未传时回退到 title/description
  const ogTitle = input.ogTitle || input.title
  if (ogTitle) meta.push({ property: 'og:title', content: ogTitle })
  const ogDesc = input.ogDescription || input.description
  if (ogDesc) meta.push({ property: 'og:description', content: ogDesc })
  if (input.ogImage) {
    meta.push({ property: 'og:image', content: input.ogImage })
  }
  if (input.ogType) meta.push({ property: 'og:type', content: input.ogType })
  if (input.ogUrl) meta.push({ property: 'og:url', content: input.ogUrl })

  // 文章专属 Open Graph 标签
  if (input.articlePublishedTime) {
    meta.push({ property: 'article:published_time', content: input.articlePublishedTime })
  }
  if (input.articleAuthor) {
    meta.push({ property: 'article:author', content: input.articleAuthor })
  }
  if (input.articleTags?.length) {
    input.articleTags.forEach(tag => meta.push({ property: 'article:tag', content: tag }))
  }

  useHead({
    title: input.title || 'Hedgehog Blog',
    meta,
  })
}
