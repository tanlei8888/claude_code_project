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
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import TheHeader from '@/components/TheHeader.vue'
import TheFooter from '@/components/TheFooter.vue'

const userStore = useUserStore()

// 页面刷新或首次打开时，若 token 尚在有效期内则恢复用户信息
onMounted(async () => {
  if (userStore.isLoggedIn()) {
    await userStore.fetchInfo()
  }
})
</script>
