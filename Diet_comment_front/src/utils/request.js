import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // 基础URL
  timeout: 30000,
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    console.log('请求拦截器中的token:', token)
    if (token) {
      config.headers['token'] = token;
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data

    if (res.code !== 1) {
      if (res.message === "没有找到相关帖子") {
        return res
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  (error) => {
    // 处理HTTP错误状态码
    let message = ''
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '身份验证失败，请重新登录'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `连接错误 ${error.response.status}`
      }
    } else if (error.request) {
      message = '无响应，服务器未启动或网络异常'
    } else {
      message = error.message
    }

    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export default service
