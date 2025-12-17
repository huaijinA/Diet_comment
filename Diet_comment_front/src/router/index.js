import { createRouter, createWebHistory } from 'vue-router'
import LoginWindow from '@/views/login/LoginWindow.vue'
import MainShopWindow from '@/views/main/MainShopWindow.vue'
import ForgetWindow from '@/views/login/ForgetWindow.vue'
import RegisterWindow from '@/views/login/RegisterWindow.vue'
import store from '@/store'
import UserWindow from '@/views/user/UserWindow.vue'
import UserInfo from '@/views/user/UserInfo.vue'
import ChangePassword from '@/views/user/ChangePassword.vue'
import Page404 from '@/404Page.vue'
import ShopWindow from '@/views/shop/ShopWindow.vue'
import PostWindow from '@/views/post/PostWindow.vue'
import MainPostWindow from '@/views/main/MainPostWindow.vue'
import NewPostWindow from '@/views/post/NewPostWindow.vue'
import EditPostWindow from '@/views/post/EditPostWindow.vue'

const routes = [
  {
    path: '/',
    name: 'MainPostWindow',
    component: MainPostWindow,
  },
  {
    path: '/MainShopWindow',
    name: 'MainShopWindow',
    component: MainShopWindow,
  },
  { path: '/404', name: '404', component: Page404 },
  {
    path: '/NewPostWindow',
    name: 'NewPostWindow',
    component: NewPostWindow,
  },
  {
    path: '/EditPostWindow',
    name: 'EditPostWindow',
    component: EditPostWindow,
  },
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
  {
    path: '/ShopWindow',
    name: 'ShopWindow',
    component: ShopWindow,
  },
  {
    path: '/PostWindow',
    name: 'PostWindow',
    component: PostWindow,
  },
  { path: '/:pathMatch(.*)*', redirect: '/404', hidden: true },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters.isLoggedIn
  const isOneUser = store.getters.isOneUser
  const isOnePost = store.getters.isOnePost
  const isOneShop = store.getters.isOneShop

  if (to.path == '/UserWindow' && !isOneUser) {
    return next({ path: '/' })
  }
  if (to.path == '/PostWindow' && !isOnePost) {
    return next({ path: '/' })
  }
  if (to.path == '/ShopWindow' && !isOneShop) {
    return next({ path: '/' })
  }
  if (!isLoggedIn && to.path !== '/login' && to.path !== '/register' && to.path !== '/forget') {
    next({ path: '/login' })
  } else {
    next()
  }
})

export default router
