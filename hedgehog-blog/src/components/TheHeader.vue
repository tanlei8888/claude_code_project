<template>
  <header class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
      <router-link to="/" class="text-lg sm:text-xl font-bold tracking-tight text-gray-900 hover:text-blue-600 transition-colors">
        {{ siteName }}
      </router-link>
      <!-- 桌面端导航 -->
      <nav class="hidden sm:flex items-center gap-6 text-sm text-gray-600">
        <router-link to="/" class="hover:text-gray-900 transition-colors">首页</router-link>
        <router-link to="/about" class="hover:text-gray-900 transition-colors">关于</router-link>
        <template v-if="userStore.isLoggedIn()">
          <span class="text-gray-400">{{ userStore.info?.nickname }}</span>
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
        <span class="block text-sm text-gray-400 py-1">{{ userStore.info?.nickname }}</span>
        <button @click="handleLogout(); menuOpen = false" class="block text-sm text-gray-600 py-1">退出</button>
      </template>
      <template v-else>
        <router-link to="/login" class="block text-sm text-gray-600 py-1" @click="menuOpen = false">登录</router-link>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getSiteConfig } from '@/api/site'

const router = useRouter()
const userStore = useUserStore()
const siteName = ref('Hedgehog')
const menuOpen = ref(false)

onMounted(async () => {
  try {
    const config = await getSiteConfig()
    siteName.value = config.siteName || 'Hedgehog'
  } catch {}
})

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>
