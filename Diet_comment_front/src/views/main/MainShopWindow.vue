<template>
  <div class="home-page">
    <header class="topbar">
      <div class="logogroup">
        <span class="logo">小众点评 </span>
        <button @click="goMainPost">进入味蕾社区🏠</button>
      </div>
      <div class="search">
        <input v-model="presearch" @keyup.enter="doSearch" placeholder="搜索商家、标签、位置..." />
        <button @click="doSearch">🔎搜索</button>
      </div>
      <div class="user" @click="avatarflag = !avatarflag" ref="avatarBtn" title="用户菜单">
        <img :src="userInfo.avatarUrl" alt="用户头像" />
        <transition name="fade-scale">
          <div v-if="avatarflag" class="avatar-dropdown" @click.stop>
            <button class="dropdown-item" @click="goHome">
              <span class="icon">👤</span>
              <span class="text">个人中心</span>
            </button>
            <button class="dropdown-item" @click="logOut">
              <span class="icon">🔒</span>
              <span class="text">登出</span>
            </button>
          </div>
        </transition>
      </div>
    </header>

    <section class="hero">
      <div class="hero-inner">
        <h2>欢迎来到美食广场</h2>
        <p>发现附近的美食、好店</p>
      </div>
    </section>

    <nav class="categories">
      <button
        v-for="cat in ['全部', '早餐', '火锅', '甜品', '咖啡', '奶茶', '川菜', '粤菜', '湘菜']"
        :key="cat"
        :class="{ active: category === cat }"
        @click="category = cat"
      >
        {{ cat }}
      </button>
    </nav>

    <main class="content">
      <aside class="filters">
        <h3>筛选</h3>
        <div>
          <label>
            人均评分
            <select v-model="filters.minRating">
              <option :value="0">不限</option>
              <option :value="3">>3.0</option>
              <option :value="4">>4.0</option>
              <option :value="4.5">>4.5</option>
            </select>
          </label>
        </div>
        <div>
          <label>
            人均消费
            <select v-model="filters.minAccount">
              <option :value="0">不限</option>
              <option :value="50">>50￥</option>
              <option :value="100">>100￥</option>
              <option :value="200">>200￥</option>
            </select>
          </label>
        </div>
      </aside>

      <section class="list">
        <div class="summary">
          <div>共 {{ filteredShops.length }} 家商家</div>
          <div class="sort">
            排序：
            <select v-model="sortBy">
              <option value="rating">评分</option>
              <option value="reviews">评价数</option>
            </select>
          </div>
        </div>

        <ul class="cards">
          <li v-for="shop in pagedShops" :key="shop.id" class="card" @click="goShop(shop)">
            <img :src="shop.imgurl" alt="店铺封面" />
            <div class="card-body">
              <div class="row top">
                <h4 class="name">{{ shop.name }}</h4>
                <span class="fav">{{ shop.fav ? '★' : '☆' }}</span>
              </div>
              <div class="meta">
                <div class="rating">
                  <span class="score">{{ shop.rating.toFixed(1) }}</span>
                  <span class="imgrating2" v-html="renderStarsHtml(shop.rating)"></span>
                  <span class="reviews">（{{ shop.reviews }}条点评）</span>
                </div>
                <div class="tags">
                  <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
                </div>
                <div class="info">
                  <span>人均 ¥{{ shop.price }}</span>
                </div>
                <p class="address">{{ shop.address }}</p>
              </div>
            </div>
          </li>
        </ul>

        <div class="pager">
          <button @click="prevPage" :disabled="page === 1">上一页</button>
          <span>
            第
            <input v-model="changepage" @keyup.enter="changePage" />
            /{{ totalPages }}页
          </span>
          <button @click="nextPage" :disabled="page >= totalPages">下一页</button>
        </div>
      </section>
    </main>

    <footer class="foot">店铺推荐主页</footer>
  </div>
</template>

<script>
import { getShopInfo, shopStatus } from '@/api/shop'
export default {
  name: 'MainShopWindowPage',
  data() {
    return {
      avatarflag: false,
      page: 1,
      changepage: '',
      perPage: 9,
      error: '',
      shops: [],
      search: '',
      presearch: '',
      category: '全部',
      sortBy: 'rating',
      filters: {
        minRating: 0,
        minAccount: 0,
      },
      userInfo: {
        id: null,
        userName: '',
        email: '',
        avatarUrl: '',
        role: '',
      },
    }
  },
  watch: {
    presearch(value) {
      if (value == '') {
        this.search = ''
      }
    },
    category() {
      this.page = 1
      this.changepage = this.page
    },
  },
  computed: {
    filteredShops() {
      const searchs = this.search.trim().toLowerCase()
      return this.shops
        .filter(
          (s) =>
            this.category === '全部' ||
            s.tags.includes(this.category) ||
            s.name.toLowerCase().includes(this.category.toLowerCase()),
        )
        .filter(
          (s) =>
            !searchs ||
            s.name.toLowerCase().includes(searchs.toLowerCase()) ||
            s.tags.join(' ').toLowerCase().includes(searchs.toLowerCase()) ||
            s.address.toLowerCase().includes(searchs.toLowerCase()),
        )
        .filter((s) => s.rating >= this.filters.minRating)
        .filter((s) => s.price >= this.filters.minAccount)
        .sort((a, b) => {
          if (this.sortBy === 'rating') return b.rating - a.rating
          if (this.sortBy === 'reviews') return b.reviews - a.reviews
          return 0
        })
    },
    totalPages() {
      return Math.max(1, Math.ceil(this.filteredShops.length / this.perPage))
    },
    pagedShops() {
      const start = (this.page - 1) * this.perPage
      return this.filteredShops.slice(start, start + this.perPage)
    },
  },
  created() {
    this.loadUserInfo()
    this.GetShopInfo()
    this.changepage = this.page
  },
  mounted() {
    document.addEventListener('click', this.onDocClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.onDocClick)
    this.$store.dispatch('clearShopInfo')
  },
  methods: {
    //收起页面
    onDocClick(e) {
      if (this.avatarflag && this.$refs.avatarBtn && !this.$refs.avatarBtn.contains(e.target)) {
        this.avatarflag = false
      }
    },
    //评分图像显示
    renderStarsHtml(rating) {
      const r = Number(rating) || 0
      const full = Math.floor(r)
      const half = r - full >= 0.5 ? 1 : 0
      const empty = 5 - full - half
      let html = ''
      for (let i = 0; i < full; i++) {
        html += '<span class="star star-full">★</span>'
      }
      if (half) {
        html += '<span class="star star-half">★</span>'
      }
      for (let i = 0; i < empty; i++) {
        html += '<span class="star star-empty">★</span>'
      }
      return html
    },
    //获取商铺信息
    async GetShopInfo() {
      try {
        const response = await getShopInfo()
        if (response.code === 1) {
          this.shops = response.data

          this.shops = this.shops.map((shop) => ({
            ...shop,
            fav: false,
          }))

          const shopsIds = this.shops.map((c) => c.id)
          const favRes = await Promise.all(shopsIds.map((id) => shopStatus(id, this.userInfo.id)))

          this.shops.forEach((shop) => {
            const res = favRes.find((r) => r.data.id === shop.id)
            if (res) {
              if (res.code === 1) {
                if (res.data.collected === 1) shop.fav = true
                else shop.fav = false
              }
            }
          })

          this.$store.dispatch('getShopInfo', this.shops)
        } else {
          this.error = '获取店铺信息失败'
          console.log(this.error)
        }
      } catch (error) {
        console.log(error)
      }
    },
    //搜索
    doSearch() {
      this.search = this.presearch
      this.page = 1
    },
    //加载用户信息
    loadUserInfo() {
      const userinfo = JSON.parse(localStorage.getItem('userInfo'))
      if (userinfo) {
        this.userInfo.id = userinfo.id
        this.userInfo.userName = userinfo.userName
        this.userInfo.email = userinfo.email
        this.userInfo.avatarUrl = userinfo.avatarUrl
        this.userInfo.role = userinfo.role
      }
    },
    //换页函数
    changePage() {
      if (this.changepage <= this.totalPages && this.changepage > 0) {
        this.page = this.changepage
        window.scrollTo({ top: 0, behavior: 'smooth' })
      } else {
        this.changepage = this.page
      }
    },
    //进入个人中心
    goHome() {
      this.$store.dispatch('getOneUser', this.userInfo)
      this.$router.push({ path: '/UserWindow' })
    },
    //登出
    logOut() {
      this.$store.dispatch('logout')
      this.$router.push({ path: '/login' })
    },
    //上一页
    prevPage() {
      if (this.page > 1) {
        this.page--
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
      this.changepage = this.page
    },
    //下一页
    nextPage() {
      if (this.page < this.totalPages) {
        this.page++
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
      this.changepage = this.page
    },
    goShop(shop) {
      this.$store.dispatch('getOneShop', shop)
      this.$router.push({ path: '/ShopWindow' })
    },
    goMainPost() {
      this.$router.push({ path: '/' })
    },
  },
}
</script>

<style scoped>
.home-page {
  font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif;
  color: #222;
}
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.logo {
  font-weight: 700;
  font-size: 18px;
}
.search {
  display: flex;
  gap: 8px;
}
.search input {
  padding: 8px 10px;
  width: 320px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.search button {
  padding: 8px 12px;
  background: #ff6b6b;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.hero {
  background: linear-gradient(90deg, #fff7f0, #fff);
  padding: 15px 10px;
  text-align: center;
  margin-bottom: 12px;
}
.hero h2 {
  margin: 6px 0;
  font-size: 26px;
}
.hero p {
  margin: 0;
  color: #666;
}
.categories {
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}
.categories button {
  padding: 8px 12px;
  border-radius: 20px;
  border: 1px solid #eee;
  background: #fff;
  cursor: pointer;
}
.categories button.active {
  background: #ffedd5;
  border-color: #ffd6a5;
}
.content {
  display: flex;
  gap: 20px;
  padding: 20px;
}
.filters {
  width: 200px;
  background: #fff;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
}
.list {
  flex: 1;
}
.summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  color: #666;
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 12px;
  list-style: none;
  padding: 0;
  margin: 0;
}
.card {
  display: flex;
  gap: 12px;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  align-items: flex-start;
  cursor: pointer;
}
.card img {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 6px;
}
.card-body {
  flex: 1;
}
.row.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.name {
  margin: 0;
  font-size: 16px;
}
.fav {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #ff8c00;
}
.meta {
  margin-top: 6px;
  color: #666;
  font-size: 13px;
}
.rating {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #ff9700;
}
.score {
  font-weight: 700;
  color: #ff6b6b;
}
.stars {
  color: #ffb86b;
}
.tags {
  margin-top: 6px;
}
.tag {
  display: inline-block;
  background: #f4f6f8;
  padding: 3px 6px;
  border-radius: 12px;
  margin-right: 6px;
  font-size: 12px;
  color: #555;
}
.info {
  margin-top: 6px;
  color: #888;
}
.address {
  margin-top: 6px;
  color: #999;
  font-size: 13px;
}
.pager {
  margin-top: 14px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.pager input {
  width: 20px;
  padding: 6px 5px;
  border-radius: 6px;
  border: 1px solid #eee;
  background: #fff;
  cursor: text;
  color: #666;
}
.pager button {
  border: 1px solid;
  border-radius: 5px;
  background: linear-gradient(
    135deg,
    rgba(141, 255, 253, 0.461) 0%,
    rgba(255, 255, 255, 0.08) 100%
  );
}
.foot {
  text-align: center;
  color: #999;
  padding: 16px 0;
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
}
.user {
  width: 36px;
  height: 36px;
}
.user img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.06);
}
.avatar-dropdown {
  position: absolute;
  right: 0;
  top: 60px;
  min-width: 140px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.06);
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  z-index: 60;
}
.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  background: transparent;
  border: none;
  text-align: left;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #111827;
}
.dropdown-item .icon {
  width: 18px;
  text-align: center;
}
.dropdown-item:hover {
  background: rgba(255, 106, 0, 0.06);
}
.dropdown-item.danger:hover {
  background: rgba(239, 68, 68, 0.06);
}
/* 过渡 */
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 160ms cubic-bezier(0.2, 0.8, 0.2, 1);
}
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}
.fade-scale-enter-to,
.fade-scale-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.logogroup {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 15px;
}
.logogroup button {
  padding: 4px 8px;
  background: #ffedd5;
  border: 1px solid;
  border-color: #ffd6a5;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}
</style>

<style>
.imgrating2 {
  display: inline-block;
  vertical-align: middle;
  font-size: 12px;
}
.imgrating2 .star {
  position: relative;
  display: inline-block;
  width: 12px;
  height: 12px;
  line-height: 12px;
  font-size: 12px;
  color: #dfdfdf;
  text-align: center;
  margin-right: 2px;
  z-index: 0;
}
.imgrating2 .star::before {
  content: '★';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  overflow: hidden;
  width: 0%;
  pointer-events: none;
  color: #ffc051;
  transition: width 0.12s ease;
  z-index: 1;
}
.imgrating2 .star-full::before {
  width: 100%;
}
.imgrating2 .star-half::before {
  width: 50%;
}
</style>
