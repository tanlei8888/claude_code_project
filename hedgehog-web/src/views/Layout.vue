<template>
  <el-container class="h-screen">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="bg-[#304156] transition-all overflow-hidden">
      <div class="h-[60px] leading-[60px] text-center text-white text-xl font-bold tracking-wider whitespace-nowrap">
        {{ isCollapse ? 'H' : 'Hedgehog' }}
      </div>
      <el-menu
        :default-active="activeMenu"
        :default-openeds="defaultOpeneds"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-sub-menu index="content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/articles">文章管理</el-menu-item>
          <el-menu-item index="/categories">分类管理</el-menu-item>
          <el-menu-item index="/tags">标签管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/comments">
          <el-icon><ChatLineSquare /></el-icon>
          <template #title>评论管理</template>
        </el-menu-item>
        <el-menu-item index="/media">
          <el-icon><Picture /></el-icon>
          <template #title>媒体管理</template>
        </el-menu-item>
        <el-menu-item index="/site">
          <el-icon><Setting /></el-icon>
          <template #title>站点配置</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="bg-white border-b border-gray-200 flex items-center justify-between px-4">
        <div>
          <el-icon class="text-xl cursor-pointer" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div>
          <el-dropdown @command="handleCommand">
            <span class="cursor-pointer flex items-center gap-1">
              {{ userStore.info?.nickname || userStore.info?.username || '管理员' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="bg-gray-100">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
// 主布局组件 — 左侧可收起菜单（Element Plus el-menu）+ 顶部用户下拉 + 内容区域 router-view
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Fold, Expand, ArrowDown, DataBoard, Document, ChatLineSquare, Picture, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)               // 侧边栏收起状态

// 当前激活的菜单项，处理文章/分类/标签子路由的统一高亮
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/articles')) return '/articles'
  if (path.startsWith('/categories')) return '/categories'
  if (path.startsWith('/tags')) return '/tags'
  return path
})

// 根据路径决定是否需要展开"内容管理"子菜单
const defaultOpeneds = computed(() => {
  const path = route.path
  if (path.startsWith('/articles') || path.startsWith('/categories') || path.startsWith('/tags')) {
    return ['content']
  }
  return []
})

// 菜单点击 → 路由跳转
function handleMenuSelect(index: string) {
  router.push(index)
}

// 处理右上角下拉菜单指令（目前仅"退出登录"）
function handleCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
  }
}

// 挂载时请求用户信息
onMounted(() => {
  userStore.fetchInfo()
})
</script>
