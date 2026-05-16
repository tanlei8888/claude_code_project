// JWT 令牌工具 — 在 localStorage 中存取/移除认证 token
const TOKEN_KEY = 'hedgehog_blog_token'

// 从 localStorage 取出当前存储的 JWT 令牌
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

// 将 JWT 令牌写入 localStorage
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

// 退出登录时清除 localStorage 中的令牌
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}
