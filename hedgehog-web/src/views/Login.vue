<template>
  <div class="h-screen flex items-center justify-center bg-gray-100">
    <el-card class="w-[400px]">
      <h2 class="text-center text-xl font-bold text-gray-700 mb-6">Hedgehog Admin</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="w-full" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 登录页组件 — 用户名 + 密码表单，登录成功后存 token 并跳转首页
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { login } from '@/api/auth'
import { setToken } from '@/utils/auth'

const router = useRouter()
const formRef = ref<FormInstance>()        // 表单实例引用，用于校验
const loading = ref(false)                  // 登录按钮 loading 态

const form = reactive({
  username: 'admin',      // 默认填充开发账号
  password: 'admin123',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// 执行登录：校验 → 请求 → 存 token → 跳转首页
async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    setToken(res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>
