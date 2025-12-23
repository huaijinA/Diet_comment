import request from '@/utils/request'

//接口函数

//更新用户信息

export function changeUserInfo(userName, email, avatarImage) {
  const formData = new FormData()
  formData.append('userName', userName)
  formData.append('email', email)
  if (avatarImage) {
    formData.append('image', avatarImage)
  }
  return request({
    url: '/userpage',
    method: 'put',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data', //设置请求头
    },
  })
}

//修改密码
export function changePassword(oldpwd, newpwd) {
  return request({
    url: '/userpage/pwdput',
    method: 'post',
    data: {
      oldpwd,
      newpwd,
    },
  })
}

//获取用户信息
export function getUserInfoById(user_id) {
  return request({
    url: '/user/' + user_id,
    method: 'get',
  })
}
