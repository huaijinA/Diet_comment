package com.example.diet_comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.model.Comment;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.mapper.CommentMapper;
import com.example.diet_comment.service.CommentService;
import com.example.diet_comment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

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

    java.util.Map<String, Object> resp = new java.util.HashMap<>();
    resp.put("records", comments);
    resp.put("total", commentPage.getTotal());
    resp.put("pages", commentPage.getPages());
    resp.put("current", commentPage.getCurrent());
    resp.put("size", commentPage.getSize());

    return Result.success(resp);
    }
    // @GetMapping("/post/{postId}/comment")
    // public List<Comment> getComments(@PathVariable Integer postId,
    //                                  @RequestParam(defaultValue = "1") long pageNum,
    //                                  @RequestParam(defaultValue = "10") long pageSize) {
    //     Page<Comment> page = new Page<>(pageNum, pageSize);
    //     QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>()
    //             .eq("post_id", postId)
    //             .isNull("parent_comment_id") // 只获取主评论
    //             .orderByDesc("created_at");
    //     return commentMapper.selectPage(page, queryWrapper).getRecords();
    // }

    @PostMapping("/post/{postId}/comment")
    public Result createComment(HttpServletRequest request, 
                              @PathVariable Integer postId, 
                              @RequestBody Comment comment) {
        // 从请求属性中获取当前用户ID（LoginCheck拦截器已设置）
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        // 设置评论属性
        comment.setPostId(postId);
        comment.setUserId(userId);


        // 插入评论
        commentMapper.insert(comment);

        // 查询并返回完整评论（包含用户信息）
        Comment created = commentMapper.selectById(comment.getId());
        if (created != null && created.getUserId() != null) {
            created.setUser(userService.getUserDTOById(created.getUserId()));
        }
        return Result.success(created);
    }

    @GetMapping("/post/{postId}/comment/{commentId}")
    public Result getCommentDetail(@PathVariable Integer postId, 
                                 @PathVariable Integer commentId) {
        // 获取评论详情
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 验证评论是否属于该帖子
        if (!postId.equals(comment.getPostId())) {
            return Result.error("评论不属于该帖子");
        }

        // 关联查询作者信息
        if (comment.getUserId() != null) {
            comment.setUser(userService.getUserDTOById(comment.getUserId()));
        }

        // 如果是主评论，查询其直接回复
        if (comment.getParentCommentId() == null) {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>()
                .eq("parent_comment_id", commentId)
                .orderByDesc("created_at");
            List<Comment> replies = commentMapper.selectList(queryWrapper);
            
            // 为回复填充用户信息
            for (Comment reply : replies) {
                if (reply.getUserId() != null) {
                    reply.setUser(userService.getUserDTOById(reply.getUserId()));
                }
            }
            comment.setReplies(replies);
        }

        return Result.success(comment);
    }

    @PostMapping("/post/{postId}/comment/{commentId}")
    public Result replyToComment(HttpServletRequest request,
                               @PathVariable Integer postId, 
                               @PathVariable Integer commentId, 
                               @RequestBody Comment reply) {
        // 从请求属性中获取当前用户ID
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        // 检查父评论是否存在
        Comment parentComment = commentMapper.selectById(commentId);
        if (parentComment == null) {
            return Result.error("要回复的评论不存在");
        }
        
        // 检查父评论是否属于该帖子
        if (!postId.equals(parentComment.getPostId())) {
            return Result.error("评论不属于该帖子");
        }

        // 设置回复属性
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setParentCommentId(commentId);
        reply.setCreatedAt(java.time.LocalDateTime.now());

        // 插入回复
        commentMapper.insert(reply);

        // 查询并返回完整回复（包含用户信息）
        Comment created = commentMapper.selectById(reply.getId());
        if (created != null && created.getUserId() != null) {
            created.setUser(userService.getUserDTOById(created.getUserId()));
        }
        return Result.success(created);
    }

}
