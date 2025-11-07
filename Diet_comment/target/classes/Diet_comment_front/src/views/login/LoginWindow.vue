<template>
  <div class="page">
    <form class="card" @submit.prevent="handleLogin" novalidate>
      <h1 class="title">小众美食</h1>

      <label class="text">
        <span class="label">👤用户名</span>
        <input
          v-model="username"
          type="username"
          placeholder="输入用户名"
          autocomplete="username"
          required
        />
      </label>

      <label class="text">
        <span class="label">🔒密码</span>
        <div class="password-wrap">
          <input
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="输入密码（至少6位）"
            autocomplete="current-password"
            required
          />
          <button
            type="button"
            class="toggle"
            @click="showPassword = !showPassword"
            aria-label="切换显示密码"
          >
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
      </label>
      <div class="options">
        <router-link class="sign" to="/register">注册</router-link>
        <router-link class="forgot" to="/forget">忘记密码？</router-link>
      </div>

      <button class="submit" type="submit">
        <span v-if="loading">登录中...</span>
        <span v-else>登录</span>
      </button>

      <p class="error" v-if="error">{{ error }}</p>
    </form>
  </div>
</template>

<script>
import { loginApi } from '@/api/login'
import { getUserInfo } from '@/api/login'
export default {
  data() {
    return {
      showPassword: false,
      loading: false,
      username: '',
      password: '',
      error: '',
      userInfo: {
        id: null,
        userName: '',
        email: '',
        avatarUrl: '',
        role: '',
      },
    }
  },
  methods: {
    async getUserInformation() {
      try {
        const response = await getUserInfo()
        if (response.code === 1) {
          this.userInfo.id = response.data.id
          this.userInfo.userName = response.data.userName
          this.userInfo.email = response.data.email
          this.userInfo.avatarUrl = response.data.avatarUrl
          this.userInfo.role = response.data.role

          this.$store.dispatch('getUserInfo', this.userInfo)
        } else {
          console.error('获取用户信息失败：', response.message)
        }
      } catch (error) {
        console.error('获取用户信息出错：', error)
      }
    },
    async handleLogin() {
      if (!this.username || !this.password) {
        this.error = '用户名或密码不能为空'
        return false
      }
      if (this.password.length < 6) {
        this.error = '密码长度至少6位'
        return false
      }
      this.loading = true
      try {
        const response = await loginApi(this.username, this.password)
        if (response.data.token !== '') {
          console.log('nihao1')
          this.error = ''
          this.$store.dispatch('login', response.data.token)
          this.getUserInformation()
          setTimeout(() => {
            this.$router.push('/')
          }, 1000)
        } else {
          console.log(response.data.token)
          this.error = response.messageConfig
        }
      } catch (error) {
        console.log(error)
        this.error = error.message || '登录失败，请稍后重试'
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<style scoped>
:root {
  --bg: #f8c16d;
  --card: #ffffff;
  --accent: #3b82f6;
  --muted: #6b7280;
  --danger: #ef4444;
  --success: #10b981;
  --orange-1: #ff6a00;
  --orange-2: #ff9a3d;
}

* {
  box-sizing: border-box;
}

.card {
  width: 100%;
  max-width: 420px;
  padding: 28px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: relative;
  overflow: hidden;
  background: var(--card);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.card::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 154, 38, 0.461) 0%, rgba(253, 219, 52, 0.08) 100%);
  pointer-events: none;
  mix-blend-mode: normal;
  z-index: 0;
  transition: opacity 0.25s ease;
}

.card > * {
  position: relative;
  z-index: 1;
}

.error {
  margin: 6px 0 0 0;
  font-size: 13px;
  color: var(--danger);
}

.label {
  font-size: 15px;
  color: var(--muted);
}

.forgot {
  color: var(--muted);
  text-underline-position: below;
  text-decoration: underline;
  text-decoration-thickness: 2px;
}

.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--muted);
  margin-top: 4px;
}

.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.password-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sign {
  color: #000000;
  text-underline-position: below;
  text-decoration: underline;
  text-decoration-thickness: 2px;
}

.submit {
  background: linear-gradient(90deg, hwb(37 54% 0%), #ffffff);
  text-align: center;
  margin-bottom: 12px;
  margin-top: 6px;
  padding: 10px 12px;
  color: #000000;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.title {
  margin: 0 0 4px 0;
  font-size: 30px;
  color: #000000;
  text-align: center;
}

.text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.toggle {
  padding: 3px 4px;
  font-size: 12px;
  border-radius: 8px;
  background: transparent;
  color: var(--accent);
  cursor: pointer;
}

input[type='username'],
input[type='password'],
input[type='text'] {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e6edf3;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  color: #0f172a;
}

.error {
  color: crimson;
}
</style>
