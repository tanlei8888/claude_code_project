<template>
  <div class="flex flex-col gap-4">
    <!-- 搜索栏 -->
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card>
      <div class="flex justify-between items-center mb-4">
        <span class="text-base font-semibold">用户列表</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'ADMIN'" size="small">管理员</el-tag>
            <el-tag v-else type="info" size="small">用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar v-if="row.avatar" :src="row.avatar" :size="32" />
            <span v-else class="text-gray-400 text-xs">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="bio" label="个人简介" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-end mt-4">
        <el-pagination
          v-model:current-page="searchForm.page"
          v-model:page-size="searchForm.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialogForm.username" :disabled="!!dialogForm.id" />
        </el-form-item>
        <el-form-item label="密码" :prop="dialogForm.id ? '' : 'password'">
          <el-input
            v-model="dialogForm.password"
            type="password"
            show-password
            :placeholder="dialogForm.id ? '留空则不修改' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="dialogForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="dialogForm.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialogForm.phone" />
        </el-form-item>
        <el-form-item label="头像 URL" prop="avatar">
          <el-input v-model="dialogForm.avatar" placeholder="头像图片链接" />
        </el-form-item>
        <el-form-item label="个人简介" prop="bio">
          <el-input v-model="dialogForm.bio" type="textarea" :rows="2" placeholder="一句话介绍" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="dialogForm.role">
            <el-radio value="USER">普通用户</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialogForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type FormItemRule } from 'element-plus'
import { getUserPage, saveUser, updateUser, deleteUser } from '@/api/user'

interface UserRecord {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  bio: string
  role: string
  status: number
  createTime: string
}

const loading = ref(false)
const tableData = ref<UserRecord[]>([])
const total = ref(0)

const searchForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
})

// 弹窗相关
const dialogVisible = ref(false)
const dialogFormRef = ref<FormInstance>()
const submitLoading = ref(false)
const dialogForm = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  bio: '',
  role: 'USER',
  status: 1,
})

const dialogTitle = computed(() => (dialogForm.id ? '编辑用户' : '新增用户'))

const dialogRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  searchForm.page = 1
  fetchData()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.page = 1
  fetchData()
}

function handleAdd() {
  resetDialogForm()
  dialogForm.status = 1
  dialogVisible.value = true
}

function handleEdit(row: UserRecord) {
  resetDialogForm()
  Object.assign(dialogForm, {
    id: row.id,
    username: row.username,
    password: '',
    nickname: row.nickname || '',
    email: row.email || '',
    phone: row.phone || '',
    avatar: row.avatar || '',
    bio: row.bio || '',
    role: row.role || 'USER',
    status: row.status,
  })
  dialogVisible.value = true
}

async function handleDelete(row: UserRecord) {
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '提示', {
      type: 'warning',
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 取消或错误
  }
}

async function handleSubmit() {
  const rules = { ...dialogRules }
  if (dialogForm.id) {
    const pwRules = rules.password as FormItemRule[]
    pwRules[0].required = false
  }

  const valid = await dialogFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (dialogForm.id) {
      await updateUser(dialogForm.id, { ...dialogForm })
      ElMessage.success('编辑成功')
    } else {
      await saveUser({ ...dialogForm })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误已在拦截器中处理
  } finally {
    submitLoading.value = false
  }
}

function handleDialogClosed() {
  dialogFormRef.value?.resetFields()
}

function resetDialogForm() {
  dialogForm.id = undefined
  dialogForm.username = ''
  dialogForm.password = ''
  dialogForm.nickname = ''
  dialogForm.email = ''
  dialogForm.phone = ''
  dialogForm.avatar = ''
  dialogForm.bio = ''
  dialogForm.role = 'USER'
  dialogForm.status = 1
  dialogFormRef.value?.clearValidate()
}

onMounted(() => {
  fetchData()
})
</script>
