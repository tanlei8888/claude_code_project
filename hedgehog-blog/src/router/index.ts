// 博客前台路由配置 — 基于 HTML5 History 模式，含 8 条路由，导航后自动更新 SEO 元信息
import { createRouter, createWebHistory } from 'vue-router'
import { useSeo } from '@/composables/useSeo'

// 站点默认 SEO 标题和描述，路由未配置 meta 时兜底使用
const DEFAULT_TITLE = 'Hedgehog Blog'
const DEFAULT_DESC = 'Hedgehog Blog - 一个极简技术博客'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 首页 — 文章列表，支持分类/标签筛选与分页
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/Home.vue'),
      meta: {
        title: `首页 - ${DEFAULT_TITLE}`,
        description: `${DEFAULT_DESC}。浏览最新文章，关注编程、架构与最佳实践。`,
      },
    },
    // 文章详情页 — Markdown 渲染正文 + 代码高亮 + 评论区 + 点赞
    {
      path: '/article/:slug',
      name: 'ArticleDetail',
      component: () => import('@/views/ArticleDetail.vue'),
    },
    // 分类筛选页 — 展示指定分类下的文章列表
    {
      path: '/category/:slug',
      name: 'CategoryArticles',
      component: () => import('@/views/CategoryArticles.vue'),
      meta: {
        title: `分类文章 - ${DEFAULT_TITLE}`,
        description: `按分类浏览 ${DEFAULT_TITLE} 的所有文章。`,
      },
    },
    // 标签筛选页 — 展示指定标签下的文章列表
    {
      path: '/tag/:slug',
      name: 'TagArticles',
      component: () => import('@/views/TagArticles.vue'),
      meta: {
        title: `标签文章 - ${DEFAULT_TITLE}`,
        description: `按标签浏览 ${DEFAULT_TITLE} 的所有文章。`,
      },
    },
    // 关于页 — 站点与作者介绍，内容由后台站点配置驱动
    {
      path: '/about',
      name: 'About',
      component: () => import('@/views/About.vue'),
      meta: {
        title: `关于 - ${DEFAULT_TITLE}`,
        description: `了解 ${DEFAULT_TITLE} 和作者。`,
      },
    },
    // 登录页 — 用户名+密码登录
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: `登录 - ${DEFAULT_TITLE}` },
    },
    // 注册页 — 用户名+密码+昵称注册
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
      meta: { title: `注册 - ${DEFAULT_TITLE}` },
    },
    // 个人中心 — 查看/修改用户资料
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/Profile.vue'),
      meta: { title: `个人中心 - ${DEFAULT_TITLE}` },
    },
  ],
})

// 路由跳转后自动更新页面 title 和 meta description
router.afterEach((to) => {
  useSeo({
    title: (to.meta.title as string) || DEFAULT_TITLE,
    description: (to.meta.description as string) || DEFAULT_DESC,
  })
})

export default router
