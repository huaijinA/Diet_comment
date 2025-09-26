package com.example.diet_comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @GetMapping("/homepage")
    public List<Post> getHomepage() {
        // TODO: 实现更复杂的主页逻辑，例如推荐算法
        // 这里简单返回最新的10个帖子
        Page<Post> page = new Page<>(1, 10);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<Post>().orderByDesc("created_at");
        return postMapper.selectPage(page, queryWrapper).getRecords();
    }

    @PostMapping("/post")
    public Post createPost(@RequestBody Post post) {
        // TODO: 从安全上下文中获取用户ID并设置
        // post.setUserId(currentUserId);
        postMapper.insert(post);
        return post;
    }

    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable Integer id) {
        // TODO: 实现帖子详情，可能需要关联查询作者、店铺等信息
        return postMapper.selectById(id);
    }

    @PutMapping("/post/{id}")
    public Post updatePost(@PathVariable Integer id, @RequestBody Post post) {
        post.setId(id);
        // TODO: 校验当前用户是否有权限修改该帖子
        postMapper.updateById(post);
        return post;
    }

    @DeleteMapping("/post/{id}")
    public String deletePost(@PathVariable Integer id) {
        // TODO: 校验当前用户是否有权限删除该帖子
        postMapper.deleteById(id);
        return "Post with ID " + id + " deleted.";
    }
}
