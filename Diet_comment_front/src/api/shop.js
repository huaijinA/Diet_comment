import request from '@/utils/request'
//获取店铺信息
export function getShopInfo() {
  return request({
    url: '/shopInfo',
    method: 'get',
  })
}

//获取店铺信息2
export function getShopInfoById(shop_id) {
  return request({
    url: '/shopInfo/' + shop_id,
    method: 'get',
  })
}

//获取菜单
export function getDishes(shop_id) {
  return request({
    url: '/dishes/' + shop_id,
    method: 'get',
  })
}

//获取评分
export function getRating(userId, shopId) {
  return request({
    url: '/shop/rating/post',
    method: 'post',
    data: {
      userId,
      shopId,
    },
  })
}

//打分
export function saveRating(userId, shopId, rating) {
  return request({
    url: '/shop/rating/save',
    method: 'post',
    data: {
      userId,
      shopId,
      rating,
    },
  })
}

//删除评分
export function deleteRating(userId, shopId) {
  return request({
    url: '/shop/rating/delete',
    method: 'delete',
    data: {
      userId,
      shopId,
    },
  })
}

//店铺收藏
export function collectShops(shop_id, user_id) {
  return request({
    url: '/shop/collect',
    method: 'post',
    data: {
      shop_id: shop_id,
      user_id: user_id,
    },
  })
}

//店铺取消收藏
export function uncollectShops(shop_id, user_id) {
  return request({
    url: '/shop/cancelcollect',
    method: 'delete',
    data: {
      shop_id: shop_id,
      user_id: user_id,
    },
  })
}

//获取已收藏店铺
export function collectedShops(user_id) {
  return request({
    url: '/shop/collectedShops/' + user_id,
    method: 'get',
  })
}

//收藏店铺查询
export function shopStatus(shop_id, user_id) {
  return request({
    url: '/shop/collectStatus',
    method: 'post',
    data: {
      shop_id: shop_id,
      user_id: user_id,
    },
  })
}

//获取收藏数
export function getShopCollectNum(shop_id) {
  return request({
    url: '/shop/collectNum/' + shop_id,
    method: 'get',
  })
}
