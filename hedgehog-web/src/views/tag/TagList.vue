<template>
  <div>
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold text-gray-800">标签管理</h2>
      <el-button type="primary" @click="openDialog()">新建标签</el-button>
    </div>
    <el-table :data="tags" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" width="200" />
      <el-table-column prop="slug" label="标识" width="200" />
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
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑标签' : '新建标签'" width="400px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="标识">
          <el-input v-model="form.slug" placeholder="留空自动生成拼音" />
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
// 标签管理组件 — 表格列表 + 弹窗表单（新建/编辑）+ 删除确认
import { ref, onMounted } from 'vue'
import { getTags, createTag, updateTag, deleteTag } from '@/api/tag'
import { ElMessage } from 'element-plus'

const tags = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editId = ref<number | null>(null)           // 编辑中的标签 ID，null = 新建模式
const form = ref({ name: '', slug: '' })

// 打开新建/编辑弹窗
function openDialog(row?: any) {
  editId.value = row ? row.id : null
  form.value = { name: row?.name || '', slug: row?.slug || '' }
  dialogVisible.value = true
}

// 加载全部标签
async function load() {
  loading.value = true
  try { tags.value = (await getTags()).data }
  finally { loading.value = false }
}

// 保存标签（新建或更新）
async function handleSave() {
  if (!form.value.name) { ElMessage.warning('请输入名称'); return }
  if (editId.value) {
    await updateTag(editId.value, form.value)
  } else {
    await createTag(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

// 删除标签（含确认弹窗）
async function handleDelete(id: number) {
  await deleteTag(id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => load())
</script>
