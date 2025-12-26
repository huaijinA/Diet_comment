package com.example.diet_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * 根据店铺ID获取菜品列表
     * 
     * @param shopId 店铺ID
     * @return 菜品列表
     */
    List<Dish> getDishesByShopId(Integer shopId);
}
