// 路由配置 — Hash 模式，含全局导航守卫（未登录跳转 /login）
import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',                          // 登录页
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',                                // 主布局（所有管理页面父路由）
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',                   // 默认跳转仪表盘
    children: [
      {
        path: 'dashboard',                    // 仪表盘 — 统计卡片 + 最近文章
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'articles',                     // 文章管理 — 表格列表 + 筛选
        name: 'ArticleList',
        component: () => import('@/views/article/ArticleList.vue'),
        meta: { title: '文章管理' },
      },
      {
        path: 'articles/create',              // 新建文章 — 复用 ArticleEdit 组件
        name: 'ArticleCreate',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: { title: '新建文章' },
      },
      {
        path: 'articles/:id/edit',            // 编辑文章 — 复用 ArticleEdit 组件，通过路由参数传 id
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: { title: '编辑文章' },
      },
      {
        path: 'categories',                   // 分类管理 — 表格 + 弹窗表单
        name: 'CategoryList',
        component: () => import('@/views/category/CategoryList.vue'),
        meta: { title: '分类管理' },
      },
      {
        path: 'tags',                         // 标签管理 — 表格 + 弹窗表单
        name: 'TagList',
        component: () => import('@/views/tag/TagList.vue'),
        meta: { title: '标签管理' },
      },
      {
        path: 'users',                        // 用户管理 — 搜索 + 表格 + 弹窗 CRUD
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'comments',                     // 评论管理 — 状态筛选 + 审核操作
        name: 'CommentList',
        component: () => import('@/views/comment/CommentList.vue'),
        meta: { title: '评论管理' },
      },
      {
        path: 'media',                        // 媒体管理 — 网格展示 + 上传 + 复制 URL
        name: 'MediaList',
        component: () => import('@/views/media/MediaList.vue'),
        meta: { title: '媒体管理' },
      },
      {
        path: 'site',                         // 站点配置 — 选项卡表单 + 关于页编辑器
        name: 'SiteConfig',
        component: () => import('@/views/site/SiteConfig.vue'),
        meta: { title: '站点配置' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory('/admin/'), // base 与 Vite base 一致，URL 为 /admin/#/dashboard
  routes,
})

// 全局导航守卫：未登录用户只能访问 /login，已登录用户访问 /login 自动跳转首页
router.beforeEach((to, _from, next) => {
  const token = getToken()
  if (to.path === '/login') {
    token ? next('/') : next()
  } else {
    token ? next() : next('/login')
  }
})

export default router
