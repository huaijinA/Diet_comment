<template>
  <div class="page">
    <div class="window">
      <header class="header">
        <div class="head">修改密码</div>
      </header>
      <main class="main">
        <div class="old">
          <div>原密码</div>
          <input type="password" v-model="oldPassword" placeholder="请输入原密码" />
        </div>
        <div class="new">
          <div>新密码</div>
          <input type="password" v-model="newPassword" placeholder="请输入新密码(不少于6位)" />
        </div>
      </main>
      <footer class="footer">
        <button @click="confirm" type="submit">确定</button>
        <button @click="cancel">取消</button>
      </footer>
      <div class="success">{{ success }}</div>
      <div class="error">{{ error }}</div>
    </div>
  </div>
</template>

<script>
import { changePassword } from '@/api/user'
export default {
  data() {
    return {
      error: '',
      success: '',
      oldPassword: '',
      newPassword: '',
    }
  },
  methods: {
    async confirm() {
      if (this.newPassword && this.oldPassword) {
        if (this.newPassword.length < 6) {
          this.error = '新密码不得少于六位'
          return
        }
        if (this.newPassword == this.oldPassword) {
          this.error = '新密码不能与旧密码相同'
          return
        }
        try {
          const response = await changePassword(this.oldPassword, this.newPassword)
          if (response.code == 1) {
            this.success = '更新成功，请重新登录'
            this.error = ''
            this.$store.dispatch('logout')
            this.$store.dispatch('clearShopInfo')
            setTimeout(() => this.$router.push('/login'), 1000)
          } else {
            this.error = response.message
            return
          }
        } catch (e) {
          this.error = e
        }
      } else {
        this.error = '请完整填写'
      }
    },
    cancel() {
      this.$router.back()
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
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.window {
  width: 450px;
  height: 500px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  gap: 10px;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
}
.header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 10px;
}
.head {
  font-size: 35px;
}
.main {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 20px 0px;
}
.old {
  font-size: 30px;
}
.old input {
  border-radius: 5px;
  font-size: 30px;
  border: 1px solid;
}
.new {
  font-size: 30px;
}
.new input {
  border-radius: 5px;
  font-size: 30px;
  border: 1px solid;
}
.footer {
  width: 100%;
  gap: 10px;
  display: flex;
  justify-content: center;
  padding: 20px;
}
.footer button {
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  cursor: pointer;
  width: 40%;
  border-radius: 5px;
  font-size: 30px;
  border: 1px solid;
  margin: 0px 10px;
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
