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
    info: null,
  }),

  getters: {
    isAdmin: (state) => state.info?.role === 'ADMIN',
  },

  actions: {
    async fetchInfo() {
      const res = await getInfo()
      this.info = res.data
    },

    logout() {
      removeToken()
      this.info = null
      router.push('/login')
    },
  },
})
