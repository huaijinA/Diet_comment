package com.example.diet_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Post;

import java.util.List;

public interface PostService extends IService<Post> {
    Integer addPost(Post post);
    Post getPostById(int id);

    List<Post> searchPostsByKeyword(String keyword);
}
