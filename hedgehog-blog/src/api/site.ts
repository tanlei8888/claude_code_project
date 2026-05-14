import request from './request'

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

export function getSiteConfig(): Promise<SiteConfig> {
  return request.get('/site/config')
}
