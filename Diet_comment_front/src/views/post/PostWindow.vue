<template>
  <div class="window">
    <div class="start">
      <span class="back">
        <button @click="goBack">返回</button>
      </span>
      <span class="choice" ref="choiceBtn">
        <button v-if="post.user.id === localUserInfo.id" @click="choiceFlag = !choiceFlag">
          选项
        </button>
        <div class="choicewindow" v-if="choiceFlag">
          <div class="eidt">
            <button @click="editPost">✒️修改</button>
          </div>
          <div class="border"></div>
          <div class="delete">
            <button @click="openDeleteWindow">🗑️删除</button>
          </div>
        </div>
      </span>
    </div>

    <!-- 帖子删除窗口 -->
    <div class="prewpage" v-if="openDeleteFlag" @click.self="closeWindow">
      <div class="deleteWindow">
        <div class="deletetext">确定要删除这条帖子吗？此操作无法撤销。</div>
        <div class="deletebutton">
          <button class="comfirm" @click="deletePost">确定</button>
          <button class="cancel" @click="closeWindow">取消</button>
        </div>
        <div class="error" v-if="error">{{ error }}</div>
        <div class="success" v-if="success">{{ success }}</div>
      </div>
    </div>

    <!-- 评论删除窗口 -->
    <div class="prewpage" v-if="openDeleteCommentFlag" @click.self="closeWindow">
      <div class="deleteWindow">
        <div class="deletetext">确定要删除这条评论吗？此操作无法撤销。</div>
        <div class="deletebutton">
          <button class="comfirm" @click="deleteComment">确定</button>
          <button class="cancel" @click="closeWindow">取消</button>
        </div>
        <div class="error" v-if="error">{{ error }}</div>
        <div class="success" v-if="success">{{ success }}</div>
      </div>
    </div>

    <!-- 评论窗口 -->
    <div class="replyWinodw" v-if="replyPostFlag">
      <input
        type="file"
        ref="fileInput"
        @change="onFileChange"
        accept="image/*"
        multiple
        style="display: none"
      />
      <div class="replyContent">
        <div class="closeBtn">
          <span></span>
          <button @click="closeReplyWindow">×</button>
        </div>
        <div class="replyhead">
          <span class="replyText">
            <input v-model="replyContent" placeholder="随便聊聊～" type="text" />
          </span>
          <span>
            <button class="preimage" @click="chooseImg">🖼️</button>
            <button class="submit" @click="PostComments(replyContent, post.id, null)">发布</button>
          </span>
        </div>
        <div class="replyImg">
          <span v-for="(image, index) in replyPreviewImages" :key="image" class="previewImage">
            <button class="removeBtn" @click="removePreview(index)">×</button>
            <img :src="image" @click="openPreview(replyPreviewImages, index)" :alt="评论图片" />
          </span>
          <span class="error">{{ error }}</span>
          <span class="success">{{ success }}</span>
        </div>
      </div>
    </div>

    <!-- 回复窗口 -->
    <div class="replyWinodw" v-if="replyCommentFlag">
      <input
        type="file"
        ref="fileInput"
        @change="onFileChange"
        accept="image/*"
        multiple
        style="display: none"
      />
      <div class="replyContent">
        <div class="closeBtn">
          <span class="replyUser">回复{{ replyUser.username }}:</span>
          <button @click="closeReplyWindow">×</button>
        </div>
        <div class="replyhead">
          <span class="replyText">
            <input v-model="replyContent" placeholder="随便聊聊～" type="text" />
          </span>
          <span>
            <button class="preimage" @click="chooseImg">🖼️</button>
            <button class="submit" @click="ReplyComments(replyContent, post.id, commentId)">
              发布
            </button>
          </span>
        </div>
        <div class="replyImg">
          <span v-for="(image, index) in replyPreviewImages" :key="image" class="previewImage">
            <button class="removeBtn" @click="removePreview(index)">×</button>
            <img :src="image" @click="openPreview(replyPreviewImages, index)" :alt="评论图片" />
          </span>
          <span class="error">{{ error }}</span>
          <span class="success">{{ success }}</span>
        </div>
      </div>
    </div>

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

    <!-- 帖子 -->
    <div class="post">
      <div class="posthead">
        <span class="user">
          <img :src="post.user.avatarUrl" @click="goUser(post.user)" :alt="帖子图片" />
          <span class="username">{{ post.user.userName }}</span>
        </span>
        <span>
          <span class="time">{{ post.createdAt }}</span>
        </span>
      </div>
      <div class="title">{{ post.title }}</div>
      <div class="content">{{ post.content }}</div>
      <div class="image">
        <span v-for="(image, index) in post.imgurls" :key="image">
          <img :src="image" @click="openPreview(post.imgurls, index)" :alt="帖子图片" />
        </span>
      </div>
      <div class="foot">
        <span class="shop" v-if="JSON.stringify(post.shop) != '{}'">
          <div class="shopname" @click="goShop(post.shop)">🏠{{ post.shop.name }}</div>
          <div class="shopaddress">地址：{{ post.shop.address }}</div>
        </span>
        <span v-else></span>
        <span class="fav">
          <span class="postReply">
            <button @click="openPostReply">💬</button>
          </span>
          <button @click="toggleFav">{{ fav ? '★' : '☆' }}</button>
          <span class="favnum">{{ favNum.num }}</span>
        </span>
      </div>
    </div>

    <!-- 评论 -->
    <ul class="comments">
      <div class="commenthead">评论({{ this.total }})</div>
      <li class="comment" v-for="comment in comments" :key="comment.id">
        <div class="commenttop">
          <button
            class="commentDelete"
            v-if="comment.user.id === localUserInfo.id || post.user.id === localUserInfo.id"
            @click="openDeleteCommentWindow(comment.id)"
          >
            删除
          </button>
        </div>
        <div class="posthead">
          <span class="user">
            <img :src="comment.user.avatarUrl" @click="goUser(comment.user)" :alt="评论用户头像" />
            <span class="username">{{ comment.user.userName }}</span>
          </span>
          <span>
            <span class="time">{{ comment.createdAt }}</span>
          </span>
        </div>
        <div class="commentcontent">{{ comment.content }}</div>
        <div class="image">
          <span v-for="(image, index) in comment.imgurls" :key="image">
            <img :src="image" @click="openPreview(comment.imgurls, index)" :alt="评论图片" />
          </span>
        </div>
        <div class="res">
          <button @click="openCommentReply(comment)">回复</button>
        </div>
        <div class="res">
          <span class="restext" @click="childcomment(comment)">{{
            comment.isExpanded == false ? '展开回复' : '收起回复'
          }}</span>
        </div>
        <div>
          <transition name="slide-fade">
            <ul class="childcomments" v-if="comment.isExpanded == true">
              <li v-if="comment.child.length == 0">
                <span class="no">暂无回复</span>
              </li>
              <li
                class="childcomment"
                v-for="childcomment in comment.child"
                v-else
                :key="childcomment.id"
              >
                <div class="commenttop">
                  <button
                    class="commentDelete"
                    v-if="
                      childcomment.user.id === localUserInfo.id || post.user.id === localUserInfo.id
                    "
                    @click="openDeleteCommentWindow(childcomment.id)"
                  >
                    删除
                  </button>
                </div>
                <div class="childposthead">
                  <span class="childuser">
                    <img
                      :src="childcomment.user.avatarUrl"
                      @click="goUser(childcomment.user)"
                      :alt="子评论用户头像"
                    />
                    <span class="childusername">{{ childcomment.user.userName }}</span>
                  </span>
                  <span>
                    <span class="childtime">{{ childcomment.createdAt }}</span>
                  </span>
                </div>
                <div class="childcommentcontent">{{ childcomment.content }}</div>
                <div class="image">
                  <span v-for="(image, index) in childcomment.imgurls" :key="image">
                    <img
                      :src="image"
                      @click="openPreview(childcomment.imgurls, index)"
                      :alt="子评论图片"
                    />
                  </span>
                </div>
              </li>
            </ul>
          </transition>
        </div>
      </li>
      <div class="pager">
        <button @click="prevPage" :disabled="page === 1">上一页</button>
        <span>
          第
          <input v-model="changepage" @keyup.enter="changePage" />
          /{{ totalPages }}页
        </span>
        <button @click="nextPage" :disabled="page >= totalPages">下一页</button>
      </div>
    </ul>
  </div>
</template>
<script>
import {
  getCommentsByPost,
  getCommentsByParent,
  deleteComments,
  postComments,
  replyComments,
} from '@/api/comment'
import { getUserInfoById } from '@/api/user'
import { getImage, uploadImage } from '@/api/image'
import {
  deletePosts,
  postStatus,
  collectPosts,
  uncollectPosts,
  getPostCollectNum,
} from '@/api/post'
export default {
  data() {
    return {
      fav: 0,
      post: JSON.parse(localStorage.getItem('onePost')),
      showModal: false,
      currentImageUrls: [],
      currentImageUrl: '',
      currentIndex: null,
      currentLength: 0,
      choiceFlag: false,
      openDeleteFlag: false,
      openDeleteCommentFlag: false,
      replyPostFlag: false,
      replyCommentFlag: false,
      deleteId: null,
      replyContent: '',
      replyPreviewImages: [],
      replyUser: null,
      ImagesFiles: [],
      commentId: null,
      error: '',
      success: '',
      favNum: 0,
      userInfo: {
        id: null,
        userName: '',
        avatarUrl: '',
        email: '',
        role: null,
      },
      localUserInfo: JSON.parse(localStorage.getItem('userInfo')),
      comments: [],
      changepage: 1,
      page: 1,
      total: null,
      totalPages: null,
      size: 10,
    }
  },
  created() {
    this.GetComments()
    postStatus(this.post.id, this.localUserInfo.id).then((response) => {
      if (response.code == 1) {
        const fav = response.data.collected
        if (fav == 1) this.fav = true
        else this.fav = false
      }
    })
    getPostCollectNum(this.post.id).then((response) => {
      if (response.code == 1) {
        this.favNum = response.data
      }
    })
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    //收起函数
    handleClickOutside(e) {
      if (!this.$refs.choiceBtn.contains(e.target)) {
        this.choiceFlag = false
      }
    },
    //收藏函数
    toggleFav() {
      if (this.fav) {
        uncollectPosts(this.post.id, this.localUserInfo.id)
        this.favNum.num = this.favNum.num > 0 ? this.favNum.num - 1 : 0
      } else {
        collectPosts(this.post.id, this.localUserInfo.id)
        this.favNum.num = this.favNum.num + 1
      }
      this.fav = !this.fav
    },
    //回退
    goBack() {
      this.$router.back()
    },
    //前往用户主页
    goUser(user) {
      this.userInfo.id = user.id
      this.userInfo.userName = user.userName
      this.userInfo.avatarUrl = user.avatarUrl
      this.userInfo.email = user.email
      this.userInfo.role = user.role
      this.$store.dispatch('getOneUser', this.userInfo)
      this.$router.push({ path: '/UserWindow' })
    },
    //前往店铺主页
    goShop(shop) {
      this.$store.dispatch('getOneShop', shop)
      this.$router.push({ path: '/ShopWindow' })
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
    //获取评论
    async GetComments() {
      try {
        const response = await getCommentsByPost(this.page, this.size, this.post.id)
        if (response.code !== 1) {
          this.$message.error('获取父评论失败')
          return
        }
        this.page = response.data.current
        this.totalPages = response.data.pages
        this.total = response.data.total
        this.size = response.data.size
        this.comments = response.data.records.map((comment) => ({
          ...comment,
          user: {},
          child: [],
          imgurls: [],
          isExpanded: false,
        }))
        const parentUserIds = this.comments.map((c) => c.userId)
        const parentUserRes = await Promise.all(parentUserIds.map((id) => getUserInfoById(id)))
        parentUserRes.forEach((res) => {
          if (res.code === 1) {
            const targetComments = this.comments.filter((comment) => comment.userId === res.data.id)
            targetComments.forEach((comment) => {
              comment.user = res.data
            })
          }
        })
        const commentIds = this.comments.map((c) => c.id)
        const imgurlRes = await Promise.all(
          commentIds.map((id) => getImage('comment', id).then((res) => ({ id, res }))),
        )
        imgurlRes.forEach(({ id, res }) => {
          if (res.code === 1) {
            const comment = this.comments.find((c) => c.id === id)
            if (comment) comment.imgurls = res.data
          }
        })
      } catch (error) {
        console.error('获取评论失败:', error)
      }
    },
    //展开子评论
    childcomment(comment) {
      comment.isExpanded = !comment.isExpanded
      if (comment.isExpanded) {
        this.GetChildComments(comment)
      }
    },
    //获取子评论
    async GetChildComments(comment) {
      try {
        const response = await getCommentsByParent(comment.postId, comment.id)
        if (response.code === 1) {
          comment.child = response.data.replies.map((comment) => ({
            ...comment,
            // user: {},
            imgurls: [],
          }))
          // const childUserIds = comment.child.map((c) => c.userId)
          // const childUserRes = await Promise.all(childUserIds.map((id) => getUserInfoById(id)))
          // childUserRes.forEach((res) => {
          //   if (res.code === 1) {
          //     const targetComments2 = comment.child.filter((c) => c.userId === res.data.id)
          //     targetComments2.forEach((comment) => {
          //       comment.user = res.data
          //     })
          //   }
          // })
          const commentIds = comment.child.map((c) => c.id)
          const imgurlRes = await Promise.all(
            commentIds.map((id) => getImage('comment', id).then((res) => ({ id, res }))),
          )
          imgurlRes.forEach(({ id, res }) => {
            if (res.code === 1) {
              const comment2 = comment.child.find((c) => c.id === id)
              if (comment2) comment2.imgurls = res.data
            }
          })
        }
      } catch (error) {
        console.error('获取评论失败:', error)
      }
    },
    //换页函数
    changePage() {
      if (this.changepage <= this.totalPages && this.changepage > 0) {
        this.page = this.changepage
        this.GetComments()
        window.scrollTo({ top: 0, behavior: 'smooth' })
      } else {
        this.changepage = this.page
      }
    },
    //上一页
    prevPage() {
      if (this.page > 1) {
        this.page--
        this.GetComments()
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
      this.changepage = this.page
    },
    //下一页
    nextPage() {
      if (this.page < this.totalPages) {
        this.page++
        this.GetComments()
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
      this.changepage = this.page
    },
    //修改帖子
    editPost() {
      this.$router.push({ path: '/EditPostWindow' })
    },
    //打开帖子删除窗口
    openDeleteWindow() {
      this.deleteId = this.post.id
      this.openDeleteFlag = true
      this.choiceFlag = false
    },
    //打开评论删除窗口
    openDeleteCommentWindow(commentId) {
      this.deleteId = commentId
      this.openDeleteCommentFlag = true
    },
    //删除帖子
    async deletePost() {
      try {
        const response = await deletePosts(this.post.id)
        if (response.code == 1) {
          this.error = ''
          this.success = '删除帖子成功'
          setTimeout(() => {
            this.$router.push('/')
          }, 1000)
        } else {
          this.error = '删除帖子失败'
          this.success = ''
        }
      } catch (e) {
        console.log(e)
      }
    },
    //关闭删除窗口
    closeWindow() {
      this.openDeleteFlag = false
      this.openDeleteCommentFlag = false
      this.deleteId = null
      this.error = ''
      this.success = ''
    },
    //删除评论
    async deleteComment() {
      try {
        const response = await deleteComments(this.deleteId)
        if (response.code == 1) {
          this.error = ''
          this.success = '删除评论成功'
          await new Promise((r) => setTimeout(r, 800))
          this.success = ''
          this.openDeleteCommentFlag = false
          this.$router.go(0)
        } else {
          this.error = '删除评论失败'
          this.success = ''
        }
      } catch (e) {
        console.log(e)
      }
    },
    //打开帖子评论窗口
    openPostReply() {
      this.replyPostFlag = true
    },
    //打开评论评论窗口
    openCommentReply(post) {
      this.commentId = post.id
      this.replyUser = post.user
      this.replyCommentFlag = true
    },
    //关闭评论窗口
    closeReplyWindow() {
      this.replyPostFlag = false
      this.replyCommentFlag = false
      this.replyContent = ''
      this.replyPreviewImages = []
      this.ImagesFiles = []
      this.commentId = null
      this.replyUser = null
      this.error = ''
      this.success = ''
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
    //发布帖子评论
    async PostComments(content, postId, parentCommentId) {
      try {
        if (content.trim() === '') {
          this.error = '评论内容不能为空'
          return
        }
        const response = await postComments(content, postId, parentCommentId)
        if (response.code == 1) {
          const commentId = response.data
          if (this.ImagesFiles.length > 0) {
            const imgResponse = await uploadImage(commentId, 'comment', this.ImagesFiles)
            if (imgResponse.code != 1) {
              this.error = '照片上传失败'
              this.success = ''
              return
            }
          }
          this.error = ''
          this.success = '发布评论成功'
          await new Promise((r) => setTimeout(r, 800))
          this.success = ''
          this.replyContent = ''
          this.replyPostFlag = false
          this.$router.go(0)
        } else {
          this.error = '发布评论失败'
          this.success = ''
        }
      } catch (e) {
        console.log(e)
      }
    },
    //发布评论回复
    async ReplyComments(content, postId, commentId) {
      try {
        if (content.trim() === '') {
          this.error = '评论内容不能为空'
          return
        }
        const response = await replyComments(content, postId, commentId)
        if (response.code == 1) {
          const id = response.data
          if (this.ImagesFiles.length > 0) {
            const imgResponse = await uploadImage(id, 'comment', this.ImagesFiles)
            if (imgResponse.code != 1) {
              this.error = '照片上传失败'
              this.success = ''
              return
            }
          }
          this.error = ''
          this.success = '回复评论成功'
          await new Promise((r) => setTimeout(r, 800))
          this.success = ''
          this.$router.go(0)
        } else {
          this.error = '回复评论失败'
          this.success = ''
        }
      } catch (e) {
        console.log(e)
      }
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
.window {
  width: 100%;
  min-height: 985px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: linear-gradient(135deg, rgba(255, 205, 148, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
}
.start {
  max-width: 820px;
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.back {
  margin-top: 10px;
  margin-left: 10px;
  display: flex;
  flex-direction: column;
}
.back button {
  padding: 5px;
  width: 70px;
  height: 30px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 5px;
  border-width: 1px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  cursor: pointer;
}
.choice {
  margin-top: 10px;
  margin-right: 10px;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}
.choice button {
  padding: 5px;
  width: 70px;
  height: 30px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 5px;
  border-width: 1px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  cursor: pointer;
}
.choicewindow {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 80px;
  background: #fff;
  border-radius: 8px;
  padding: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 998;
  transform-origin: top right;
  transition:
    opacity 0.16s ease,
    transform 0.16s ease;
  opacity: 1;
}
.choicewindow button {
  padding: 5px;
  width: 100%;
  height: 30px;
  border: none;
  border-radius: 5px;
  border-width: 1px;
  background: transparent;
  cursor: pointer;
}
.border {
  height: 1px;
  background-color: #e0e0e0;
}
.post {
  background: white;
  width: 800px;
  margin: 15px 15px 15px 15px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  border-radius: 10px;
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
  cursor: pointer;
}
.image {
  gap: 20px;
  margin-left: 40px;
}
.image img {
  width: 150px;
  height: 180px;
  margin: 5px 5px;
  object-fit: cover;
  border-radius: 5px;
}
.username {
  font-size: 15px;
}
.title {
  display: flex;
  justify-content: center;
  margin: 10px 0px 20px 0px;
  font-size: 25px;
  font-weight: 500;
}
.time {
  color: #b3b3b3;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  margin-right: 10px;
  font-family: monospace;
  width: 10ch;
  white-space: nowrap;
  overflow: hidden;
  text-align: right;
}
.content {
  font-size: 17px;
  color: rgb(99, 99, 98);
  padding: 5px;
  margin-left: 40px;
}
.foot {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}
.shopname {
  font-size: 14px;
  color: #4e4e4e;
  cursor: pointer;
  text-underline-position: below;
  text-decoration: underline;
  text-decoration-thickness: 1.5px;
}
.shopaddress {
  font-size: 10px;
  color: #b3b3b3;
}
.fav {
  display: flex;
  align-items: center;
}
.fav button {
  border: none;
  width: fit-content;
  color: orange;
  font-size: 25px;
  background: none;
}
.favnum {
  margin-left: 5px;
  font-size: 15px;
  color: #9a9a9a;
}
.postReply {
  margin-right: 10px;
}
.postReply button {
  font-size: 25px;
  color: #000000b6;
  background: none;
  cursor: pointer;
  padding: 3px;
}
/* 评论 */
.comments {
  display: flex;
  flex-direction: column;
  width: 800px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  list-style: none;
  border-radius: 10px;
  background: white;
}
.commenttop {
  display: flex;
  justify-content: flex-end;
}
.commentDelete {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  background-color: #ff4d4f;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}
.commenthead {
  margin: 15px 10px 10px 25px;
  font-size: 15px;
}
.comment {
  display: block;
  border-radius: 10px;
  margin: 20px 30px 20px 30px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
}
.commentcontent {
  font-size: 15px;
  padding: 5px;
  margin-left: 40px;
  color: black;
}
.end {
  text-align: center;
  font-size: 15px;
  color: #9a9a9a;
  margin-bottom: 20px;
}
.res {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
.restext {
  color: #9a9a9a;
  font-size: 12px;
  cursor: pointer;
}
.res button {
  color: #000000b6;
  background: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  border: 1px solid #ccc;
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
  z-index: 999;
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
  color: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
  cursor: not-allowed;
}
/* Hover效果:增强交互 */
.pre-btn:not(:disabled):hover,
.next-btn:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
}
/* 子评论 */
.childcomments {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  list-style: none;
  border-radius: 10px;
  background: transparent;
}
.childcomment {
  display: block;
  width: 600px;
  border-top: 1px solid #9a9a9a;
  margin: 5px 30px 0px 30px;
  padding: 20px;
}
.childposthead {
  flex: 1;
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 5px;
}
.childuser {
  display: flex;
  gap: 10px;
  font-size: 15px;
  align-items: center;
}
.childuser img {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
}
.childusername {
  font-size: 13px;
}
.childtime {
  height: 100%;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #b3b3b3;
  margin-right: 10px;
  font-family: monospace;
  width: 10ch;
  white-space: nowrap;
  overflow: hidden;
  text-align: right;
}
.childcommentcontent {
  font-size: 13px;
  padding: 5px;
  color: black;
  width: 500px;
  margin-left: 35px;
}
.no {
  font-size: 13px;
  color: #b3b3b3;
}
/* 过渡画面 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  max-height: 0;
  margin-top: 0;
}
.slide-fade-enter-to,
.slide-fade-leave-from {
  opacity: 1;
  max-height: 500px;
}
/* 页码 */
.pager {
  margin: 14px 0px 10px 15px;
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 14px;
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
  cursor: pointer;
  border: 1px solid;
  border-radius: 5px;
  background: linear-gradient(
    135deg,
    rgba(141, 255, 253, 0.461) 0%,
    rgba(255, 255, 255, 0.08) 100%
  );
}
/*删除窗口*/
.prewpage {
  width: 100%;
  height: 100%;
  position: fixed;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.deleteWindow {
  width: 320px;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}
.deletetext {
  font-size: 14px;
  color: #333;
  margin-bottom: 20px;
}
.deletebutton {
  display: flex;
  justify-content: center;
  gap: 12px;
}
.comfirm {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: #ff4d4f;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}
.cancel {
  padding: 8px 16px;
  border: 1px solid #eee;
  border-radius: 4px;
  background-color: #fff;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
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
/* 评论帖子 */
.replyWinodw {
  width: 100%;
  height: 100%;
  position: fixed;
  background-color: rgba(0, 0, 0, 0.5);
  bottom: 0px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  z-index: 800;
}
.replyContent {
  width: 800px;
  height: auto;
  background: #efefef;
  box-shadow: 0 8px 30px rgba(17, 24, 39, 0.06);
  border-radius: 12px;
}
.replyhead {
  width: 800px;
  height: auto;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.replyText {
  flex: 1;
  padding: 8px;
  border: 1px solid rgba(16, 24, 40, 0.06);
  border-radius: 8px;
  background: #f8fafc;
}
.replyText input {
  width: 100%;
  height: 36px;
  font-size: 15px;
  border: none;
  outline: none;
  background: transparent;
  padding: 0 6px;
}
.preimage {
  padding: 8px 16px;
  border: none;
  font-size: 20px;
  cursor: pointer;
}
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
.submit {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: linear-gradient(
    135deg,
    rgba(141, 255, 253, 0.461) 0%,
    rgba(255, 255, 255, 0.08) 100%
  );
  cursor: pointer;
}
.closeBtn {
  display: flex;
  justify-content: space-between;
}
.closeBtn button {
  margin: 2px;
  background: rgba(0, 0, 0, 0.55);
  border: none;
  color: #fff;
  font-size: 14px;
  border-radius: 30px;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  cursor: pointer;
}
.replyUser {
  margin-left: 20px;
  margin-top: 10px;
  color: #9a9a9a;
}
</style>
