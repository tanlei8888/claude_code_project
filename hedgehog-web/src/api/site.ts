import request from './request'
import type { ApiResponse } from './request'

export interface SiteConfig {
  id: number
  siteName: string
  siteSubtitle: string
  siteLogo: string
  siteFavicon: string
  aboutContentMd: string
  aboutContentHtml: string
  authorName: string
  authorAvatar: string
  authorBio: string
  socialGithub: string
  socialTwitter: string
  socialZhihu: string
  icpNumber: string
  footerText: string
}

export function getSiteConfig(): Promise<ApiResponse<SiteConfig>> {
  return request.get('/site/config')
}

export function updateSiteConfig(data: any): Promise<ApiResponse<null>> {
  return request.put('/admin/site/config', data)
}
