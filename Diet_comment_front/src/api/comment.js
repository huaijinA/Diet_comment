import request from '@/utils/request'
// 获取父评论
export function getCommentsByPost(page, size, post_id) {
  return request({
    url: `/post/${post_id}/comment`,
    method: 'get',
    params: {
      page: page,
      size: size,
    },
  })
}
// 获取子评论
export function getCommentsByParent(postId, commentId) {
  return request({
    url: `/post/${postId}/comment/${commentId}`,
    method: 'get',
    params: {
      postId: postId,
      commentId: commentId,
    },
  })
}
//发布评论
export function postComments(content, postId, parentCommentId) {
  return request({
    url: `/post/${postId}/comment`,
    method: 'post',
    data: {
      content: content,
      postId: postId,
      parentCommentId: parentCommentId,
    },
  })
}
//回复评论
export function replyComments(content, postId, commentId) {
  return request({
    url: `/post/${postId}/comment/${commentId}`,
    method: 'post',
    data: {
      content: content,
      postId: postId,
      commentId: commentId,
    },
  })
}
//删除评论
export function deleteComments(commentId) {
  return request({
    url: `/post/comment/${commentId}`,
    method: 'delete',
    data: {
      commentId: commentId,
    },
  })
}
