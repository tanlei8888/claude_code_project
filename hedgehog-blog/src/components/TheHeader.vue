<template>
  <header class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
      <router-link to="/" class="flex items-center gap-2 text-lg sm:text-xl font-bold tracking-tight text-gray-900 hover:text-blue-600 transition-colors">
        <img v-if="siteLogo" :src="siteLogo" :alt="siteName" class="h-8 w-auto object-contain rounded-xl shadow-sm ring-1 ring-gray-200/60 transition-transform duration-200 group-hover:scale-105" />
        {{ siteName }}
      </router-link>
      <!-- 桌面端导航 -->
      <nav class="hidden sm:flex items-center gap-6 text-sm text-gray-600">
        <router-link to="/" class="hover:text-gray-900 transition-colors">首页</router-link>
        <router-link to="/about" class="hover:text-gray-900 transition-colors">关于</router-link>
        <template v-if="userStore.isLoggedIn()">
          <router-link to="/profile" class="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors">
            <img :src="userStore.info?.avatar || defaultAvatar" class="w-6 h-6 rounded-full object-cover" />
            <span>{{ userStore.info?.nickname }}</span>
          </router-link>
          <button @click="handleLogout" class="text-gray-400 hover:text-red-500 transition-colors">退出</button>
        </template>
        <template v-else>
          <router-link to="/login" class="hover:text-gray-900 transition-colors">登录</router-link>
        </template>
      </nav>
      <!-- 移动端汉堡菜单 -->
      <button @click="menuOpen = !menuOpen" class="sm:hidden p-2 text-gray-600">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path v-if="!menuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
          <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 6l12 12M6 18L18 6"/>
        </svg>
      </button>
    </div>
    <!-- 移动端下拉菜单 -->
    <div v-if="menuOpen" class="sm:hidden border-t border-gray-100 bg-white px-4 py-3 space-y-2">
      <router-link to="/" class="block text-sm text-gray-600 py-1" @click="menuOpen = false">首页</router-link>
      <router-link to="/about" class="block text-sm text-gray-600 py-1" @click="menuOpen = false">关于</router-link>
      <template v-if="userStore.isLoggedIn()">
        <router-link to="/profile" class="flex items-center gap-2 py-1" @click="menuOpen = false">
          <img :src="userStore.info?.avatar || defaultAvatar" class="w-5 h-5 rounded-full object-cover" />
          <span class="text-sm text-gray-600">{{ userStore.info?.nickname }}</span>
        </router-link>
        <button @click="handleLogout(); menuOpen = false" class="block text-sm text-gray-600 py-1">退出</button>
      </template>
      <template v-else>
        <router-link to="/login" class="block text-sm text-gray-600 py-1" @click="menuOpen = false">登录</router-link>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
// 全局顶部导航栏 — 粘性定位，响应式（桌面导航栏 / 移动端汉堡菜单），展示站点名和登录状态
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getSiteConfig } from '@/api/site'

const router = useRouter()
const userStore = useUserStore()
const siteName = ref('Hedgehog')           // 站点名，优先使用配置中的 siteName
const siteLogo = ref('')                 // 站点 Logo URL
const menuOpen = ref(false)                // 移动端汉堡菜单展开状态
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCUiIHkxPSIwJSIgeDI9IjEwMCUiIHkyPSIxMDAlIj48c3RvcCBvZmZzZXQ9IjAlIiBzdG9wLWNvbG9yPSIjOTM1MkQzIi8+PHN0b3Agb2Zmc2V0PSIxMDAlIiBzdG9wLWNvbG9yPSIjNEM2REZGIi8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iMjAiIGZpbGw9InVybCgjZykiLz48L3N2Zz4='

// 挂载后从站点配置获取站点名和 Logo
onMounted(async () => {
  try {
    const config = await getSiteConfig()
    siteName.value = config.siteName || 'Hedgehog'
    siteLogo.value = config.siteLogo || ''
  } catch {}
})

// 退出登录：清除 token 并跳转首页
function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>
