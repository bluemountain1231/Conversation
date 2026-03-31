import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/HomeView.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue') },
  { path: '/post/create', name: 'CreatePost', component: () => import('../views/CreatePostView.vue'), meta: { requiresAuth: true } },
  { path: '/post/:id', name: 'PostDetail', component: () => import('../views/PostDetailView.vue') },
  { path: '/profile/:id?', name: 'Profile', component: () => import('../views/ProfileView.vue') },
  { path: '/circles', name: 'CircleList', component: () => import('../views/CircleListView.vue') },
  { path: '/circle/create', name: 'CreateCircle', component: () => import('../views/CreateCircleView.vue'), meta: { requiresAuth: true } },
  { path: '/circle/:id', name: 'CircleDetail', component: () => import('../views/CircleDetailView.vue') },
  { path: '/search', name: 'Search', component: () => import('../views/SearchView.vue') },
  { path: '/chat/:userId?', name: 'Chat', component: () => import('../views/ChatView.vue'), meta: { requiresAuth: true } },
  { path: '/notifications', name: 'Notifications', component: () => import('../views/NotificationView.vue'), meta: { requiresAuth: true } },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('../views/admin/DashboardView.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/UserManageView.vue') },
      { path: 'posts', name: 'AdminPosts', component: () => import('../views/admin/PostManageView.vue') },
      { path: 'circles', name: 'AdminCircles', component: () => import('../views/admin/CircleManageView.vue') },
      { path: 'logs', name: 'AdminLogs', component: () => import('../views/admin/LogView.vue') },
    ]
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
