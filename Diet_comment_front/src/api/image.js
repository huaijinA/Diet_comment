import request from '@/utils/request'
// 上传图片
export function uploadImage(id, type, Images) {
  const formData = new FormData()
  formData.append('id', id)
  formData.append('code', type)
  if (Images && Images.length > 0) {
    Array.from(Images).forEach((file) => {
      formData.append('files', file)
    })
  }
  return request({
    url: '/upload',
    method: 'post',
    data: formData,
  })
}

// 获取图片
export function getImage(imageableType, imageableId) {
  return request({
    url: `/imageMap`,
    method: 'get',
    params: {
      imageableType: imageableType,
      imageableId: imageableId,
    },
  })
}