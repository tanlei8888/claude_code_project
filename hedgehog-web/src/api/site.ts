// 站点配置 API — 公开查询 + 管理员更新
import request from './request'
import type { ApiResponse } from './request'

export interface SiteConfig {
  id: number
  siteName: string
  siteSubtitle: string
  siteLogo: string
  siteFavicon: string
  aboutContentMd: string      // 关于页 Markdown 原文
  aboutContentHtml: string    // 关于页渲染后 HTML
  authorName: string
  authorAvatar: string
  authorBio: string
  socialGithub: string
  socialTwitter: string
  socialZhihu: string
  icpNumber: string           // ICP 备案号
  footerText: string          // 页脚自定义文字
}

// GET /api/site/config — 获取站点配置（公开接口）
export function getSiteConfig(): Promise<ApiResponse<SiteConfig>> {
  return request.get('/site/config')
}

// PUT /api/admin/site/config — 更新站点配置（含关于页 Markdown）
export function updateSiteConfig(data: any): Promise<ApiResponse<null>> {
  return request.put('/admin/site/config', data)
}
