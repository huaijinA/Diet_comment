package com.example.diet_comment.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.PostMapper;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.service.PostService;
import org.springframework.stereotype.Service;


@Service
public class PostServiceImpl extends ServiceImpl<PostMapper,Post> implements PostService {
    @Override
    public Integer addPost(Post post){
        if("".equals(post.getTitle())){
            return -1;
        }




        this.save(post);

        return post.getId();
    }


    @Override
    public Post getPostById(int id){

        return this.getById(id);
    }

}
