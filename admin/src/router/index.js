import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout'
import store from '@/store'

Vue.use(VueRouter)

export const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页看板', icon: 'el-icon-data-line' }
      }
    ]
  },
  {
    path: '/station',
    component: Layout,
    redirect: '/station/index',
    meta: { title: '油站管理', icon: 'el-icon-office-building' },
    children: [
      {
        path: 'index',
        name: 'Station',
        component: () => import('@/views/station/index.vue'),
        meta: { title: '油站与油价' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    redirect: '/order/index',
    meta: { title: '订单管理', icon: 'el-icon-s-order' },
    children: [
      {
        path: 'index',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '加油订单' }
      }
    ]
  },
  {
    path: '/member',
    component: Layout,
    redirect: '/member/index',
    meta: { title: '会员管理', icon: 'el-icon-user' },
    children: [
      {
        path: 'index',
        name: 'Member',
        component: () => import('@/views/member/index.vue'),
        meta: { title: '会员列表' }
      }
    ]
  },
  {
    path: '/coupon',
    component: Layout,
    redirect: '/coupon/index',
    meta: { title: '营销中心', icon: 'el-icon-present' },
    children: [
      {
        path: 'index',
        name: 'Coupon',
        component: () => import('@/views/coupon/index.vue'),
        meta: { title: '优惠券管理' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'el-icon-setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '账号管理' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
    return
  }
  if (!store.state.user.token) {
    next('/login')
    return
  }
  next()
})

export default router
