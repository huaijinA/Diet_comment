package com.example.diet_comment.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.PostMapper;
import com.example.diet_comment.mapper.ShopMapper;
import com.example.diet_comment.mapper.UserMapper;
import com.example.diet_comment.model.DTO.UserDTO;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.model.User;
import com.example.diet_comment.service.PostService;
import com.example.diet_comment.service.ShopService;
import com.example.diet_comment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.SQLOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper,Post> implements PostService {
    @Autowired
    private final UserService userService;
    @Autowired
    private final ShopService shopService;
    @Autowired
    private final PostMapper postMapper;

    @Autowired
    private CommentServiceImpl commentServiceImpl;
    @Autowired
    private ImageServiceImpl imageServiceImpl;

    public PostServiceImpl(UserService userService, ShopService shopService, PostMapper postMapper, ShopMapper imageService, UserMapper commentService) {
        this.userService = userService;
        this.shopService = shopService;
        this.postMapper = postMapper;

    }


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

        Post post =  this.getById(id);
        if (id <= 0||post == null) {
            return null;
        }

        return post;

    }

    @Override
    public List<Post> searchPostsByKeyword(String keyword1) {
        System.out.println("搜索关键字: " + keyword1);
        String keyword;
        if (!StringUtils.hasText(keyword1)) {
            System.out.println("关键字为空");
            return Collections.emptyList(); // 关键字为空则返回空列表

        }else{
            keyword = keyword1.trim();
        }



        // 1. 根据关键字搜索用户，获取匹配的用户ID列表
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.like(User::getUserName, keyword);
        List<Integer> userIds = userService.list(userQuery).stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // 2. 根据关键字搜索店铺（名称、地址、标签），获取匹配的店铺ID列表
        LambdaQueryWrapper<Shop> shopQuery = new LambdaQueryWrapper<>();
        shopQuery.like(Shop::getName, keyword)
                .or().like(Shop::getAddress, keyword)
                .or().like(Shop::getTags, keyword);
        List<Integer> shopIds = shopService.list(shopQuery).stream()
                .map(Shop::getId)
                .collect(Collectors.toList());

        // 3. 构建最终的帖子查询条件
        LambdaQueryWrapper<Post> postQuery = new LambdaQueryWrapper<>();
        postQuery.and(qw -> {
            // 匹配帖子标题或内容
            qw.like(Post::getTitle, keyword)
                    .or()
                    .like(Post::getContent, keyword);

            // 如果找到了匹配的用户，则加入用户ID的查询条件
            if (!CollectionUtils.isEmpty(userIds)) {
                qw.or().in(Post::getUserId, userIds);
            }

            // 如果找到了匹配的店铺，则加入店铺ID的查询条件
            if (!CollectionUtils.isEmpty(shopIds)) {
                qw.or().in(Post::getShopId, shopIds);
            }
        });
        List<Post> posts = this.list(postQuery);

        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        List<Integer> postUserIds = posts.stream().map(Post::getUserId).distinct().collect(Collectors.toList());
        List<Integer> postShopIds = posts.stream().map(Post::getShopId).distinct().collect(Collectors.toList());

        // 一次性查询所有相关的用户和店铺信息
        Map<Integer, UserDTO> userDTOMap = userService.listByIds(postUserIds).stream()
                .map(user -> new UserDTO().fromUser(user))
                .collect(Collectors.toMap(UserDTO::getId, userDTO -> userDTO));

        Map<Integer, Shop> shopMap = shopService.listByIds(postShopIds).stream()
                .collect(Collectors.toMap(Shop::getId, shop -> shop));

        // 遍历帖子列表，设置 user 和 shop
        posts.forEach(post -> {
            post.setUser(userDTOMap.get(post.getUserId()));
            post.setShop(shopMap.get(post.getShopId()));
        });


        return posts;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePostsById(Integer id) {
        Post existingPost = postMapper.selectById(id);


        // 删除评论
       commentServiceImpl.deleteCommentsByPostId(id);

        // 删除图片
        imageServiceImpl.deleteByTypeAndId("post", id);


        // 删除帖子本身
        postMapper.deleteById(id);
    }




}


