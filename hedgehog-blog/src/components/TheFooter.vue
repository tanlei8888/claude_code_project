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
import { ref, onMounted } from 'vue'
import { getSiteConfig, type SiteConfig } from '@/api/site'

const config = ref<SiteConfig | null>(null)

onMounted(async () => {
  try {
    config.value = await getSiteConfig()
  } catch {}
})
</script>
