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
            String filePath = imageService.uploadImageById(postId, img, "post");
            if (filePath != null) {
                String imageUrl = filePath.replaceFirst("^\\./?", "/").replace("\\", "/");
                post.setImageUrl(imageUrl);
                // 可选：把 image info 插入 image 表（如果你有 Image 表）
            }
        }

        // 返回包含 imageUrl 的 post 给前端
        post.setId(postId);
        return Result.success(post);
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
