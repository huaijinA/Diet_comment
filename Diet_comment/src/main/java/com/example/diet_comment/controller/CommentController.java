package com.example.diet_comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.model.Comment;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.mapper.CommentMapper;
import com.example.diet_comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

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
        /**
     * 获取帖子评论（分页）
     * URL: /post/{id}/comment
     * 方法: GET
     * 请求参数: page (int, 可选, 默认1), size (int, 可选, 默认10)
     * 响应: Result 包含评论列表（data 为 List<Comment>）
     */
    @GetMapping("/post/{id}/comment")
    public Result getPostComments(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        // 分页查询评论
        Page<Comment> pg = new Page<>(page, size);
        Page<Comment> commentPage = commentService.getCommentsByPostId(pg, id);
        List<Comment> comments = commentPage.getRecords();


        return Result.success(comments);
    }
}
