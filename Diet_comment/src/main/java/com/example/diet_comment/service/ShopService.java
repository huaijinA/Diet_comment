package com.example.diet_comment.service;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.model.ShopRating;

import java.util.List;

public interface ShopService extends IService<Shop> {
    Integer getShopIdByName(String shopName);
    List<Shop> searchShopsByKeyword(String keyword);
    void addShopRating(ShopRating shopRating);
    Shop getShopByShopId(Integer shopId);
}
