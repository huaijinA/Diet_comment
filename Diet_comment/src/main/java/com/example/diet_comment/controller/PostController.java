package com.example.diet_comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.diet_comment.model.DTO.UserDTO;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.mapper.PostMapper;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.service.PostService;
import com.example.diet_comment.service.ShopService;
import com.example.diet_comment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

//import org.h2.engine.Comment;？
import com.example.diet_comment.model.Comment;
import com.example.diet_comment.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.example.diet_comment.service.ImageService;
import java.util.List;



@RestController
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ShopService shopService;
    
    @Autowired
    private CommentService commentService;

    @GetMapping("/homepage")
    public List<Post> getHomepage() {
        // TODO: 实现更复杂的主页逻辑，例如推荐算法
        // 这里简单返回最新的10个帖子
        Page<Post> page = new Page<>(1, 10);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<Post>().orderByDesc("created_at");
        return postMapper.selectPage(page, queryWrapper).getRecords();
    }

    @PostMapping(value = "/post", consumes = "multipart/form-data")
    public Result createPost(HttpServletRequest request,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam String shopName,
                             @RequestPart(value = "img", required = false) MultipartFile img) {

        Integer userId = (Integer) request.getAttribute("userId");

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(userId);
        Integer shopId = shopService.getShopIdByName(shopName);
        System.out.println("LOG"+shopId);
        post.setShopId(shopId);

        Integer postId = postService.addPost(post);

        if (img != null && !img.isEmpty()) {
            String url = imageService.uploadImageById(postId, img, "post");

        }
        return Result.success();
    }

    @GetMapping("/post/{id}")
    public Result getPostById(@PathVariable Integer id) {

        Post post =  postService.getPostById(id);

        if(post.getUserId()!=null){
            UserDTO user = userService.getUserDTOById(post.getUserId());
            post.setUser(user);

        }


        if(post.getShopId()!=null){
            Shop shop = shopService.getShopByShopId(post.getShopId());
            post.setShop(shop);
        }

        return Result.success(post);
    }



    @GetMapping("post")
    public Result getPostsByUserOrShopId(@RequestParam(required = false) Integer userId,
                                          @RequestParam(required = false) Integer shopId) {
        if (userId == null && shopId == null) {
            return Result.error("必须提供 userId 或 shopId 之一");
        }

        List<Post> posts;
        if (userId != null) {
            posts = postService.getPostsByUserId(userId);
        } else{
            posts = postService.getPostsByShopId(shopId);
        }

        if ( posts.isEmpty() ){
            return Result.error("没有找到相关帖子");
        }

        return Result.success(posts);
    }


    @PutMapping(value = "/post/{id}", consumes = "multipart/form-data")
    public Result updatePost(HttpServletRequest request,
                             @PathVariable Integer id,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String content,
                             @RequestParam(required = false) String shopName,
                             @RequestPart(value = "img", required = false) MultipartFile img) {


        Integer userId = (Integer) request.getAttribute("userId");
        Post existingPost = postService.getPostById(id);
        if (existingPost == null) {
            return Result.error("帖子不存在");
        }
        if (!existingPost.getUserId().equals(userId)) {
            return Result.error("无权限修改该帖子");
        }


        if (img != null && !img.isEmpty()) {
            try {

                imageService.deleteByTypeAndId("post", id);

                String newImageUrl = imageService.uploadImageById(id, img, "post");
                if (newImageUrl == null) {

                    return Result.error("图片更新失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("处理图片时发生错误: " + e.getMessage());
            }
        }


        boolean isUpdated = false;
        if (title != null && !title.equals(existingPost.getTitle())) {
            existingPost.setTitle(title);
            isUpdated = true;
        }
        if (content != null && !content.equals(existingPost.getContent())) {
            existingPost.setContent(content);
            isUpdated = true;
        }
        if (shopName != null) {
            Integer shopId = shopService.getShopIdByName(shopName);
            if (shopId != null && !shopId.equals(existingPost.getShopId())) {
                existingPost.setShopId(shopId);
                isUpdated = true;
            }
        }


        if (isUpdated) {
            postService.updateById(existingPost);
        }

        return Result.success(postService.getPostById(id));
    }


    @DeleteMapping("/post/{id}")
    public Result deletePost(HttpServletRequest request,@PathVariable Integer id) {
        Integer userID= (Integer) request.getAttribute("userId");
        Post existingPost = postMapper.selectById(id);
        if (existingPost == null || !existingPost.getUserId().equals(userID)) {
            return Result.error("无权限修改该帖子或帖子不存在");
        }
        try {
            postService.deletePostsById(id);
            return Result.success();
        } catch (IllegalStateException e) {
            return Result.error("删除帖子失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败，已回滚");
        }


    }


    @PostMapping("/post/search")
    public Result searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchPostsByKeyword(keyword);
        if(posts.isEmpty()){
            return Result.error("没有找到相关帖子");
        }
        return Result.success(posts);
    }
    


}
