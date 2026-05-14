import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getInfo, updateProfile, type UserInfo } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const info = ref<UserInfo | null>(null)
  const token = ref<string | null>(getToken())

  async function login(username: string, password: string) {
    const res = await loginApi(username, password)
    token.value = res.token
    setToken(res.token)
    await fetchInfo()
  }

  async function register(username: string, password: string, nickname: string) {
    await registerApi(username, password, nickname)
  }

  async function fetchInfo() {
    try {
      info.value = await getInfo()
    } catch {
      info.value = null
    }
  }

  async function update(data: Record<string, string>) {
    await updateProfile(data)
    await fetchInfo()
  }

  function logout() {
    info.value = null
    token.value = null
    removeToken()
  }

  const isLoggedIn = () => !!token.value

  return { info, token, login, register, fetchInfo, update, logout, isLoggedIn }
})
