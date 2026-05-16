<template>
  <div class="max-w-4xl mx-auto px-6 py-12">
    <div v-if="config" class="text-center mb-12">
      <img v-if="config.authorAvatar" :src="config.authorAvatar" class="w-24 h-24 rounded-full mx-auto mb-4" />
      <h1 class="text-2xl font-bold text-gray-900 mb-2">{{ config.authorName || config.siteName }}</h1>
      <p class="text-gray-500">{{ config.authorBio || config.siteSubtitle }}</p>
      <div class="flex justify-center gap-4 mt-4">
        <a v-if="config.socialGithub" :href="config.socialGithub" target="_blank" class="text-gray-400 hover:text-gray-600 transition-colors text-sm">GitHub</a>
        <a v-if="config.socialTwitter" :href="config.socialTwitter" target="_blank" class="text-gray-400 hover:text-gray-600 transition-colors text-sm">Twitter</a>
        <a v-if="config.socialZhihu" :href="config.socialZhihu" target="_blank" class="text-gray-400 hover:text-gray-600 transition-colors text-sm">知乎</a>
      </div>
    </div>
    <div v-if="config?.aboutContentHtml" class="markdown-body" v-html="config.aboutContentHtml"></div>
    <div v-else class="text-center text-gray-400 py-10">暂无关于信息</div>
  </div>
</template>

<script setup lang="ts">
// 关于页 — 展示作者信息、社交链接和站点"关于"内容（Markdown 转 HTML）
import { ref, onMounted } from 'vue'
import { getSiteConfig, type SiteConfig } from '@/api/site'

const config = ref<SiteConfig | null>(null) // 站点配置，包含作者信息和关于内容

// 挂载后获取站点配置
onMounted(async () => {
  try {
    config.value = await getSiteConfig()
  } catch {}
})
</script>
