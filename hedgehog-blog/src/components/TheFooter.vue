<template>
  <footer class="border-t border-gray-100 py-8 mt-16">
    <div class="max-w-4xl mx-auto px-6 text-center text-sm text-gray-400">
      <p v-if="config">{{ config.footerText || `© ${new Date().getFullYear()} ${config.siteName || 'Hedgehog'}. All rights reserved.` }}</p>
      <p v-if="config?.icpNumber" class="mt-1">
        <a href="https://beian.miit.gov.cn" target="_blank" rel="noopener" class="hover:text-gray-600">{{ config.icpNumber }}</a>
      </p>
    </div>
  </footer>
</template>

<script setup lang="ts">
// 全局页脚 — 显示版权信息、备案号，数据来自站点配置
import { ref, onMounted } from 'vue'
import { getSiteConfig, type SiteConfig } from '@/api/site'

const config = ref<SiteConfig | null>(null) // 站点配置，用于渲染页脚文字和备案号

// 挂载后获取站点配置
onMounted(async () => {
  try {
    config.value = await getSiteConfig()
  } catch {}
})
</script>
