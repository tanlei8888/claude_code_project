<template>
  <div class="max-w-sm mx-auto px-6 py-20">
    <h1 class="text-2xl font-bold text-center text-gray-900 mb-8">登录</h1>
    <form @submit.prevent="handleLogin" class="space-y-4">
      <div>
        <input v-model="username" type="text" placeholder="用户名" required
          class="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div>
        <input v-model="password" type="password" placeholder="密码" required
          class="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div v-if="error" class="text-sm text-red-500 text-center">{{ error }}</div>
      <button type="submit" :disabled="loading"
        class="w-full py-3 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      <p class="text-center text-sm text-gray-400">
        还没有账号？<router-link to="/register" class="text-blue-600 hover:underline">立即注册</router-link>
      </p>
    </form>
  </div>
</template>

<script setup lang="ts">
// 登录页 — 用户名+密码表单，登录成功后跳转首页
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')    // 用户名输入
const password = ref('')    // 密码输入
const error = ref('')      // 登录错误信息
const loading = ref(false)  // 登录提交中

// 提交登录表单，成功后跳转首页
async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await userStore.login(username.value, password.value)
    router.push('/')
  } catch (e: any) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>
