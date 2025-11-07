<template>
  <div class="page">
    <header class="header">
      <button class="home" @click="mainWindow">返回主页</button>
      <div class="person">
        <img :src="userInfo.avatarUrl" />
        <div>{{ userInfo.userName }}</div>
        <span class="headbutton">
          <button @click="edit">编辑资料</button>
          <button @click="changePassword">修改密码</button>
        </span>
      </div>
    </header>
    <nav class="nav">
      <span class="category">
        <button
          v-for="cate in ['我的动态', '帖子收藏', '关注店铺']"
          :key="cate"
          :class="{ active: category === cate }"
          @click="changeCategory(cate)"
        >
          {{ cate }}
        </button>
      </span>
      <span class="search">
        <input placeholder="搜索筛选相应类别" v-model="presearch" @keyup.enter="searchFor" />
        <button @click="searchFor">🔍搜索</button>
      </span>
    </nav>

    <div class="border"></div>

    <main class="main">
      <section v-if="category == '我的动态'">
        <span class="total"> 共 {{ filtered.length }} 条动态 </span>
        <div class="warn" v-if="filtered.length == 0">
          <img :src="backImg" />
        </div>
        <!-- <ul class="cards">
          <li v-for="news in pagedData" :key="news.id" class="card">
            <img :src="news.imgurl" alt="帖子封面" />
            <div class="card-body">
              <div class="row top">
                <h4 class="name">{{ news.title }}</h4>
              </div>
              <div class="meta">
                <div class="rating">
                  <span class="reviews">{{ shop.reviews }}条评论</span>
                </div>
                <div class="tags">
                  <span v-for="tag in news.shop.tags" :key="tag" class="tag">{{ tag }}</span>
                </div>
                <p class="address">{{ news.shop.shopname }}</p>
                <p class="address">{{ news.shop.address }}</p>
              </div>
            </div>
          </li>
        </ul> -->
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
      <section v-if="category == '帖子收藏'">
        <span class="total"> 共 {{ filtered.length }} 条帖子 </span>
        <div class="warn" v-if="filtered.length == 0">
          <img :src="backImg" />
        </div>
        <!-- <ul class="cards">
          <li v-for="collected in pagedData" :key="collected.id" class="card">
            <img :src="collected.imgurl" alt="帖子封面" />
            <div class="card-body">
              <div class="row top">
                <h4 class="name">{{ collected.title }}</h4>
              </div>
              <div class="meta">
                <div class="rating">
                  <span class="reviews">{{ collected.reviews }}条评论</span>
                </div>
                <div class="tags">
                  <span v-for="tag in collected.shop.tags" :key="tag" class="tag">{{ tag }}</span>
                </div>
                <p class="address">{{ collected.shop.shopname }}</p>
                <p class="address">{{ collected.shop.address }}</p>
                <div class="author">
                  <img :src="collected.user.avatarUrl" />
                  <span>{{ collected.user.userName }}</span>
                </div>
              </div>
            </div>
          </li>
        </ul> -->
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
      <section class="shop" v-if="category == '关注店铺'">
        <div class="mainhead">
          <span class="sort">
            排序：
            <select v-model="sortBy">
              <option value="rating">评分</option>
              <option value="reviews">评价数</option>
              <option value="distance">距离</option>
            </select>
          </span>
          <span class="total"> 共 {{ filtered.length }} 家店 </span>
        </div>
        <div class="warn" v-if="filtered.length == 0">
          <img :src="backImg" />
        </div>
        <ul class="cards">
          <li v-for="shop in pagedData" :key="shop.id" class="card">
            <img :src="shop.imgurl" alt="店铺封面" />
            <div class="card-body">
              <div class="row top">
                <h4 class="name">{{ shop.shopname }}</h4>
                <button class="fav" @click="toggleFav(shop)">{{ shop.fav ? '★' : '☆' }}</button>
              </div>
              <div class="meta">
                <div class="rating">
                  <span class="score">{{ shop.rating.toFixed(1) }}</span>
                  <span class="stars" v-html="renderStars(shop.rating)"></span>
                  <span class="reviews">（{{ shop.reviews }}条点评）</span>
                </div>
                <div class="tags">
                  <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
                </div>
                <div class="info">
                  <span>人均 ¥{{ shop.price }}</span>
                  <span> · {{ shop.distance }}km</span>
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
  </div>
</template>
<script>
import { getShopInfo } from '@/api/shop'
import backImg from '@/assets/back1.jpg'
export default {
  data() {
    return {
      category: '我的动态',
      page: 1,
      changepage: 1,
      oldPassword: '',
      newPassword: '',
      search: '',
      presearch: '',
      perPage: 12,
      sortBy: 'rating',
      backImg,
      userInfo: {
        id: '',
        userName: '',
        email: '',
        avatarUrl: '',
        role: '',
      },
      shops: [],
      myposts: [],
      collectedposts: [],
      error: '',
    }
  },
  created() {
    this.loadUserInfo()
    this.loadShopInfo()
    //load动态
    //load帖子
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
      this.search = ''
      this.presearch = ''
    },
  },
  computed: {
    filtered() {
      const searchs = this.search.trim().toLowerCase()
      if (this.category == '我的动态') {
        //后续需要修改
        return this.myposts.filter(
          (s) =>
            !searchs ||
            s.tags.includes(searchs) ||
            s.shop.shopname.toLowerCase().includes(searchs.toLowerCase()) ||
            s.shop.address.toLowerCase().includes(searchs.toLowerCase()) ||
            s.title.toLowerCase().includes(searchs.toLowerCase()),
        )
      } else if (this.category == '帖子收藏') {
        //后续需要修改
        return this.collectedposts.filter(
          (s) =>
            !searchs ||
            s.tags.includes(searchs) ||
            s.shop.shopname.toLowerCase().includes(searchs.toLowerCase()) ||
            s.shop.address.toLowerCase().includes(searchs.toLowerCase()) ||
            s.title.toLowerCase().includes(searchs.toLowerCase()) ||
            s.user.userName.toLowerCase().includes(searchs.toLowerCase()),
        )
      } else if (this.category == '关注店铺') {
        return this.shops
          .filter(
            (s) =>
              !searchs ||
              s.tags.includes(searchs) ||
              s.shopname.toLowerCase().includes(searchs.toLowerCase()) ||
              s.address.toLowerCase().includes(searchs.toLowerCase()),
          )
          .sort((a, b) => {
            if (this.sortBy === 'rating') return b.rating - a.rating
            if (this.sortBy === 'reviews') return b.reviews - a.reviews
            if (this.sortBy === 'distance') return a.distance - b.distance
            return 0
          })
      }
      return null
    },
    totalPages() {
      return Math.max(1, Math.ceil(this.filtered.length / this.perPage))
    },
    pagedData() {
      const start = (this.page - 1) * this.perPage
      return this.filtered.slice(start, start + this.perPage)
    },
  },
  methods: {
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
    changeCategory(cate) {
      this.category = cate
      this.page = 1
      this.changepage = this.page
      console.log(this.category)
    },
    prevPage() {
      if (this.page > 1) {
        this.page--
      }
      this.changepage = this.page
    },
    changePage() {
      if (this.changepage > 0 && this.changepage <= this.totalPages) {
        this.page = this.changepage
      } else {
        this.changepage = this.page
      }
    },
    nextPage() {
      if (this.page < this.totalPages) {
        this.page++
      }
      this.changepage = this.page
    },
    renderStars(rating) {
      const full = Math.floor(rating)
      const half = rating - full >= 0.5
      let html = ''
      for (let i = 0; i < full; i++) html += '★'
      if (half) html += '☆'
      while (html.length < 5) html += '☆'
      return html
    },
    //后续需修改
    async loadShopInfo() {
      try {
        const response = await getShopInfo()
        if (response.code == 1) {
          this.shops = response.data
          this.$store.dispatch('getShopInfo', this.shops)
        } else {
          this.error = '获取失败'
        }
      } catch (e) {
        console.log(e)
        this.error = '获取失败'
      }
      this.shops = JSON.parse(localStorage.getItem('shopInfo'))
    },
    edit() {
      this.$router.push({ path: '/UserInfo' })
    },
    changePassword() {
      this.$router.push({ path: '/ChangePassword' })
    },
    mainWindow() {
      this.$router.push({ path: '/' })
    },
    searchFor() {
      this.search = this.presearch
      this.page = 1
    },
  },
}
</script>
<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
.page {
  cursor: default;
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  background: linear-gradient(180deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  box-sizing: border-box;
}
.header {
  display: flex;
  flex-direction: column;
  border-width: 2px;
  height: 20%;
}
.home {
  padding: 5px;
  width: 70px;
  height: 30px;
  border-radius: 5px;
  border-width: 1px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  border: 1px solid rgba(0, 0, 0, 0.12);
  cursor: pointer;
}
.person {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
.person img {
  margin-top: 20px;
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 50%;
}
.person button {
  padding: 5px;
  margin: 0px 10px;
  border-radius: 7px;
  border-width: 1px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  width: 100px;
  cursor: pointer;
}
.nav {
  display: flex;
  justify-content: space-between;
}
.nav button {
  padding: 7px;
  border-radius: 7px;
  margin: 0px 5px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  cursor: pointer;
}
.category .active {
  background: #ffedd5;
  border-color: #ffd6a5;
}
.search {
  margin-right: 20px;
}
.search input {
  padding: 8px 10px;
  width: 320px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.nav button {
  padding: 7px;
  border-radius: 7px;
  margin: 0px 5px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  cursor: pointer;
}
.main {
  display: flex;
  flex-direction: column;
  margin: 5px 15px;
}
.shop {
  display: flex;
  flex-direction: column;
  padding: 9px;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(385px, 1fr));
  gap: 10px;
  list-style: none;
  padding: 0;
  margin: 0;
}
.card {
  display: flex;
  flex-direction: row;
  gap: 12px;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  align-items: flex-start;
}
.card img {
  width: 160px;
  height: 130px;
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
  width: 50px;
  padding: 6px 5px;
  border-radius: 6px;
  border: 1px solid #eee;
  background: #fff;
  cursor: text;
  color: #666;
}
.pager button {
  padding: 7px;
  border-radius: 7px;
  margin: 0px 5px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: linear-gradient(
    135deg,
    rgba(141, 255, 253, 0.461) 0%,
    rgba(255, 255, 255, 0.08) 100%
  );
}
.sort {
  margin: 0px 10px 10px 10px;
}
.sort select {
  font-size: 15px;
}
.mainhead {
  display: flex;
  justify-content: space-between;
}
.total {
  color: #999;
  margin-right: 10px;
}
.warn {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 18px 0;
}
.warn img {
  width: 40%;
  height: auto;
  object-fit: contain;
  display: block;
  border-radius: 8px;
  box-shadow: 0 8px 20px rgba(11, 17, 24, 0.08);
  opacity: 0.95;
}
.border {
  margin: 15px 15px 0px 15px;
  border: 1px solid #000;
}
</style>
