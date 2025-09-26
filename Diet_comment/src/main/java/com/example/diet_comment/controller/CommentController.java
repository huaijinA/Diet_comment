package com.example.diet_comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.model.Comment;
import com.example.diet_comment.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @GetMapping("/post/{postId}/comment")
    public List<Comment> getComments(@PathVariable Integer postId,
                                     @RequestParam(defaultValue = "1") long pageNum,
                                     @RequestParam(defaultValue = "10") long pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>()
                .eq("post_id", postId)
                .isNull("parent_comment_id") // 只获取主评论
                .orderByDesc("created_at");
        return commentMapper.selectPage(page, queryWrapper).getRecords();
    }

    @PostMapping("/post/{postId}/comment")
    public Comment createComment(@PathVariable Integer postId, @RequestBody Comment comment) {
        comment.setPostId(postId);
        // TODO: 从安全上下文中获取用户ID并设置
        // comment.setUserId(currentUserId);
        commentMapper.insert(comment);
        return comment;
    }

    @GetMapping("/post/{postId}/comment/{commentId}")
    public Comment getCommentDetail(@PathVariable Integer postId, @PathVariable Integer commentId) {
        // TODO: 实现评论详情，可能需要关联查询作者、回复等信息
        return commentMapper.selectById(commentId);
    }

    @PostMapping("/post/{postId}/comment/{commentId}")
    public Comment replyToComment(@PathVariable Integer postId, @PathVariable Integer commentId, @RequestBody Comment reply) {
        reply.setPostId(postId);
        reply.setParentCommentId(commentId); // 设置回复的父评论ID
        // TODO: 从安全上下文中获取用户ID并设置
        // reply.setUserId(currentUserId);
        commentMapper.insert(reply);
        return reply;
    }
}
