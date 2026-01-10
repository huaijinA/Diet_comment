package com.example.diet_comment.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.mapper.CommentMapper;
import com.example.diet_comment.model.Comment;
import com.example.diet_comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Page<Comment> getCommentsByPostId(Page<Comment> page, Integer postId) {
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.eq("post_id", postId).isNull("parent_comment_id").orderByDesc("created_at");
        return commentMapper.selectPage(page, qw);
    }

    @Override
    public void deleteCommentsByPostId(Integer postId) {

        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.eq("post_id", postId);
        List<Comment> initial = commentMapper.selectList(qw);
        if (initial == null || initial.isEmpty()) {
            return;
        }

        Set<Integer> toDeleteSet = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        for (Comment c : initial) {
            if (c != null && c.getId() != null) {
                toDeleteSet.add(c.getId());

                queue.add(c.getId());
            }
        }

        while (!queue.isEmpty()) {
            Integer parentId = queue.poll();
            QueryWrapper<Comment> childQ = new QueryWrapper<>();
            childQ.eq("parent_comment_id", parentId);
            List<Comment> children = commentMapper.selectList(childQ);
            if (children == null || children.isEmpty()) {
                continue;
            }
            for (Comment ch : children) {
                if (ch != null && ch.getId() != null && toDeleteSet.add(ch.getId())) {
                    queue.add(ch.getId());
                }
            }
        }

        if (!toDeleteSet.isEmpty()) {
            LambdaQueryWrapper<Comment> deleteQ = new LambdaQueryWrapper<>();
            deleteQ.in(Comment::getId, toDeleteSet);
            commentMapper.delete(deleteQ);
        }
    }
}
