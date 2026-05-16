// 站点配置 API — 获取站点名称、logo、关于页、社交链接等全局配置
import request from './request'

// 站点全局配置，由后台管理端维护
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

// GET /api/site/config — 获取站点全局配置（公开接口）
export function getSiteConfig(): Promise<SiteConfig> {
  return request.get('/site/config')
}
