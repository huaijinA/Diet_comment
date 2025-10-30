import request from '@/utils/request'

//接口函数

//更新用户信息

export function changeUserInfo(userName, email, avatarImage) {
  const formData = new FormData()
  formData.append('userName', userName)
  formData.append('email', email)
  if (avatarImage) {
    formData.append('avatarImage', avatarImage)
  }
  return request({
    url: '/userpage',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data', //设置请求头
    },
  })
}

//修改密码
export function changePassword(oldpwd, newpwd){
  return request({
    url: '/userpage/pwdput',
    method: 'post',
    data:{
      oldpwd,
      newpwd,
    }
  })
}
//注释注释注释注释注释注释
