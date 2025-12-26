package com.example.diet_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.model.ShopRating;

import java.util.List;

public interface ShopService extends IService<Shop> {
    Integer getShopIdByName(String shopName);

    List<Shop> searchShopsByKeyword(String keyword);

    void addShopRating(ShopRating shopRating);

    Shop getShopByShopId(Integer shopId);

    /**
     * 根据用户和店铺获取评分
     */
    Double getUserRating(Integer userId, Integer shopId);

    /**
     * 删除用户对某个店铺的评分
     */
    void deleteUserRating(Integer userId, Integer shopId);
}
