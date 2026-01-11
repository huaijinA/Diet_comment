<template>
  <div class="page">
    <header class="header">
      <button class="home" @click="goBack">返回</button>
      <div class="person">
        <img :src="userInfo.avatarUrl" :alt="用户头像" />
        <div>{{ userInfo.userName }}</div>
        <span class="headbutton" v-if="userInfo.id == localuserInfo.id">
          <button @click="edit">编辑资料</button>
          <button @click="changePassword">修改密码</button>
        </span>
        <span class="email" v-else>
          <span>邮箱：{{ userInfo.email }} </span>
        </span>
      </div>
    </header>
    <nav class="nav">
      <span class="category">
        <button
          v-for="cate in ['美食动态', '帖子收藏', '关注店铺']"
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

    <div v-if="isDeleteModalVisible" class="delete-modal-mask" @click.self="closeDeleteWindow">
      <div class="delete-modal">
        <p class="modal-tip">确定要删除这条帖子吗？此操作无法撤销。</p>
        <div class="modal-buttons">
          <button @click="deletePost" class="confirm-btn">确定</button>
          <button @click="closeDeleteWindow" class="cancel-btn">取消</button>
        </div>
      </div>
    </div>

    <main class="main">
      <section v-if="category == '美食动态'">
        <div class="head">
          <span class="total"> 共 {{ filtered.length }} 条动态 </span>
          <span>
            <button @click="choice">{{ !Flag ? '删除' : '取消' }}</button>
          </span>
        </div>
        <div class="warn" v-if="filtered.length == 0">
          <img :src="backImg" :alt="默认图片" />
        </div>
        <ul class="postcards">
          <li v-for="post in pagedData" :key="post.id" class="postcard" @click="goPost(post)">
            <div class="posthead">
              <span>
                <span class="time">{{ post.createdAt }}</span>
              </span>
              <span v-if="Flag">
                <button @click.stop="openDeleteWindow(post)" class="delete">🗑️删除</button>
              </span>
            </div>
            <div class="title">{{ post.title }}</div>
            <div class="content">{{ post.content }}</div>
            <div class="image" v-if="(post.imgurls || []).length <= 3">
              <span v-for="image in post.imgurls" :key="image">
                <img :src="image" :alt="帖子图片" />
              </span>
            </div>
            <div class="image" v-else>
              <span>
                <img :src="post.imgurls[0]" :alt="帖子图片1" />
                <img :src="post.imgurls[1]" :alt="帖子图片2" />
                <img :src="post.imgurls[2]" :alt="帖子图片3" />
              </span>
            </div>
            <div class="postshop" v-if="JSON.stringify(post.shop) != '{}'">
              <div class="shopname">🏠{{ post.shop.name }}</div>
              <div class="shopaddress">{{ post.shop.address }}</div>
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
      <section v-if="category == '帖子收藏'">
        <span class="total"> 共 {{ filtered.length }} 条帖子 </span>
        <div class="warn" v-if="filtered.length == 0">
          <img :src="backImg" :alt="默认图片" />
        </div>
        <ul class="postcards">
          <li v-for="post in pagedData" :key="post.id" class="postcard" @click="goPost(post)">
            <div class="posthead">
              <span class="postuser">
                <img :src="post.user.avatarUrl" :alt="用户头像" />
                <span class="postusername">{{ post.user.userName }}</span>
              </span>
              <span>
                <span class="time">{{ post.createdAt }}</span>
              </span>
            </div>
            <div class="title">{{ post.title }}</div>
            <div class="content">{{ post.content }}</div>
            <div class="image" v-if="(post.imgurls || []).length <= 3">
              <span v-for="image in post.imgurls" :key="image">
                <img :src="image" :alt="帖子图片" />
              </span>
            </div>
            <div class="image" v-else>
              <span>
                <img :src="post.imgurls[0]" :alt="帖子图片1" />
                <img :src="post.imgurls[1]" :alt="帖子图片2" />
                <img :src="post.imgurls[2]" :alt="帖子图片3" />
              </span>
            </div>
            <div class="postshop" v-if="JSON.stringify(post.shop) != '{}'">
              <div class="shopname">🏠{{ post.shop.name }}</div>
              <div class="shopaddress">{{ post.shop.address }}</div>
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
      <section class="shop" v-if="category == '关注店铺'">
        <div class="mainhead">
          <span class="sort">
            排序：
            <select v-model="sortBy">
              <option value="rating">评分</option>
              <option value="reviews">评价数</option>
            </select>
          </span>
          <span class="total"> 共 {{ filtered.length }} 家店 </span>
        </div>
        <div class="warn" v-if="filtered.length == 0">
          <img :src="backImg" :alt="默认图片" />
        </div>
        <ul class="cards">
          <li v-for="shop in pagedData" :key="shop.id" class="card" @click="goShop(shop)">
            <img :src="shop.imgurl" alt="店铺封面" />
            <div class="card-body">
              <div class="row top">
                <h4 class="name">{{ shop.name }}</h4>
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
  </div>
</template>
<script>
import backImg from '@/assets/back1.jpg'
import { getUserPosts, deletePosts, collectedPosts } from '@/api/post'
import { getImage } from '@/api/image'
import { collectedShops } from '@/api/shop'
// import { getShopInfoById } from '@/api/shop'
// import { getUserInfoById } from '@/api/user'
export default {
  data() {
    return {
      category: '美食动态',
      page: 1,
      changepage: 1,
      oldPassword: '',
      newPassword: '',
      search: '',
      presearch: '',
      perPage: 12,
      sortBy: 'rating',
      backImg,
      Flag: false,
      isDeleteModalVisible: false,
      deleteId: null,
      userInfo: {
        id: '',
        userName: '',
        email: '',
        avatarUrl: '',
        role: '',
      },
      localuserInfo: null,
      shops: [],
      myposts: [],
      collectedposts: [],
      error: '',
    }
  },
  created() {
    this.loadUserInfo()
    //load美食动态
    this.loadMyPost()
    //load关注店铺信息
    this.loadShopInfo()
    //load帖子收藏
    this.loadPostInfo()
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
      if (this.category == '美食动态') {
        return this.myposts.filter(
          (s) =>
            !searchs ||
            s.shop?.tags.includes(searchs) ||
            s.shop?.name.toLowerCase().includes(searchs.toLowerCase()) ||
            s.shop?.address.toLowerCase().includes(searchs.toLowerCase()) ||
            s.title.toLowerCase().includes(searchs.toLowerCase()) ||
            s.content.toLowerCase().includes(searchs.toLowerCase()),
        )
      } else if (this.category == '帖子收藏') {
        return this.collectedposts.filter(
          (s) =>
            !searchs ||
            s.shop?.tags.includes(searchs) ||
            s.shop?.name.toLowerCase().includes(searchs.toLowerCase()) ||
            s.shop?.address.toLowerCase().includes(searchs.toLowerCase()) ||
            s.title.toLowerCase().includes(searchs.toLowerCase()) ||
            s.content.toLowerCase().includes(searchs.toLowerCase()) ||
            s.user?.userName.toLowerCase().includes(searchs.toLowerCase()),
        )
      } else if (this.category == '关注店铺') {
        return this.shops
          .filter(
            (s) =>
              !searchs ||
              s.tags.includes(searchs) ||
              s.name.toLowerCase().includes(searchs.toLowerCase()) ||
              s.address.toLowerCase().includes(searchs.toLowerCase()),
          )
          .sort((a, b) => {
            if (this.sortBy === 'rating') return b.rating - a.rating
            if (this.sortBy === 'reviews') return b.reviews - a.reviews
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
      const userinfo = JSON.parse(localStorage.getItem('oneUser'))
      this.localuserInfo = JSON.parse(localStorage.getItem('userInfo'))
      if (userinfo) {
        this.userInfo.id = userinfo.id
        this.userInfo.userName = userinfo.userName
        this.userInfo.email = userinfo.email
        this.userInfo.avatarUrl = userinfo.avatarUrl
        this.userInfo.role = userinfo.role
      } else {
        this.userInfo.id = this.localuserinfo.id
        this.userInfo.userName = this.localuserinfo.userName
        this.userInfo.email = this.localuserinfo.email
        this.userInfo.avatarUrl = this.localuserinfo.avatarUrl
        this.userInfo.role = this.localuserinfo.role
      }
    },
    async loadMyPost() {
      try {
        const response = await getUserPosts(this.userInfo.id)
        if (response.code == 1) {
          this.myposts = response.data.map((post) => ({
            ...post,
            imgurls: [],
            shop: post.shop ? {
              ...post.shop,  // 保留原始 shop 的所有属性
              tags: post.shop.tags || []
            } : {},
          }))
          
          const postIds = this.myposts.map((c) => c.id)
          const imgurlRes = await Promise.all(
            postIds.map((id) => getImage('post', id).then((res) => ({ id, res }))),
          )
          imgurlRes.forEach(({ id, res }) => {
            if (res.code === 1) {
              const post = this.myposts.find((c) => c.id === id)
              if (post) post.imgurls = res.data
            }
          })
        } else {
          console.log('获取失败')
        }
      } catch (e) {
        console.log(e)
      }
    },
    async loadShopInfo() {
      try {
        const response = await collectedShops(this.userInfo.id)
        if (response.code == 1) {
          this.shops = response.data
          this.shops = this.shops.map((shop) => ({
            ...shop,
            tags: shop.tags || []
          }))
        } else {
          this.error = '获取失败'
        }
      } catch (e) {
        console.log(e)
        this.error = '获取失败'
      }
    },
    async loadPostInfo() {
      try {
        const response = await collectedPosts(this.userInfo.id)
        if (response.code == 1) {
          this.collectedposts = response.data.map((post) => ({
            ...post,
            // user: {},
            // shop: {},
            shop: post.shop ? {
              ...post.shop,  // 保留原始 shop 的所有属性
              tags: post.shop.tags || []
            } : {},
            imgurls: [],
          }))
          const postIds = this.collectedposts.map((c) => c.id)
          const imgurlRes = await Promise.all(
            postIds.map((id) => getImage('post', id).then((res) => ({ id, res }))),
          )
          imgurlRes.forEach(({ id, res }) => {
            if (res.code === 1) {
              const post = this.collectedposts.find((c) => c.id === id)
              if (post) post.imgurls = res.data
            }
          })
          // const userIds = this.collectedposts.map((c) => c.userId)
          // const userRes = await Promise.all(userIds.map((id) => getUserInfoById(id)))
          // userRes.forEach((res) => {
          //   if (res.code === 1) {
          //     const targetPosts = this.collectedposts.filter((post) => post.userId === res.data.id)
          //     targetPosts.forEach((post) => {
          //       post.user = res.data
          //     })
          //   }
          // })
          // const shopIds = this.collectedposts.map((c) => c.shopId)
          // const shopRes = await Promise.all(shopIds.map((id) => getShopInfoById(id)))
          // shopRes.forEach((res) => {
          //   if (res.code === 1) {
          //     const targetPosts = this.collectedposts.filter((post) => post.shopId === res.data.id)
          //     targetPosts.forEach((post) => {
          //       post.shop = res.data
          //     })
          //   }
          // })
          console.log(this.collectedposts)
        } else {
          console.log('获取失败')
          this.error = '获取失败'
        }
      } catch (e) {
        console.log(e)
      }
    },
    changeCategory(cate) {
      this.category = cate
      this.page = 1
      this.changepage = this.page
      if (this.category == '关注店铺') {
        this.perPage = 12
      } else if (this.category == '帖子收藏') {
        this.perPage = 12
      } else if (this.category == '美食动态') {
        this.perPage = 12
      }
    },
    prevPage() {
      if (this.page > 1) {
        this.page--
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
      this.changepage = this.page
    },
    changePage() {
      if (this.changepage > 0 && this.changepage <= this.totalPages) {
        this.page = this.changepage
        window.scrollTo({ top: 0, behavior: 'smooth' })
      } else {
        this.changepage = this.page
      }
    },
    nextPage() {
      if (this.page < this.totalPages) {
        this.page++
        window.scrollTo({ top: 0, behavior: 'smooth' })
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
    edit() {
      this.$router.push({ path: '/UserInfo' })
    },
    changePassword() {
      this.$router.push({ path: '/ChangePassword' })
    },
    goBack() {
      this.$router.back()
      this.$store.dispatch('clearOneUser')
    },
    searchFor() {
      this.search = this.presearch
      this.page = 1
    },
    goPost(post) {
      this.$store.dispatch('getOnePost', post)
      this.$router.push({ path: '/PostWindow' })
    },
    goShop(shop) {
      this.$store.dispatch('getOneShop', shop)
      this.$router.push({ path: '/ShopWindow' })
    },
    choice() {
      this.Flag = !this.Flag
    },
    openDeleteWindow(post) {
      this.isDeleteModalVisible = true
      this.deleteId = post.id
    },
    closeDeleteWindow() {
      this.isDeleteModalVisible = false
      this.deleteId = null
    },
    deletePost() {
      try {
        const response = deletePosts(this.deleteId)
        if (response.code == 1) {
          this.loadMyPost()
          alert('删除成功')
        }
      } catch (e) {
        console.log(e)
      }
    },
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
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 5px;
  border-width: 1px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
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
.email {
  font-size: 13px;
  color: #8f8f8f;
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
.head {
  display: flex;
  justify-content: space-between;
  padding: 10px 15px;
}
.head button {
  padding: 5px;
  width: 70px;
  border-radius: 7px;
  border-width: 1px;
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
/* 帖子 */
.postcards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(460px, 1fr));
  gap: 30px;
  list-style: none;
  padding: 0;
  margin: 0;
}
.postcard {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  gap: 12px;
  background: #fff6e8;
  padding: 15px;
  border-radius: 8px;
  align-items: flex-start;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  margin: 5px 10px 5px 10px;
}
.posthead {
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 1px;
}
.postuser {
  display: flex;
  gap: 13px;
  font-size: 10px;
  align-items: center;
}
.postusername {
  display: flex;
  font-size: 15px;
}
.postuser img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.06);
}
.time {
  height: 100%;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
  color: #b3b3b3;
  /* 只显示年月日 */
  font-family: monospace;
  width: 10ch;
  white-space: nowrap;
  overflow: hidden;
  text-align: right;
}
.delete {
  padding: 5px 10px;
  border: none;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  color: black;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.image {
  width: 100%;
}
.image img {
  width: 140px;
  height: 160px;
  margin-right: 10px;
  object-fit: cover;
  border-radius: 5px;
}
.title {
  display: flex;
  width: 100%;
  justify-content: center;
  text-align: center;
  font-size: 18px;
  font-weight: 500;
}
.content {
  font-size: 13px;
  color: rgb(99, 99, 98);
  padding: 5px;
  line-height: 2.2;
  /* 截断 */
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}
.postshop {
  width: 100%;
  justify-items: end;
}
.shopname {
  font-size: 13px;
}
.shopaddress {
  font-size: 10px;
  color: #b3b3b3;
}
/* 店铺 */
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

/* 删除窗口 */
.delete-modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999; /* 确保在最上层 */
}

/* 模态框主体：白色卡片 */
.delete-modal {
  width: 320px;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}
/* 提示文字 */
.modal-tip {
  font-size: 14px;
  color: #333;
  margin-bottom: 20px;
}
/* 按钮容器：水平排列 */
.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
}
/* 取消按钮：灰色 */
.cancel-btn {
  padding: 8px 16px;
  border: 1px solid #eee;
  border-radius: 4px;
  background-color: #fff;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}
.cancel-btn:hover {
  background-color: #f5f5f5;
}
/* 确定按钮：红色（警示色） */
.confirm-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: #ff4d4f;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}
.confirm-btn:hover {
  background-color: #ff3b30;
}
</style>
