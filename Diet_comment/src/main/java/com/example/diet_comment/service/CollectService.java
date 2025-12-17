package com.example.diet_comment.service;

import com.example.diet_comment.model.Post;
import com.example.diet_comment.model.Shop;

import java.util.List;
import java.util.Map;

public interface CollectService {

    // --- Post Collection ---
    void collectPost(Integer userId, Integer postId);
    void cancelCollectPost(Integer userId, Integer postId);
    boolean isPostCollected(Integer userId, Integer postId);
    Long getPostCollectCount(Integer postId);
    List<Post> getCollectedPostsByUserId(Integer userId);

    // --- Shop Collection ---
    void collectShop(Integer userId, Integer shopId);
    void cancelCollectShop(Integer userId, Integer shopId);
    boolean isShopCollected(Integer userId, Integer shopId);
    Long getShopCollectCount(Integer shopId);
    List<Shop> getCollectedShopsByUserId(Integer userId);
}
