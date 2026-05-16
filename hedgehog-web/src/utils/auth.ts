// Token 工具 — JWT 令牌的读写与清除，基于 localStorage 持久化
const TOKEN_KEY = 'hedgehog-token'

// 从 localStorage 读取 token
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

// 将 token 写入 localStorage
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

// 从 localStorage 移除 token
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}
