import { createRouter, createWebHistory } from 'vue-router'
import LoginWindow from '@/views/login/LoginWindow.vue'
import MainWindow from '@/views/main/MainWindow.vue'
import ForgetWindow from '@/views/login/ForgetWindow.vue'
import RegisterWindow from '@/views/login/RegisterWindow.vue'
import store from '@/store'
import UserWindow from '@/views/user/UserWindow.vue'
import UserInfo from '@/views/user/UserInfo.vue'
import ChangePassword from '@/views/user/ChangePassword.vue'
import Page404 from '@/404Page.vue'

const routes = [
  {
    path: '/',
    name: 'main',
    component: MainWindow,
  },
  { path: '/404', name: '404', component: Page404 },

  {
    path: '/login',
    name: 'login',
    component: LoginWindow,
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterWindow,
  },
  {
    path: '/forget',
    name: 'forget',
    component: ForgetWindow,
  },
  {
    path: '/UserWindow',
    name: 'UserWindow',
    component: UserWindow,
  },
  {
    path: '/UserInfo',
    name: 'UserInfo',
    component: UserInfo,
  },
  {
    path: '/ChangePassword',
    name: 'ChangePassword',
    component: ChangePassword,
  },

  { path: '/:pathMatch(.*)*', redirect: '/404', hidden: true },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters.isLoggedIn
  if (!isLoggedIn && to.path !== '/login' && to.path !== '/register' && to.path !== '/forget') {
    next({ path: '/login' })
  } else {
    next()
  }
})

export default router
