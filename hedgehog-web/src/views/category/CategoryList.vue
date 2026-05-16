<template>
  <div>
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold text-gray-800">分类管理</h2>
      <el-button type="primary" @click="openDialog()">新建分类</el-button>
    </div>
    <el-table :data="categories" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="slug" label="标识" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑分类' : '新建分类'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="标识">
          <el-input v-model="form.slug" placeholder="留空自动生成拼音" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// 分类管理组件 — 表格列表 + 弹窗表单（新建/编辑）+ 删除确认
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'
import { ElMessage } from 'element-plus'

const categories = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editId = ref<number | null>(null)           // 编辑中的分类 ID，null = 新建模式
const form = ref({ name: '', slug: '', description: '', sortOrder: 0 })

// 打开新建/编辑弹窗，传入行数据为编辑模式
function openDialog(row?: any) {
  if (row) {
    editId.value = row.id
    form.value = { name: row.name, slug: row.slug, description: row.description || '', sortOrder: row.sortOrder || 0 }
  } else {
    editId.value = null
    form.value = { name: '', slug: '', description: '', sortOrder: 0 }
  }
  dialogVisible.value = true
}

// 加载全部分类
async function load() {
  loading.value = true
  try { categories.value = (await getCategories()).data }
  finally { loading.value = false }
}

// 保存分类（新建或更新）
async function handleSave() {
  if (!form.value.name) { ElMessage.warning('请输入名称'); return }
  if (editId.value) {
    await updateCategory(editId.value, form.value)
  } else {
    await createCategory(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

// 删除分类（含确认弹窗）
async function handleDelete(id: number) {
  await deleteCategory(id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => load())
</script>
