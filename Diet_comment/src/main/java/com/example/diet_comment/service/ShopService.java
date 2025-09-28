package com.example.diet_comment.service;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Shop;

public interface ShopService extends IService<Shop> {
    Integer getShopIdByName(String shopName);
}
