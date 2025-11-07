<template>
  <div class="page">
    <div class="window">
      <img :src="editAvatar || userInfo.avatarUrl" class="avatar" @click="changeAvatar" />
      <div class="avatartext">点击头像更换</div>
      <div class="info">
        <input
          type="file"
          ref="fileInput"
          @change="onFileChange"
          accept="image/*"
          style="display: none"
        />
        <div class="header">
          <span class="text">编号：</span>
          <span class="text">{{ userInfo.id }}</span>
        </div>
        <div class="header">
          <span class="text">角色：</span>
          <span class="text">{{ Role }}</span>
        </div>
        <div class="header">
          <span class="text">用户名：</span>
          <input v-model="new_userInfo.new_userName" />
        </div>
        <div class="header">
          <span class="text">邮箱：</span>
          <input v-model="new_userInfo.new_email" />
        </div>
        <div class="foot">
          <button @click="saveEdit">保存</button>
          <button @click="cancelEdit">取消</button>
        </div>
        <div class="success" v-if="success">{{ success }}</div>
        <div class="error" v-if="error">{{ error }}</div>
      </div>
    </div>
  </div>
</template>
<script>
import { changeUserInfo } from '@/api/user'
import { getUserInfo } from '@/api/login'
export default {
  name: 'UserWindow',
  data() {
    return {
      editAvatar: '',
      success: '',
      error: '',
      userInfo: {
        id: null,
        userName: '',
        email: '',
        avatarUrl: '',
        role: '',
      },
      new_userInfo: {
        new_userName: '',
        new_email: '',
        new_avatar: null,
      },
    }
  },
  created() {
    this.loadUserInfo()
  },
  computed: {
    Role() {
      switch (this.userInfo.role) {
        case 1:
          return '商户'
        case 2:
          return '顾客'
        case 3:
          return '管理员'
        default:
          return '未知角色'
      }
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
        this.new_userInfo.new_userName = userinfo.userName
        this.new_userInfo.new_email = userinfo.email
      }
    },
    changeAvatar() {
      this.$refs.fileInput.click()
    },
    cancelEdit() {
      this.$router.push({ path: '/UserWindow' })
    },
    async saveEdit() {
      try {
        const response = await changeUserInfo(
          this.new_userInfo.new_userName,
          this.new_userInfo.new_email,
          this.new_userInfo.new_avatar,
        )
        if (response.code === 1) {
          const response2 = await getUserInfo()
          if (response2.code === 1) {
            this.$store.dispatch('clearUserInfo')
            this.$store.dispatch('getUserInfo', response2.data)
            this.success = '更新成功'
            this.error = ''
            setTimeout(() => this.$router.push('/UserWindow'), 1000)
          } else {
            this.error = '获取用户信息失败'
          }
        } else {
          this.error = response.message
        }
      } catch (err) {
        this.error = '请求失败：' + err.message
      }
    },
    onFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (e) => {
          this.editAvatar = e.target.result
          this.new_userInfo.new_avatar = file
        }
        reader.readAsDataURL(file)
      }
    },
  },
}
</script>
<style scoped>
.page {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.window {
  cursor: default;
  width: 60%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  min-height: 500px;
  align-items: center;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
}
.avatar {
  width: 80px;
  height: 80px;
  margin-top: 20px;
  margin-bottom: 5px;
  object-fit: cover;
  display: block;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  justify-content: center;
}
.avatartext {
  font-size: x-small;
  color: cadetblue;
}
.info {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: none;
  gap: 12px;
  width: 80%;
  height: 60%;
  max-width: 420px;
  margin-left: 20px;
  margin-right: 20px;
  padding: 5%;
}
.info input {
  flex: 1;
  box-sizing: border-box;
  background: transparent;
  border-radius: 5px;
  height: 100%;
  box-sizing: border-box;
  height: 30px;
  font-size: 15px;
  border: 1px solid rgba(0, 0, 0, 0.12);
}
.text {
  font-size: 17px;
}
.header {
  background: linear-gradient(135deg, rgba(216, 255, 208, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  display: flex;
  flex-direction: row;
  justify-content: start;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  padding: 5%;
  margin-top: 5px;
  margin-bottom: 5px;
}
.foot {
  display: flex;
  border-radius: 15px;
  gap: 50px;
}
.foot button {
  width: 40%;
  border-radius: 10px;
  border-width: 1px;
  margin-top: 20px;
  margin-top: 20px;
  cursor: pointer;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  size: 50px;
  font-size: 20px;
}
.success {
  color: chartreuse;
  font-size: 15px;
  text-align: center;
}
.error {
  color: crimson;
  font-size: 15px;
  text-align: center;
}
</style>
