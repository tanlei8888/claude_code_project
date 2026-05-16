<template>
  <div class="mt-12 pt-8 border-t border-gray-100">
    <h3 class="text-lg font-semibold text-gray-900 mb-6">
      评论 ({{ totalComments }})
    </h3>

    <!-- 发表评论 -->
    <div v-if="userStore.isLoggedIn()" class="mb-8">
      <textarea v-model="newComment" rows="3" class="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
        placeholder="写下你的评论..."></textarea>
      <div class="flex justify-end mt-2">
        <button @click="handleComment" :disabled="!newComment.trim() || submitting"
          class="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
          {{ submitting ? '提交中...' : '发表评论' }}
        </button>
      </div>
    </div>
    <div v-else class="mb-8 p-4 bg-gray-50 rounded-lg text-center text-sm text-gray-500">
      <router-link to="/login" class="text-blue-600 hover:underline">登录</router-link>后发表评论
    </div>

    <!-- 评论列表 -->
    <div v-if="comments.length === 0 && !loading" class="text-center text-gray-400 py-8 text-sm">
      暂无评论，来说点什么吧
    </div>
    <div v-for="comment in comments" :key="comment.id" class="mb-6">
      <!-- 顶级评论 -->
      <div class="flex gap-3">
        <img :src="comment.user?.avatar || defaultAvatar" class="w-9 h-9 rounded-full flex-shrink-0" />
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 mb-1">
            <span class="text-sm font-medium text-gray-900">{{ comment.user?.nickname || comment.user?.username }}</span>
            <span class="text-xs text-gray-400">{{ formatDate(comment.createTime) }}</span>
          </div>
          <p class="text-sm text-gray-700 mb-2">{{ comment.content }}</p>
          <button v-if="userStore.isLoggedIn()" @click="replyTo = replyTo?.id === comment.id ? null : comment"
            class="text-xs text-gray-400 hover:text-blue-600 transition-colors">
            {{ replyTo?.id === comment.id ? '取消回复' : '回复' }}
          </button>
          <!-- 回复框 -->
          <div v-if="replyTo?.id === comment.id" class="mt-3 flex gap-2">
            <input v-model="replyContent" placeholder="回复..." class="flex-1 px-3 py-1.5 border border-gray-200 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
            <button @click="handleReply(comment)" :disabled="!replyContent.trim()"
              class="px-3 py-1.5 bg-blue-600 text-white text-xs rounded hover:bg-blue-700 disabled:opacity-50 transition-colors">
              回复
            </button>
          </div>
          <!-- 二级回复 -->
          <div v-if="comment.replies?.length" class="mt-3 pl-4 border-l-2 border-gray-100 space-y-3">
            <div v-for="reply in comment.replies" :key="reply.id" class="flex gap-2">
              <img :src="reply.user?.avatar || defaultAvatar" class="w-7 h-7 rounded-full flex-shrink-0" />
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 mb-0.5">
                  <span class="text-sm font-medium text-gray-900">{{ reply.user?.nickname || reply.user?.username }}</span>
                  <span v-if="reply.replyToUser" class="text-xs text-gray-400">
                    回复 @{{ reply.replyToUser.nickname || reply.replyToUser.username }}
                  </span>
                  <span class="text-xs text-gray-400">{{ formatDate(reply.createTime) }}</span>
                </div>
                <p class="text-sm text-gray-600">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 加载更多 -->
    <div v-if="hasMore" class="text-center mt-6">
      <button @click="loadComments(page + 1)" :disabled="loading"
        class="px-6 py-2 text-sm text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 disabled:opacity-50 transition-colors">
        {{ loading ? '加载中...' : '加载更多' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 评论区组件 — 支持两层嵌套评论（顶级+回复），分页加载，发表/回复评论
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { getComments, createComment, type Comment } from '@/api/comment'

const props = defineProps<{
  articleId: number    // 所属文章 ID
  commentCount: number // 文章当前评论总数（用于初始化显示）
}>()

const userStore = useUserStore()
const comments = ref<Comment[]>([])        // 当前页评论列表
const newComment = ref('')                 // 新评论输入内容
const replyContent = ref('')              // 回复输入内容
const replyTo = ref<Comment | null>(null)  // 当前正在回复的评论（null 表示非回复状态）
const loading = ref(false)                 // 分页加载中
const submitting = ref(false)              // 提交评论中
const page = ref(1)                        // 当前页码
const hasMore = ref(false)                 // 是否还有更多评论
const totalComments = ref(props.commentCount) // 评论总数（随新增递增）

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCUiIHkxPSIwJSIgeDI9IjEwMCUiIHkyPSIxMDAlIj48c3RvcCBvZmZzZXQ9IjAlIiBzdG9wLWNvbG9yPSIjOTM1MkQzIi8+PHN0b3Agb2Zmc2V0PSIxMDAlIiBzdG9wLWNvbG9yPSIjNEM2REZGIi8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iMjAiIGZpbGw9InVybCgjZykiLz48L3N2Zz4='

// 分页加载评论列表，p=1 时替换当前列表，否则追加
async function loadComments(p = 1) {
  if (loading.value) return
  loading.value = true
  try {
    const data = await getComments(props.articleId, p, 10)
    if (p === 1) {
      comments.value = data
    } else {
      comments.value.push(...data)
    }
    page.value = p
    hasMore.value = data.length === 10
  } finally {
    loading.value = false
  }
}

// 发表顶级评论
async function handleComment() {
  if (!newComment.value.trim()) return
  submitting.value = true
  try {
    await createComment({ articleId: props.articleId, content: newComment.value })
    newComment.value = ''
    totalComments.value++
    await loadComments(1)
  } catch {
    // 错误消息已由全局拦截器统一展示，此处仅阻止状态变更
  } finally {
    submitting.value = false
  }
}

// 回复某条评论，携带 parentId 和 replyToUserId 标识二级回复关系
async function handleReply(parent: Comment) {
  if (!replyContent.value.trim()) return
  try {
    await createComment({
      articleId: props.articleId,
      content: replyContent.value,
      parentId: parent.id,
      replyToUserId: parent.userId,
    })
    replyContent.value = ''
    replyTo.value = null
    totalComments.value++
    await loadComments(1)
  } catch {
    // 错误消息已由全局拦截器统一展示，此处仅阻止状态变更
  }
}

// 格式化日期为智能相对时间（刚刚/X分钟前/X小时前/日期）
function formatDate(date: string) {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return d.toLocaleDateString('zh-CN')
}

onMounted(() => loadComments())

// 同步外部传入的评论数变化
watch(() => props.commentCount, (v) => { totalComments.value = v })
</script>
