import { createStore } from 'vuex'

export default createStore({
  state: {
    // 初始化
    token: localStorage.getItem('token') || null,
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
    shopInfo: JSON.parse(localStorage.getItem('shopInfo')) || null,
    postInfo: JSON.parse(localStorage.getItem('postInfo')) || null,
    oneShop: JSON.parse(localStorage.getItem('oneShop')) || null,
    onePost: JSON.parse(localStorage.getItem('onePost')) || null,
    oneUser: JSON.parse(localStorage.getItem('oneUser')) || null,
  },
  mutations: {
    // 更新用户信息
    setUserInfo(state, userInfo) {
      state.userInfo = userInfo
      // 同步到本地存储
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    setToken(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    setShopInfo(state, shopInfo) {
      state.shopInfo = shopInfo
      localStorage.setItem('shopInfo', JSON.stringify(shopInfo))
    },
    setPostInfo(state, postInfo) {
      state.postInfo = postInfo
      localStorage.setItem('postInfo', JSON.stringify(postInfo))
    },
    setOneShop(state, oneShop) {
      state.oneShop = oneShop
      localStorage.setItem('oneShop', JSON.stringify(oneShop))
    },
    setOnePost(state, onePost) {
      state.onePost = onePost
      localStorage.setItem('onePost', JSON.stringify(onePost))
    },
    setOneUser(state, oneUser) {
      state.oneUser = oneUser
      localStorage.setItem('oneUser', JSON.stringify(oneUser))
    },
    clearUserInfo(state) {
      state.userInfo = null
      localStorage.removeItem('userInfo')
    },
    clearToken(state) {
      state.token = null
      localStorage.removeItem('token')
    },
    clearShopInfo(state) {
      state.shopInfo = null
      localStorage.removeItem('shopInfo')
    },
    clearPostInfo(state) {
      state.postInfo = null
      localStorage.removeItem('postInfo')
    },
    clearOneShop(state) {
      state.oneShop = null
      localStorage.removeItem('oneShop')
    },
    clearOnePost(state) {
      state.onePost = null
      localStorage.removeItem('onePost')
    },
    clearOneUser(state) {
      state.oneUser = null
      localStorage.removeItem('oneUser')
    },
  },
  getters: {
    isLoggedIn: (state) => {
      //!!是把值转化为布尔值
      return !!state.token
    },
    isOneUser: (state) => {
      return !!state.oneUser
    },
    isOnePost: (state) => {
      return !!state.onePost
    },
    isOneShop: (state) => {
      return !!state.oneShop
    },
  },
  actions: {
    async login({ commit }, token) {
      commit('setToken', token)
    },
    async getUserInfo({ commit }, userInfo) {
      commit('setUserInfo', userInfo)
    },
    async getShopInfo({ commit }, shopInfo) {
      commit('setShopInfo', shopInfo)
    },
    async getPostInfo({ commit }, postInfo) {
      commit('setPostInfo', postInfo)
    },
    async getOneShop({ commit }, oneShop) {
      commit('setOneShop', oneShop)
    },
    async getOnePost({ commit }, onePost) {
      commit('setOnePost', onePost)
    },
    async getOneUser({ commit }, oneUser) {
      commit('setOneUser', oneUser)
    },
    async clearUserInfo({ commit }) {
      commit('clearUserInfo')
    },
    async clearShopInfo({ commit }) {
      commit('clearShopInfo')
    },
    async clearPostInfo({ commit }) {
      commit('clearPostInfo')
    },
    async clearOneShop({ commit }) {
      commit('clearOneShop')
    },
    async clearOnePost({ commit }) {
      commit('clearOnePost')
    },
    async clearOneUser({ commit }) {
      commit('clearOneUser')
    },
    logout({ commit }) {
      commit('clearUserInfo')
      commit('clearToken')
    },
  },
})
