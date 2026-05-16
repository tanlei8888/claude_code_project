// 用户状态 Store — 管理当前登录用户信息、角色判断、登出
import { defineStore } from 'pinia'
import { getInfo } from '@/api/auth'
import { removeToken } from '@/utils/auth'
import router from '@/router'

interface UserInfo {
  id: number
  username: string
  nickname: string
  role: string
}

export const useUserStore = defineStore('user', {
  state: (): { info: UserInfo | null } => ({
    info: null,           // 当前登录用户信息，未登录时为 null
  }),

  getters: {
    // 是否管理员角色
    isAdmin: (state) => state.info?.role === 'ADMIN',
  },

  actions: {
    // 请求后端获取当前用户信息并填充 state
    async fetchInfo() {
      const res = await getInfo()
      this.info = res.data
    },

    // 退出登录：清除 token 和用户信息，跳转登录页
    logout() {
      removeToken()
      this.info = null
      router.push('/login')
    },
  },
})
