package com.example.diet_comment.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.mapper.CommentMapper;
import com.example.diet_comment.model.Comment;
import com.example.diet_comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Page<Comment> getCommentsByPostId(Page<Comment> page, Integer postId) {
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.eq("post_id", postId).orderByDesc("created_at");
        return commentMapper.selectPage(page, qw);
    }
}