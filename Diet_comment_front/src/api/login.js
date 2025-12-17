import request from '@/utils/request'

//接口函数

//登录
export function loginApi(userName, password) {
  return request({
    url: '/login', //后端接口
    method: 'post',
    data: {
      userName,
      password,
    },
  })
}

//获取用户信息
export function getUserInfo() {
  return request({
    url: '/userpage', //后端接口
    method: 'get',
  })
}

//发送验证码（暂未实现后端）
export function sendCode(email) {
  return request({
    url: '/register/sendcode', //后端接口
    method: 'post',
    data: {
      email,
    },
  })
}

//注册
export function registerApi(userName, password, email, role, code) {
  return request({
    url: '/register', //后端接口
    method: 'post',
    data: {
      userName,
      password,
      email,
      role,
      code,
    },
  })
}

//找回密码（暂未实现后端）
export function findPasswordApi(userName, password, email, code) {
  return request({
    url: '/find', //后端接口
    method: 'post',
    data: {
      userName,
      password,
      email,
      code,
    },
  })
}
