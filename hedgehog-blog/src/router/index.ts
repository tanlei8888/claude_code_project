import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/Home.vue'),
    },
    {
      path: '/article/:slug',
      name: 'ArticleDetail',
      component: () => import('@/views/ArticleDetail.vue'),
    },
    {
      path: '/category/:slug',
      name: 'CategoryArticles',
      component: () => import('@/views/CategoryArticles.vue'),
    },
    {
      path: '/tag/:slug',
      name: 'TagArticles',
      component: () => import('@/views/TagArticles.vue'),
    },
    {
      path: '/about',
      name: 'About',
      component: () => import('@/views/About.vue'),
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
    },
  ],
})

export default router
