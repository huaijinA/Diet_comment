package com.example.diet_comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.model.Comment;

public interface CommentService {
    Page<Comment> getCommentsByPostId(Page<Comment> page, Integer postId);
    void deleteCommentsByPostId(Integer postId);
}
