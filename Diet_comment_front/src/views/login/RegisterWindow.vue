<template>
  <div class="page">
    <form class="window" @submit.prevent="handleRegister">
      <h1 class="title">用户注册</h1>
      <label class="label">
        <div class="text">👤用户名</div>
        <input type="username" v-model="username" placeholder="用户名" />
        <div class="text">🔒密码</div>
        <input type="password" v-model="password" placeholder="密码" />
        <div class="text">🔒确认密码</div>
        <input type="password" v-model="confirmPassword" placeholder="确认密码" />
        <div class="text">📪邮箱</div>
        <span class="email">
          <input type="email" v-model="email" placeholder="邮箱" />
          <button @click="codecheck" :disabled="!(time == 0)" type="button">
            {{ time == 0 ? '发送验证码' : time }}
          </button>
        </span>
        <div class="text">🤖邮箱验证码</div>
        <input type="emailcode" v-model="code" placeholder="邮箱验证码" />
        <span class="user"
          >👕用户类型 <input class="user" type="radio" v-model="role" name="sort" value="1" />商户
          <input class="user" type="radio" v-model="role" name="sort" value="2" />顾客
        </span>
      </label>
      <div class="register">
        <button class="register-btn" type="submit">注册</button>
        <button @click="$router.push('/login')" class="register-btn">返回</button>
      </div>
      <div>
        <p class="error" v-if="error">{{ error }}</p>
        <p class="success" v-if="success">{{ success }}</p>
      </div>
    </form>
  </div>
</template>
<script>
import { registerApi, sendCode } from '@/api/login'
export default {
  name: 'RegisterWindow',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      code: '',
      role: '',
      time: 0,
      timer: null,
      error: '',
      success: '',
    }
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer)
  },
  methods: {
    startCountdown() {
      if (this.timer) clearInterval(this.timer)
      this.timer = setInterval(() => {
        if (this.time > 0) {
          this.time--
        } else {
          clearInterval(this.timer)
          this.timer = null
        }
      }, 1000)
    },
    async codecheck() {
      if (!this.email) {
        this.error = '请输入邮箱'
        this.success = ''
        return
      }
      const response = await sendCode(this.email)
      if (response.code == 1) {
        this.success = '验证码已发送'
        this.error = ''
        this.time = 60
        this.startCountdown()
      } else {
        this.error = response.message
        this.success = ''
      }
    },
    async handleRegister() {
      // 注册逻辑
      if (
        !this.username ||
        !this.password ||
        !this.confirmPassword ||
        !this.email ||
        !this.role ||
        !this.code
      ) {
        this.success = ''
        this.error = '请填写所有信息'
        return false
      }
      if (this.password.length < 6) {
        this.success = ''
        this.error = '密码长度至少6位'
        return false
      }
      if (this.password !== this.confirmPassword) {
        this.error = '两次输入的密码不一致'
        this.success = ''
        return false
      }
      try {
        const response = await registerApi(
          this.username,
          this.password,
          this.email,
          this.role,
          this.code,
        )
        if (response.code === 1) {
          this.success = '注册成功，正在跳转到登录页面...'
          this.error = ''
          setTimeout(() => {
            this.$router.push('/login')
          }, 2000)
        } else {
          this.error = response.message
          this.success = ''
        }
      } catch (error) {
        this.success = ''
        this.error = error.message || '注册失败，请稍后重试'
      }
    },
  },
}
</script>
<style scoped>
.page {
  height: 100vh;
  width: 100vw;
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

* {
  box-sizing: border-box;
}

.window {
  width: 100%;
  max-width: 500px;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  align-items: center;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
}

.title {
  display: flex;
  flex-direction: column;
  text-align: center;
  font-size: 30px;
  margin-bottom: 10px;
}

.label {
  width: 100%;
  display: flex;
  flex-direction: column;
  font-size: 15px;
  padding: 10px;
}
.email {
  display: flex;
  flex-direction: row;
  gap: 5px;
}
.email button {
  width: 30%;
  background: linear-gradient(90deg, hwb(37 54% 0%), #ffffff);
  text-align: center;
  color: #000000;
  border-radius: 8px;
  cursor: pointer;
  border: 0px solid;
}
.text {
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-top: 10px;
  margin-bottom: 10px;
  gap: 15px;
}

.user {
  font-size: 15px;
  margin-top: 10px;
  margin-bottom: 10px;
}

input[type='username'],
input[type='password'],
input[type='email'],
input[type='emailcode'] {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e6edf3;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  color: #0f172a;
  display: flex;
  flex-direction: column;
}

.register {
  margin-bottom: 12px;
  margin-top: 6px;
  gap: 12px;
  width: 100%;
  display: flex;
}
.register-btn {
  flex: 1;
  background: linear-gradient(90deg, hwb(37 54% 0%), #ffffff);
  text-align: center;
  padding: 10px 12px;
  color: #000000;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 10px;
}

.error {
  color: crimson;
  font-size: 17px;
}

.success {
  color: chartreuse;
  font-size: 17px;
}
</style>
