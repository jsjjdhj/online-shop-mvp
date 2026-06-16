import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/user/Home.vue'),
    meta: { title: '首页', requiresAuth: true }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/user/ProductDetail.vue'),
    meta: { title: '商品详情', requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/user/Cart.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/user/Checkout.vue'),
    meta: { title: '结算', requiresAuth: true }
  },
  {
    path: '/order/success/:orderId',
    name: 'OrderSuccess',
    component: () => import('@/views/user/OrderSuccess.vue'),
    meta: { title: '订单提交成功', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/views/user/OrderList.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/orders/:orderId',
    name: 'OrderDetail',
    component: () => import('@/views/user/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: '管理后台', requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/products',
    name: 'AdminProducts',
    component: () => import('@/views/admin/ProductManage.vue'),
    meta: { title: '商品管理', requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('@/views/admin/OrderManage.vue'),
    meta: { title: '订单管理', requiresAuth: true, role: 'ADMIN' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '在线购物平台 MVP'
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  if (to.meta.role && localStorage.getItem('role') !== to.meta.role) {
    next('/')
    return
  }
  next()
})

export default router
