import request from '@/utils/request'
//获取店铺信息
export function getShopInfo() {
  return request({
    url: '/shopInfo',
    method: 'get',
  })
}
