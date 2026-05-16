<template>
  <div class="max-w-2xl mx-auto px-4 sm:px-6 py-12">
    <h2 class="text-2xl font-bold text-gray-900 mb-8">个人资料</h2>

    <!-- 信息展示区 -->
    <div class="flex items-center gap-6 mb-10 p-6 bg-gray-50 rounded-xl">
      <img :src="userStore.info?.avatar || defaultAvatar" class="w-20 h-20 rounded-full object-cover border-2 border-white shadow" />
      <div>
        <p class="text-xl font-semibold text-gray-900">{{ userStore.info?.nickname || userStore.info?.username }}</p>
        <p class="text-sm text-gray-500 mt-1">@{{ userStore.info?.username }}</p>
        <span class="inline-block mt-1 px-2 py-0.5 text-xs rounded border"
          :class="userStore.info?.role === 'ADMIN' ? 'bg-purple-50 border-purple-200 text-purple-700' : 'bg-gray-100 border-gray-200 text-gray-600'">
          {{ userStore.info?.role === 'ADMIN' ? '管理员' : '用户' }}
        </span>
      </div>
    </div>

    <!-- 头像选择器 -->
    <div class="mb-8">
      <h3 class="text-base font-semibold text-gray-900 mb-4">选择头像</h3>
      <div v-if="avatars.length === 0" class="text-sm text-gray-400">暂无可用头像</div>
      <div class="grid grid-cols-5 sm:grid-cols-5 gap-3">
        <button
          v-for="av in avatars"
          :key="av.id"
          @click="form.avatar = av.url"
          class="w-full aspect-square rounded-lg border-2 overflow-hidden transition-all"
          :class="form.avatar === av.url ? 'border-blue-500 ring-2 ring-blue-200' : 'border-gray-200 hover:border-gray-300'"
        >
          <img :src="av.url" class="w-full h-full object-cover" :alt="av.filename" />
        </button>
      </div>
    </div>

    <!-- 编辑表单 -->
    <div class="space-y-4">
      <h3 class="text-base font-semibold text-gray-900">编辑资料</h3>
      <div>
        <label class="block text-sm text-gray-600 mb-1">昵称</label>
        <input v-model="form.nickname" class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">邮箱</label>
        <input v-model="form.email" type="email" class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">手机号</label>
        <input v-model="form.phone" class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">个人简介</label>
        <textarea v-model="form.bio" rows="3" class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none" placeholder="介绍一下自己..."></textarea>
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">新密码<span class="text-gray-400">（留空则不修改）</span></label>
        <input v-model="form.password" type="password" class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="留空则不修改密码" />
      </div>
      <div class="pt-2">
        <button @click="handleSave" :disabled="saving" class="px-6 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors">
          {{ saving ? '保存中...' : '保存修改' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 个人资料页 — 展示用户信息、可选头像列表、编辑个人资料表单
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAvatars, type AvatarItem } from '@/api/auth'
import { message } from '@/utils/message'

const userStore = useUserStore()
const avatars = ref<AvatarItem[]>([]) // 可选头像列表
const saving = ref(false)             // 保存提交中

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCUiIHkxPSIwJSIgeDI9IjEwMCUiIHkyPSIxMDAlIj48c3RvcCBvZmZzZXQ9IjAlIiBzdG9wLWNvbG9yPSIjOTM1MkQzIi8+PHN0b3Agb2Zmc2V0PSIxMDAlIiBzdG9wLWNvbG9yPSIjNEM2REZGIi8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iMjAiIGZpbGw9InVybCgjZykiLz48L3N2Zz4='

// 编辑资料表单，绑定用户可修改的字段
const form = reactive({
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  bio: '',
  password: '',
})

onMounted(async () => {
  // 加载可选头像列表
  try {
    avatars.value = await getAvatars()
  } catch {}

  // 用当前用户信息填充表单
  const info = userStore.info
  if (info) {
    form.nickname = info.nickname || ''
    form.email = info.email || ''
    form.phone = info.phone || ''
    form.avatar = info.avatar || ''
    form.bio = info.bio || ''
  }
})

// 提交表单保存用户资料，仅发送非空字段
async function handleSave() {
  saving.value = true
  try {
    const data: Record<string, string> = {}
    if (form.nickname) data.nickname = form.nickname
    if (form.email) data.email = form.email
    if (form.phone) data.phone = form.phone
    if (form.avatar) data.avatar = form.avatar
    if (form.bio) data.bio = form.bio
    if (form.password) data.password = form.password
    await userStore.update(data)
    message.success('保存成功')
  } catch {
    // 错误已在拦截器中展示
  } finally {
    saving.value = false
  }
}
</script>
