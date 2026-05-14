<template>
  <div>
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold text-gray-800">媒体管理</h2>
      <el-upload :show-file-list="false" :http-request="handleUpload" accept="image/jpeg,image/png,image/gif,image/webp,image/svg+xml">
        <el-button type="primary">上传文件</el-button>
      </el-upload>
    </div>
    <div v-if="loading" class="text-center py-10 text-gray-400">加载中...</div>
    <el-empty v-else-if="mediaList.length === 0" description="暂无媒体文件" />
    <div v-else class="grid grid-cols-4 gap-4">
      <div v-for="item in mediaList" :key="item.id" class="border border-gray-200 rounded-lg overflow-hidden group relative">
        <img :src="item.url" class="w-full h-36 object-cover" />
        <div class="p-2 text-xs text-gray-500 truncate">{{ item.filename }}</div>
        <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
          <el-button size="small" @click="copyUrl(item.url)">复制 URL</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(item.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
    <div class="flex justify-end mt-4" v-if="total > size">
      <el-pagination v-model:current-page="page" :total="total" :page-size="size" layout="total, prev, pager, next"
        @current-change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMediaPage, uploadFile, deleteMedia } from '@/api/media'
import { ElMessage } from 'element-plus'

const mediaList = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const size = 12

async function load() {
  loading.value = true
  try {
    const res = await getMediaPage({ page: page.value, size })
    mediaList.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handleUpload(options: any) {
  try {
    await uploadFile(options.file)
    ElMessage.success('上传成功')
    load()
  } catch { ElMessage.error('上传失败') }
}

function copyUrl(url: string) {
  navigator.clipboard.writeText(url).then(() => ElMessage.success('已复制到剪贴板'))
}

async function handleDelete(id: number) {
  await deleteMedia(id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => load())
</script>
