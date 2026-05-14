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
import TheHeader from '@/components/TheHeader.vue'
import TheFooter from '@/components/TheFooter.vue'
</script>
