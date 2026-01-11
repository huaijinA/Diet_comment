<template>
  <button class="home" @click="goBack">返回</button>

  <header class="header">
    <span class="img">
      <img :src="imgurl" :alt="店铺图片" />
    </span>
    <span class="headertext">
      <div class="shopname">{{ shopname }}</div>

      <div class="rate">
        <span class="rating">{{ rating }}</span>
        <span class="imgrating" v-html="renderStarsHtml(rating)"></span>
      </div>

      <span class="tags" v-for="tag in tags" :key="tag">{{ tag }}</span>
      <div class="reviews">共{{ reviews }}条帖子</div>
      <div class="price">平均消费￥{{ price }}</div>
      <div class="address">地址：{{ address }}</div>
    </span>
    <span class="fav">
      <button @click="toggleFav" class="fav-btn2">{{ fav ? '★' : '☆' }}</button>
      <span class="favnum">{{ favNum }}</span>
      <span class="rate-btn">
        <button @click="openRating" class="fav-btn">
          {{ self_rating ? '已评分 ' + self_rating + '★' : '评分 ☆' }}
        </button>
      </span>
    </span>
  </header>
  <div class="tabs">
    <button :class="{ active: tab === '菜品' }" @click="tab = '菜品'">
      菜品 ({{ dishes.length }})
    </button>
    <button :class="{ active: tab === '帖子' }" @click="tab = '帖子'">
      帖子 ({{ posts.length }})
    </button>
  </div>
  <main class="main">
    <div class="border"></div>
    <ul class="dishes" v-if="tab === '菜品'">
      <li class="dish" v-for="food in dishes" :key="food.name">
        <img :src="food.imgurl" :alt="菜品图片" />
        <div class="dishtext">
          <span class="foodprice">￥{{ food.price }}</span>
          <span class="foodname">{{ food.name }}</span>
        </div>
      </li>
    </ul>
    <ul class="posts" v-if="tab === '帖子'">
      <li class="post" v-for="post in posts" :key="post.id" @click="goPost(post)">
        <div class="posthead">
          <span class="user">
            <img :src="post.user.avatarUrl" :alt="用户头像" />
            <span class="username">{{ post.user.userName }}</span>
          </span>
          <span>
            <span class="time">{{ post.createdAt }}</span>
          </span>
        </div>
        <div class="title">{{ post.title }}</div>
        <div class="content">{{ post.content }}</div>
        <div class="image" v-if="(post.imgurls || []).length <= 4">
          <span v-for="image in post.imgurls" :key="image">
            <img :src="image" :alt="帖子图片" />
          </span>
        </div>
        <div class="image" v-else>
          <span>
            <img :src="post.imgurls[0]" :alt="帖子图片1" />
            <img :src="post.imgurls[1]" :alt="帖子图片2" />
            <img :src="post.imgurls[2]" :alt="帖子图片3" />
            <img :src="post.imgurls[3]" :alt="帖子图片4" />
          </span>
          <span class="last">🖼️+{{ (post.imgurls || []).length - 4 }}</span>
        </div>
      </li>
    </ul>
  </main>
  <!-- 评分窗口 -->
  <div class="modal-mask" v-if="ratingModal">
    <div class="modal-wrapper" @click.self="closeRating">
      <div class="modal-container">
        <h3>为「{{ shopname }}」打分</h3>

        <div class="imgrating star-picker" @mouseleave="hoverValue = 0">
          <span
            v-for="i in 5"
            :key="i"
            class="star"
            :class="starType(i)"
            @mousemove="onStarMouse($event, i)"
            @click="chooseStar(i)"
            style="cursor: pointer"
            >★</span
          >
        </div>

        <div class="picker-info">当前：{{ hoverValue || self_rating || 0 }} 分</div>

        <div class="modal-actions">
          <button @click="confirmRating">确认打分</button>
          <button @click="clearRating">清除评分</button>
          <button @click="closeRating">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  getDishes,
  getRating,
  saveRating,
  deleteRating,
  collectShops,
  uncollectShops,
  shopStatus,
  getShopCollectNum,
} from '@/api/shop'
import { getShopPosts } from '@/api/post'
import { getImage } from '@/api/image'
export default {
  data() {
    return {
      id: null,
      shopname: null,
      rating: null,
      reviews: null,
      tags: [],
      price: null,
      address: null,
      imgurl: null,
      self_rating: null,
      fav: null,
      post_fav: 1,
      tab: '菜品',
      dishes: [],
      posts: [],
      error: null,
      user: JSON.parse(localStorage.getItem('userInfo')),
      hoverValue: 0,
      ratingModal: false,
      favNum: 0,
    }
  },
  created() {
    this.getShopInfo()
    this.GetDishes()
    this.GetPosts()
  },
  onBeforeUnmount() {
    const shop = JSON.parse(localStorage.getItem('oneShop'))
    shop.fav = this.fav
    this.$store.dispatch('getOneShop', shop)
  },
  methods: {
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
    //获取店铺信息
    async getShopInfo() {
      const shop = JSON.parse(localStorage.getItem('oneShop'))
      this.id = shop.id
      this.shopname = shop.name
      this.rating = shop.rating
      this.reviews = shop.reviews
      this.tags = shop.tags
      this.price = shop.price
      this.address = shop.address
      this.imgurl = shop.imgurl
      try {
        const response = await getRating(this.user.id, shop.id)
        if (response.code == 1) {
          this.self_rating = response.data
        } else {
          console.log(response.message)
        }
        shopStatus(this.id, this.user.id).then((response) => {
          if (response.code == 1) {
            const fav = response.data.collected
            if (fav == 1) this.fav = true
            else this.fav = false
          }
        })
        getShopCollectNum(this.id).then((response) => {
          if (response.code == 1) {
            this.favNum = response.data.num
          }
        })
      } catch (e) {
        console.log(e)
      }
    },
    //获取菜单
    async GetDishes() {
      try {
        const response = await getDishes(this.id)
        if (response.code === 1) {
          this.dishes = response.data
        } else {
          this.error = '获取店铺菜单信息失败'
          console.log(this.error)
        }
      } catch (error) {
        console.log(error)
      }
    },
    //获取帖子
    async GetPosts() {
      try {
        const response = await getShopPosts(this.id)
        if (response.code === 1) {
          this.posts = response.data.map((post) => ({
            ...post,
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
        } else {
          this.error = '获取店铺帖子信息失败'
          console.log(this.error)
        }
      } catch (error) {
        console.log(error)
      }
    },
    //收藏函数
    toggleFav() {
      if (this.fav) {
        uncollectShops(this.id, this.user.id)
        this.favNum = this.favNum > 0 ? this.favNum - 1 : 0
      } else {
        collectShops(this.id, this.user.id)
        this.favNum += 1
      }
      this.fav = !this.fav
    },
    //设置用户评分
    async setUserRating(value) {
      const v = Math.max(0, Math.min(5, Number(value)))
      this.self_rating = v
      try {
        await this.saveRating()
      } catch (e) {
        console.error('保存评分失败', e)
      }
    },
    // 清除用户评分
    async clearRating() {
      try {
        this.hoverValue = null
        this.self_rating = null
        const response = await deleteRating(this.user.id, this.id)
        if (response.code == 1) {
          this.self_rating = null
          this.closeRating()
          console.log('删除成功')
        } else {
          console.log(response.message)
        }
      } catch (e) {
        console.error(e)
      }
    },
    //打分
    async saveRating() {
      try {
        const response = await saveRating(this.user.id, this.id, this.self_rating)
        if (response.code == 1) {
          this.self_rating = response.data
        } else {
          console.log(response.message)
        }
      } catch (e) {
        console.log(e)
      }
    },
    goBack() {
      this.$router.back()
      this.$store.dispatch('clearOneShop')
    },
    goPost(post) {
      this.$store.dispatch('getOnePost', post)
      this.$router.push({ path: '/PostWindow' })
    },
    // 打分弹窗控制
    openRating() {
      this.ratingModal = true
      this.hoverValue = 0
    },
    closeRating() {
      this.ratingModal = false
      this.hoverValue = 0
    },
    onStarMouse(e, i) {
      const rect = e.target.getBoundingClientRect()
      const x = e.clientX - rect.left
      this.hoverValue = x < rect.width / 2 ? i - 0.5 : i
    },
    // 点击选择
    chooseStar(i) {
      this.self_rating = this.hoverValue || i
    },
    // 根据当前（hoverValue 或 self_rating）判断单个星的类型
    starType(i) {
      const v = Number(this.hoverValue || this.self_rating || 0)
      if (v >= i) return 'star-full'
      if (v >= i - 0.5) return 'star-half'
      return 'star-empty'
    },
    // 确认并保存评分
    async confirmRating() {
      try {
        await this.saveRating()
        this.closeRating()
      } catch (e) {
        console.error('保存评分失败', e)
      }
    },
  },
}
</script>
<style scoped>
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
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
.header {
  cursor: default;
  display: flex;
  margin: 10px 10px;
  border-radius: 5px;
  background: linear-gradient(135deg, #ffd7a876 0%, rgba(253, 219, 52, 0.08) 100%);
}
.img {
  display: flex;
  justify-content: center;
  align-items: center;
}
.header img {
  margin: 20px 30px;
  width: 170px;
  border-radius: 5px;
}
.header button {
  margin: 20px 0px;
}
.headertext {
  margin: 20px 0px;
  width: 80%;
}
.shopname {
  font-size: 20px;
  margin: 10px 0px;
}
.rating {
  color: coral;
  margin-left: 5px;
  font-size: 15px;
}
.imgrating {
  margin-left: 10px;
  color: orange;
}
.rate {
  display: flex;
  margin: 10px 0px;
}
.tags {
  display: inline-block;
  background: #f4f6f8;
  padding: 3px 6px;
  border-radius: 12px;
  margin-right: 6px;
  font-size: 15px;
  color: #555;
}
.reviews {
  font-size: 12px;
  color: orange;
  margin: 10px 0px;
}
.price {
  font-size: 12px;
  color: orange;
  margin: 10px 0px;
}
.address {
  font-size: 15px;
  margin: 10px 0px;
}
.distance {
  font-size: 15px;
  color: gray;
  margin: 10px 0px;
}
.fav-btn2 {
  margin-left: 8px;
  border: none;
  width: fit-content;
  font-size: 20px;
  color: orange;
  background: none;
  padding: 4px 8px;
}
.fav-btn {
  margin-left: 8px;
  border-radius: 5px;
  width: fit-content;
  border: none;
  color: #ffc051;
  padding: 4px 8px;
  background-color: #62fdf8a8;
  cursor: pointer;
}
.favnum {
  margin-left: 5px;
  font-size: 15px;
  color: #9a9a9a;
}
.tabs button {
  padding: 3px;
  color: black;
  border: 1px solid gray;
  border-radius: 5px;
  margin-left: 10px;
  background: none;
}
.tabs button.active {
  background: #ffedd5;
  border-color: #ffd6a5;
}
.main {
  margin: 10px 10px;
  border-radius: 5px;
}
.border {
  border: 1px solid #b3b3b3;
  margin: 15px 0px 15px 0px;
}
/* 食品 */
.dishes {
  margin: 5px 10px;
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}
.dishtext {
  margin: 8px 0px;
  font-size: 15px;
}
.foodprice {
  margin: 0px 10px;
  font-size: 12px;
  color: #555;
}
.dishes img,
.dish img {
  width: 200px;
  height: 250px;
  object-fit: cover; /* 保持长宽比并裁剪多余部分 */
  object-position: center; /* 裁剪时以中心为基准 */
  display: block;
  border-radius: 6px;
  flex-shrink: 0;
}
.dish {
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 150px;
  border-radius: 5px;
  border: 1px solid #b1b1b1;
  overflow: hidden;
}
/* 帖子 */
.posts {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(600px, 1fr));
  gap: 30px;
}
.post {
  margin: 20px 20px;
  border-radius: 8px;
  padding: 20px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
}
.posthead {
  flex: 1;
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 10px;
}
.user {
  display: flex;
  gap: 13px;
  font-size: 15px;
  align-items: center;
}
.user img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.06);
}
.image {
  margin: 0px 10px 0px 5px;
}
.image img {
  width: 140px;
  height: 150px;
  margin-right: 10px;
  object-fit: cover;
  border-radius: 5px;
}
.username {
  font-size: 15px;
}
.title {
  display: flex;
  justify-content: center;
  font-size: 20px;
  font-weight: 500;
  margin: 15px 0px;
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
.content {
  font-size: 15px;
  color: rgb(99, 99, 98);
  padding: 5px;
  line-height: 1.6;
  /* 截断 */
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}
.last {
  font-size: 10px;
  color: #555;
}

/* 打分弹窗样式 */
.modal-mask {
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-wrapper {
  width: 100%;
  max-width: 520px;
  padding: 20px;
}
.modal-container {
  background: #fff;
  border-radius: 8px;
  padding: 18px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}
.star-picker {
  margin: 12px 0;
  font-size: 28px;
  color: #dfdfdf;
}
.star-picker .star {
  position: relative;
  display: inline-block;
  width: 36px;
  height: 36px;
  line-height: 36px;
  font-size: 36px;
  color: #dfdfdf;
  text-align: center;
  margin-right: 6px;
}
.star-picker .star::before {
  content: '★';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  overflow: hidden;
  width: 0%;
  pointer-events: none;
  color: #ffc051;
  transition: width 0.08s ease;
  z-index: 1;
}
.star-picker .star-full::before {
  width: 100%;
}
.star-picker .star-half::before {
  width: 50%;
}
.picker-info {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}
.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
.rate-btn button {
  margin-left: 8px;
  border-radius: 5px;
  width: fit-content;
  border: none;
  color: #ffc051;
  padding: 8px 12px;
  background-color: #62fdf8a8;
  cursor: pointer;
}
.modal-actions button {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: #fff;
  cursor: pointer;
  transition:
    transform 0.12s ease,
    box-shadow 0.12s ease;
}
.modal-actions button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 26px rgba(11, 17, 24, 0.06);
}
.modal-actions .confirm {
  background: linear-gradient(90deg, #ff8b1f, #ff6a00);
  color: #fff;
  border: none;
  font-weight: 700;
}
.modal-actions .confirm:hover {
  box-shadow: 0 14px 36px rgba(255, 106, 0, 0.12);
}
</style>

<style>
.imgrating {
  display: inline-block;
  vertical-align: middle;
}
.imgrating .star {
  position: relative;
  display: inline-block;
  width: 18px;
  height: 18px;
  line-height: 18px;
  font-size: 18px;
  color: #dfdfdf;
  text-align: center;
  margin-right: 2px;
  z-index: 0;
}
.imgrating .star::before {
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
.imgrating .star-full::before {
  width: 100%;
}
.imgrating .star-half::before {
  width: 50%;
}
</style>
