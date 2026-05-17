<template>
  <div class="min-h-screen flex flex-col bg-white text-gray-900">
    <TheHeader />
    <main class="flex-1">
      <router-view />
    </main>
    <TheFooter />
  </div>
</template>

<script setup lang="ts">
// 根组件 — 全局布局框架：顶部导航 + 路由视图 + 页脚，挂载时恢复登录态并应用站点配置
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getSiteConfig } from '@/api/site'
import TheHeader from '@/components/TheHeader.vue'
import TheFooter from '@/components/TheFooter.vue'

const userStore = useUserStore()

// 设置页面 favicon，移除旧的 favicon link 并创建新的
function setFavicon(url: string) {
  const head = document.head
  // 移除已有的 favicon
  head.querySelectorAll('link[rel="icon"], link[rel="shortcut icon"]').forEach(el => el.remove())
  if (!url) return
  const link = document.createElement('link')
  link.rel = 'icon'
  link.href = url
  head.appendChild(link)
}

// 页面刷新或首次打开时，恢复登录态并应用站点全局配置
onMounted(async () => {
  if (userStore.isLoggedIn()) {
    await userStore.fetchInfo()
  }
  try {
    const config = await getSiteConfig()
    if (config.siteName) {
      document.title = config.siteName
    }
    setFavicon(config.siteFavicon)
  } catch {}
})
</script>
