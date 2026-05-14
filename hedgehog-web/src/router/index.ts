import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'articles',
        name: 'ArticleList',
        component: () => import('@/views/article/ArticleList.vue'),
        meta: { title: '文章管理' },
      },
      {
        path: 'articles/create',
        name: 'ArticleCreate',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: { title: '新建文章' },
      },
      {
        path: 'articles/:id/edit',
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: { title: '编辑文章' },
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('@/views/category/CategoryList.vue'),
        meta: { title: '分类管理' },
      },
      {
        path: 'tags',
        name: 'TagList',
        component: () => import('@/views/tag/TagList.vue'),
        meta: { title: '标签管理' },
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'comments',
        name: 'CommentList',
        component: () => import('@/views/comment/CommentList.vue'),
        meta: { title: '评论管理' },
      },
      {
        path: 'media',
        name: 'MediaList',
        component: () => import('@/views/media/MediaList.vue'),
        meta: { title: '媒体管理' },
      },
      {
        path: 'site',
        name: 'SiteConfig',
        component: () => import('@/views/site/SiteConfig.vue'),
        meta: { title: '站点配置' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = getToken()
  if (to.path === '/login') {
    token ? next('/') : next()
  } else {
    token ? next() : next('/login')
  }
})

export default router
