<template>
  <div class="error-page">
    <img :src="picture" :alt="404" />
    <h1>404 Not Found</h1>
    <p>抱歉，您访问的页面不存在。</p>
    <router-link to="/" class="home-link">返回主页</router-link>
  </div>
</template>
<script>
import picture from '@/assets/404.jpg'
export default {
  data() {
    return {
      picture,
    }
  },
}
</script>
<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
  font-family: Arial, sans-serif;
  color: #333;
}
.error-page img {
  width: 200px;
}

.home-link {
  margin-top: 20px;
  padding: 10px 20px;
  color: white;
  background-color: #ff4c4c;
  border-radius: 5px;
  text-decoration: none;
}

.home-link:hover {
  background-color: #ff4c4c;
}
</style>
