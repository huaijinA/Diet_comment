import request from '@/utils/request'
// 获取店铺帖子
export function getShopPosts(shop_id) {
  return request({
    url: '/post',
    method: 'get',
    params: {
      shopId: shop_id,
    },
  })
}

// 根据用户获取帖子
export function getUserPosts(user_id) {
  return request({
    url: '/post',
    method: 'get',
    params: {
      userId: user_id,
    },
  })
}

//获取帖子页面(主页)
export function getMainPosts() {
  return request({
    url: '/homepage',
    method: 'get',
  })
}

//帖子搜索
export function searchPosts(Keyword) {
  return request({
    url: '/post/search',
    method: 'post',
    data: {
      Keyword,
    },
  })
}

// 新建帖子
export function newPosts(title, content, shopName, Images) {
  const formData = new FormData()
  formData.append('title', title)
  formData.append('content', content)
  formData.append('shopName', shopName)
  if (Images && Images.length > 0) {
    Array.from(Images).forEach((file) => {
      formData.append('img', file)
    })
  }
  return request({
    url: '/post',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data', //设置请求头
    },
  })
}

//帖子删除
export function deletePosts(post_id) {
  return request({
    url: '/post/' + post_id,
    method: 'delete',
    data: {
      id: post_id,
    },
  })
}

//帖子更新
export function updatePosts(post_id, title, content, shopName, Images) {
  const formData = new FormData()
  formData.append('id', post_id)
  formData.append('title', title)
  formData.append('content', content)
  formData.append('shopName', shopName)
  if (Images && Images.length > 0) {
    Array.from(Images).forEach((file) => {
      formData.append('img', file)
    })
  }
  return request({
    url: '/post/' + post_id,
    method: 'put',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data', //设置请求头
    },
  })
}

//帖子收藏
export function collectPosts(post_id, user_id) {
  return request({
    url: '/post/collect',
    method: 'post',
    data: {
      post_id: post_id,
      user_id: user_id,
    },
  })
}

//帖子取消收藏
export function uncollectPosts(post_id, user_id) {
  return request({
    url: '/post/cancelcollect',
    method: 'delete',
    data: {
      post_id: post_id,
      user_id: user_id,
    },
  })
}

//获取已收藏帖子
export function collectedPosts(user_id) {
  return request({
    url: '/post/collectedPosts/' + user_id,
    method: 'get',
  })
}

//收藏帖子查询
export function postStatus(post_id, user_id) {
  return request({
    url: '/post/collectStatus',
    method: 'post',
    data: {
      post_id: post_id,
      user_id: user_id,
    },
  })
}

//获取收藏数
export function getPostCollectNum(post_id) {
  return request({
    url: '/post/collectNum/' + post_id,
    method: 'get',
  })
}
