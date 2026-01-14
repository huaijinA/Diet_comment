<template>
  <div class="window">
    <!-- 预览图片 -->
    <div class="image-preview-modal" v-if="showModal" @click.self="closePreview">
      <div class="preview-container">
        <img :src="currentImageUrl" class="preview-image" :alt="预览图片" />
        <button class="close-btn" @click="closePreview">×</button>
        <div>
          <button class="pre-btn" @click="preImage" :disabled="currentIndex <= 0">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M15 18l-6-6 6-6" />
              <!-- 左箭头路径 -->
            </svg>
          </button>
          <button class="next-btn" @click="nextImage" :disabled="currentIndex >= currentLength - 1">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M9 18l6-6-6-6" />
              <!-- 右箭头路径 -->
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div class="header">
      <button @click="goBack">返回</button>
    </div>
    <div class="post">
      <input
        type="file"
        ref="fileInput"
        @change="onFileChange"
        accept="image/*"
        multiple
        style="display: none"
      />
      <div class="title">
        <textarea
          v-model="title"
          class="title-input"
          placeholder="请输入标题（最多2行）"
          rows="2"
          maxlength="120"
          required
        ></textarea>
      </div>
      <div class="content">
        <textarea v-model="content" placeholder="请输入正文" required></textarea>
      </div>
      <div class="foot">
        <label class="shop-select">
          <select v-model="shopName">
            <option value="" disabled>选择店铺</option>
            <option v-for="s in shopNames || []" :key="s" :value="s">
              {{ s }}
            </option>
          </select>
        </label>
      </div>
      <span class="more">
        <button class="preimage" @click="chooseImg">🖼️</button>
        <button class="submit" @click="submit">发布</button>
      </span>
      <span class="replyImg">
        <span v-for="(image, index) in replyPreviewImages" :key="image" class="previewImage">
          <button class="removeBtn" @click="removePreview(index)">×</button>
          <img :src="image" @click="openPreview(replyPreviewImages, index)" :alt="回复图片" />
        </span>
        <div>
          <span class="error">{{ error }}</span>
          <span class="success">{{ success }}</span>
        </div>
      </span>
    </div>
  </div>
</template>
<script>
import { newPosts } from '@/api/post'
import { getShopInfo } from '@/api/shop'
import { uploadImage } from '@/api/image'
export default {
  data() {
    return {
      title: '',
      content: '',
      replyPreviewImages: [],
      ImagesFiles: [],
      shops: null,
      shopNames: [],
      shopName: '',
      showModal: false,
      currentImageUrls: [],
      currentImageUrl: '',
      currentIndex: null,
      currentLength: 0,
      error: '',
      success: '',
    }
  },
  created() {
    this.GetShopInfo()
  },
  beforeUnmount() {
    this.$store.dispatch('clearShopInfo')
  },
  methods: {
    //获取店铺信息
    async GetShopInfo() {
      try {
        const response = await getShopInfo()
        if (response.code === 1) {
          this.shops = response.data
          this.$store.dispatch('getShopInfo', this.shops)
          this.shopNames = this.shops.map((shop) => shop.name)
        } else {
          this.error = '获取店铺信息失败'
          console.log(this.error)
        }
      } catch (error) {
        console.log(error)
      }
    },
    //返回
    goBack() {
      this.$router.back()
    },
    //打开图片预览
    openPreview(images, index) {
      this.currentImageUrls = images
      this.currentImageUrl = images[index]
      this.currentIndex = index
      this.currentLength = images.length
      this.showModal = true
    },
    //关闭图片预览
    closePreview() {
      this.showModal = false
    },
    //上一张图片
    preImage() {
      this.currentIndex--
      this.currentImageUrl = this.currentImageUrls[this.currentIndex]
    },
    //下一张图片
    nextImage() {
      if (this.currentIndex + 1 >= this.currentLength) {
        return
      }
      this.currentIndex++
      this.currentImageUrl = this.currentImageUrls[this.currentIndex]
    },
    //打开文件选择照片
    chooseImg() {
      this.$refs.fileInput.click()
    },
    //选择照片
    async onFileChange(event) {
      const files = Array.from(event.target.files || [])
      if (!files.length) return

      const MAX = 9 // 最多保留9张照片
      const available = Math.max(0, MAX - this.replyPreviewImages.length)
      if (available <= 0) {
        this.error = `最多只能上传 ${MAX} 张图片`
        if (this.$refs.fileInput) this.$refs.fileInput.value = ''
        return
      }
      //取前available张照片
      const toRead = files.slice(0, available)
      //转化为base64
      const readAsDataURL = (file) =>
        new Promise((resolve, reject) => {
          const reader = new FileReader()
          reader.onload = (e) => resolve(e.target.result)
          reader.onerror = reject
          reader.readAsDataURL(file)
        })

      this.ImagesFiles = this.ImagesFiles.concat(toRead)

      try {
        const results = await Promise.all(toRead.map(readAsDataURL))
        // 合并到现有预览数组
        this.replyPreviewImages = this.replyPreviewImages.concat(results)
        this.error = ''
      } catch (e) {
        console.error('读取图片失败', e)
        this.error = '读取图片失败'
      } finally {
        if (this.$refs.fileInput) this.$refs.fileInput.value = ''
      }
    },
    //删除预览照片
    removePreview(index) {
      this.replyPreviewImages.splice(index, 1)
      if (this.ImagesFiles && this.ImagesFiles.length > index) {
        this.ImagesFiles.splice(index, 1)
      }
    },
    //发布
    async submit() {
      if (!(this.title && this.content && this.shopName)) {
        this.success = ''
        this.error = '请完整填写'
        return
      }
      try {
        const response = await newPosts(this.title, this.content, this.shopName, null)
        if (response.code == 1) {
          if (this.ImagesFiles.length > 0) {
            const imgResponse = await uploadImage(response.data.id, 'post', this.ImagesFiles)
            if (imgResponse.code != 1) {
              this.error = '照片上传失败'
              this.success = ''
              return
            }
          }
          this.error = ''
          this.success = '发布成功!'
          console.log(response.message)
          setTimeout(() => {
            this.$router.push('/')
          }, 1000)
        }
      } catch (e) {
        console.log(e)
      }
    },
  },
}
</script>
<style scoped>
.window {
  width: 100%;
  min-height: 100vh;
  padding: 28px 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(180deg, #fffaf6 0%, #fffefc 100%);
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  color: #222;
}
.header {
  width: 860px;
  max-width: calc(100% - 32px);
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.header button {
  padding: 5px;
  width: 70px;
  height: 30px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 5px;
  border-width: 1px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  cursor: pointer;
}
.post {
  width: 860px;
  max-width: calc(100% - 32px);
  min-height: 500px;
  border: 1px solid rgba(17, 24, 39, 0.04);
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  box-sizing: border-box;
  background: #ffffff;
  padding: 18px;
  box-shadow: 0 12px 36px rgba(11, 17, 24, 0.06);
  gap: 12px;
}
.title {
  width: 100%;
  box-sizing: border-box;
}
.title-input {
  width: 100%;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid rgba(16, 24, 40, 0.06);
  font-size: 18px;
  font-weight: 600;
  background: #fffefc;
  outline: none;
  resize: vertical;
  line-height: 1.4;
  min-height: calc(1.4em * 2 + 24px);
  max-height: calc(1.4em * 4 + 24px);
  box-shadow: 0 6px 18px rgba(255, 140, 35, 0.04);
  box-sizing: border-box;
  vertical-align: top;
}
.title-input:focus {
  border-color: rgba(255, 140, 35, 0.28);
  box-shadow: 0 10px 30px rgba(255, 140, 35, 0.06);
}
.content {
  display: flex;
  width: 100%;
}
.content textarea {
  width: 100%;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid rgba(16, 24, 40, 0.06);
  font-size: 15px;
  min-height: 160px;
  height: auto;
  background: #fbfbfb;
  resize: vertical;
  outline: none;
  line-height: 1.6;
  box-sizing: border-box;
  vertical-align: top;
}
.content textarea:focus {
  border-color: rgba(255, 140, 35, 0.28);
  box-shadow: 0 6px 18px rgba(255, 140, 35, 0.06);
}
.more {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.more .left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.preimage {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(90deg, #fff7ed, #fff2e6);
  border: 1px solid rgba(255, 140, 35, 0.08);
  cursor: pointer;
  font-size: 20px;
  transition:
    transform 0.12s ease,
    box-shadow 0.12s ease;
}
.preimage:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(255, 140, 35, 0.08);
}

.submit {
  min-width: 96px;
  height: 44px;
  border-radius: 10px;
  background: linear-gradient(90deg, #feae63, #ffa15dc7);
  color: #fff;
  border: 0;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(255, 106, 0, 0.12);
  transition:
    transform 0.12s ease,
    opacity 0.12s ease;
}
.submit:active {
  transform: translateY(1px);
}
.submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
/* 照片 */
.replyImg {
  gap: 10px;
  margin-left: 10px;
  margin-right: 10px;
  display: flex;
}
.previewImage {
  width: 100px;
  height: 150px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
  border-radius: 6px;
  margin-bottom: 10px;
}
.previewImage img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  border: 0;
}
.removeBtn {
  position: absolute;
  top: 6px;
  right: 6px;
  background: rgba(0, 0, 0, 0.55);
  border: none;
  color: #fff;
  font-size: 14px;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  cursor: pointer;
}
/* 预览图片 */
.image-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999; /* 确保在最上层 */
}
.preview-container {
  max-width: 90%;
  max-height: 90%;
  position: relative;
}
.preview-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border-radius: 5px;
}
/* 关闭按钮 */
.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 30px;
  cursor: pointer;
}
.pre-btn,
.next-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%); /* 精确垂直居中 */
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease; /* 过渡动画*/
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.pre-btn {
  left: -50px;
}
.next-btn {
  right: -50px;
}
.pre-btn:disabled,
.next-btn:disabled {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.3);
  cursor: not-allowed;
}
/* Hover效果:增强交互 */
.pre-btn:not(:disabled):hover,
.next-btn:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
}
.success {
  color: rgb(75, 255, 75);
  font-size: 12px;
  margin-top: 10px;
}
.error {
  color: red;
  font-size: 12px;
  margin-top: 10px;
}
.foot {
  width: 100%;
}
.shop-select select {
  width: 240px;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid rgba(16, 24, 40, 0.06);
  background: #fff;
  font-size: 14px;
  outline: none;
}
.shop-select select:focus {
  box-shadow: 0 6px 18px rgba(255, 140, 35, 0.06);
  border-color: rgba(255, 140, 35, 0.2);
}
</style>
