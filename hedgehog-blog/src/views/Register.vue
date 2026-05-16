<template>
  <div class="max-w-sm mx-auto px-6 py-20">
    <h1 class="text-2xl font-bold text-center text-gray-900 mb-8">注册</h1>
    <form @submit.prevent="handleRegister" class="space-y-4">
      <div>
        <input v-model="username" type="text" placeholder="用户名" required
          class="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div>
        <input v-model="nickname" type="text" placeholder="昵称" required
          class="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div>
        <input v-model="password" type="password" placeholder="密码" required
          class="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div v-if="error" class="text-sm text-red-500 text-center">{{ error }}</div>
      <button type="submit" :disabled="loading"
        class="w-full py-3 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors">
        {{ loading ? '注册中...' : '注册' }}
      </button>
      <p class="text-center text-sm text-gray-400">
        已有账号？<router-link to="/login" class="text-blue-600 hover:underline">立即登录</router-link>
      </p>
    </form>
  </div>
</template>

<script setup lang="ts">
// 注册页 — 用户名+昵称+密码表单，注册成功后自动登录并跳转首页
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')    // 用户名输入
const nickname = ref('')    // 昵称输入
const password = ref('')    // 密码输入
const error = ref('')      // 注册错误信息
const loading = ref(false)  // 注册提交中

// 提交注册表单，注册成功后自动登录并跳转首页
async function handleRegister() {
  error.value = ''
  loading.value = true
  try {
    await userStore.register(username.value, password.value, nickname.value)
    await userStore.login(username.value, password.value)
    router.push('/')
  } catch (e: any) {
    error.value = e.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>
