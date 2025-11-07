import { createStore } from 'vuex'

export default createStore({
  state: {
    // 初始化
    token: JSON.parse(localStorage.getItem('token')) || null,
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
    shopInfo: JSON.parse(localStorage.getItem('shopInfo')) || null,
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
      localStorage.setItem('token', JSON.stringify(token))
    },
    setShopInfo(state, shopInfo) {
      state.shopInfo = shopInfo
      localStorage.setItem('shopInfo', JSON.stringify(shopInfo))
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
  },
  getters: {
    isLoggedIn: (state) => {
      //!!是把值转化为布尔值
      return !!state.token
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
    async clearUserInfo({ commit }) {
      commit('clearUserInfo')
    },
    async clearShopInfo({ commit }) {
      commit('clearShopInfo')
    },
    logout({ commit }) {
      commit('clearUserInfo')
      commit('clearToken')
    },
  },
})
