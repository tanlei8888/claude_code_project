// 用户状态管理 — 管理登录/注册/信息获取/退出等认证流程，token 持久化于 localStorage
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getInfo, updateProfile, type UserInfo } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  // 当前用户信息，未登录时为 null
  const info = ref<UserInfo | null>(null)
  // JWT 令牌，初始化时从 localStorage 恢复
  const token = ref<string | null>(getToken())

  // 登录：调用 API 获取 token，持久化后拉取用户信息
  async function login(username: string, password: string) {
    const res = await loginApi(username, password)
    token.value = res.token
    setToken(res.token)
    await fetchInfo()
  }

  // 注册：调用注册 API，不自动登录
  async function register(username: string, password: string, nickname: string) {
    await registerApi(username, password, nickname)
  }

  // 获取当前用户详情，失败时清空 info 防止脏数据
  async function fetchInfo() {
    try {
      info.value = await getInfo()
    } catch {
      info.value = null
    }
  }

  // 更新个人资料后重新拉取用户信息
  async function update(data: Record<string, string>) {
    await updateProfile(data)
    await fetchInfo()
  }

  // 退出登录：清空内存状态 + 移除 localStorage token
  function logout() {
    info.value = null
    token.value = null
    removeToken()
  }

  // 检查是否已登录（仅基于 token 是否存在做简单判断）
  const isLoggedIn = () => !!token.value

  return { info, token, login, register, fetchInfo, update, logout, isLoggedIn }
})
