<template>
  <div class="home-page">
    <header class="topbar">
      <div class="logogroup">
        <span class="logo">小众点评 </span>
        <button @click="goMainShop">进入美食广场🚩</button>
      </div>
      <div class="search">
        <input
          v-model="presearch"
          @keyup.enter="doSearch"
          placeholder="搜索帖子（商家、用户、位置...）"
        />
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
        <h2>欢迎来到味蕾社区</h2>
        <p>发现美食 · 用户点评 · 精选推荐</p>
      </div>
    </section>

    <main class="content">
      <section class="list">
        <div class="summary">
          <span>共 {{ posts.length }} 条帖子</span>
          <button @click="newPost">✒️写点什么</button>
        </div>
        <ul class="cards">
          <li v-for="post in posts" :key="post.id" class="card" @click="goPost(post)">
            <div class="posthead">
              <span class="postuser">
                <img :src="post.user.avatar" :alt="用户头像" />
                <span class="postusername">{{ post.user.username }}</span>
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
            <div class="shop" v-if="JSON.stringify(post.shop) != '{}'">
              <div class="shopname">🏠{{ post.shop.name }}</div>
              <div class="shopaddress">{{ post.shop.address }}</div>
            </div>
          </li>
          <div class="end">已经到底啦！可以去别处逛逛！</div>
        </ul>
      </section>
    </main>

    <footer class="foot">美食论坛主页</footer>
  </div>
</template>

<script>
import { getMainPosts, searchPosts } from '@/api/post'
import { getImage } from '@/api/image'
import { getShopInfoById } from '@/api/shop'
import { getUserInfoById } from '@/api/user'
export default {
  name: 'MainPostWindowPage',
  data() {
    return {
      avatarflag: false,
      error: '',
      posts: [],
      search: '',
      presearch: '',
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
        this.posts = JSON.parse(localStorage.getItem('postInfo'))
      }
    },
  },
  created() {
    this.loadUserInfo()
    this.GetMainPosts()
  },
  mounted() {
    document.addEventListener('click', this.onDocClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.onDocClick)
    this.$store.dispatch('clearPostInfo')
  },
  methods: {
    //收起页面
    onDocClick(e) {
      if (this.avatarflag && this.$refs.avatarBtn && !this.$refs.avatarBtn.contains(e.target)) {
        this.avatarflag = false
      }
    },
    //获取热门帖子信息
    async GetMainPosts() {
      try {
        const response = await getMainPosts()
        if (response.code === 1) {
          this.posts = response.data.map((post) => ({
            ...post,
//            user: {},
//            shop: {},
            imgurls: [],
          }))
          const postIds = this.posts.map((c) => c.id)
          const imgurlRes = await Promise.all(
            postIds.map((id) => getImage('post', id).then((res) => ({ id, res }))),
          )
          imgurlRes.forEach(({ id, res }) => {
            if (res.code === 1) {
              const post = this.posts.find((c) => c.id === id)
              if (post) post.imgurls = res.data
            }
          })
          //获取用户和商店信息
          // const userIds = this.posts.map((c) => c.userId)
          // const userRes = await Promise.all(userIds.map((id) => getUserInfoById(id)))
          // userRes.forEach((res) => {
          //   if (res.code === 1) {
          //     const targetPosts = this.posts.filter((post) => post.userId === res.data.id)
          //     targetPosts.forEach((post) => {
          //       post.user = res.data
          //     })
          //   }
          // })
          // const shopIds = this.posts.map((c) => c.shopId)
          // const shopRes = await Promise.all(shopIds.map((id) => getShopInfoById(id)))
          // shopRes.forEach((res) => {
          //   if (res.code === 1) {
          //     const targetPosts = this.posts.filter((post) => post.shopId === res.data.id)
          //     targetPosts.forEach((post) => {
          //       post.shop = res.data
          //     })
          //   }
          // })
          this.$store.dispatch('getPostInfo', this.posts)
          console.log(this.posts)
        } else {
          this.error = '获取店铺信息失败'
          console.log(this.error)
        }
      } catch (error) {
        console.log(error)
      }
    },
    //搜索
    async doSearch() {
      this.search = this.presearch
      if (this.search === '') {
        this.posts = JSON.parse(localStorage.getItem('postInfo'))
        console.log(this.posts)
        return
      }
      try {
        const response = await searchPosts(this.search)
        if (response.code === 1) {
          this.posts = response.data.map((post) => ({
            ...post,
            imgurls: [],
            user: {},
            shop: {},
          }))
          const postIds = this.posts.map((c) => c.id)
          const imgurlRes = await Promise.all(
            postIds.map((id) => getImage('post', id).then((res) => ({ id, res }))),
          )
          imgurlRes.forEach(({ id, res }) => {
            if (res.code === 1) {
              const post = this.posts.find((c) => c.id === id)
              if (post) post.imgurls = res.data
            }
          })
          const userIds = this.posts.map((c) => c.userId)
          const userRes = await Promise.all(userIds.map((id) => getUserInfoById(id)))
          userRes.forEach((res) => {
            if (res.code === 1) {
              const targetPosts = this.posts.filter((post) => post.userId === res.data.id)
              targetPosts.forEach((post) => {
                post.user = res.data
              })
            }
          })
          const shopIds = this.posts.map((c) => c.shopId)
          const shopRes = await Promise.all(shopIds.map((id) => getShopInfoById(id)))
          shopRes.forEach((res) => {
            if (res.code === 1) {
              const targetPosts = this.posts.filter((post) => post.shopId === res.data.id)
              targetPosts.forEach((post) => {
                post.shop = res.data
              })
            }
          })
        } else {
          this.error = '获取搜索帖子信息失败'
          console.log(this.error)
        }
      } catch (error) {
        console.log(error)
      }
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
    newPost() {
      this.$router.push({ path: '/NewPostWindow' })
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
    //进入帖子
    goPost(post) {
      this.$store.dispatch('getOnePost', post)
      this.$router.push({ path: '/PostWindow' })
    },
    //去商店板块
    goMainShop() {
      this.$router.push({ path: '/MainShopWindow' })
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
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.hero {
  background: linear-gradient(90deg, #fff7f0, #fff);
  padding: 10px 5px;
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
.list {
  flex: 1;
}
.summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  color: #666;
  font-size: 13px;
}
.summary button {
  margin-right: 10px;
  border-radius: 5px;
  border: none;
  padding: 8px 12px;
  background-color: #4af8de;
  cursor: pointer;
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
.username {
  font-size: 10px;
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
/* 帖子 */
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(460px, 1fr));
  gap: 30px;
  list-style: none;
  padding: 0;
  margin: 0;
  min-height: 675px;
}
.card {
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
  padding: 5px;
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
.image img {
  width: 150px;
  height: 180px;
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
.shop {
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
.end {
  width: 100%;
  text-align: center;
  align-items: flex-end;
  font-size: 15px;
  color: #9a9a9a;
  margin-bottom: 20px;
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
}
</style>
