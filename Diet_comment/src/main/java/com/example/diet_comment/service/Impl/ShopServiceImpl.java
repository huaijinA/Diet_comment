package com.example.diet_comment.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.ShopMapper;
import com.example.diet_comment.mapper.ShopRatingMapper;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.model.ShopRating;
import com.example.diet_comment.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper,Shop> implements ShopService {

    @Autowired
    private ShopRatingMapper shopRatingMapper;

    @Override
    public Integer getShopIdByName(String shopName) {
        Object id = this.baseMapper.selectObjs(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<Shop>lambdaQuery()
                        .select(Shop::getId)
                        .eq(Shop::getName, shopName)
                        .last("limit 1")
        ).stream().findFirst().orElse(null);
        if (id == null) {
            System.out.println("LOG:null");
            return null;
        }
        else return Integer.parseInt(id.toString());
    }



    @Override
    public List<Shop> searchShopsByKeyword(String keyword) {
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        // 使用 or 连接 name 和 tags 的模糊查询
        queryWrapper.like("name", keyword).or().like("tags", keyword);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional // 建议添加事务，确保评分和店铺信息更新的原子性
    public void addShopRating(ShopRating shopRating) {


        // 插入新的评分记录
        // 1. 根据 userId 和 shopId 查询是否已存在评分记录
        QueryWrapper<ShopRating> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", shopRating.getUserId());
        queryWrapper.eq("shop_id", shopRating.getShopId());
        ShopRating existingRating = shopRatingMapper.selectOne(queryWrapper);

        if (existingRating != null) {
            // 2. 如果存在，则更新评分
            existingRating.setRating(shopRating.getRating());
            // MyBatis-Plus 会自动处理 updatedAt 字段（如果配置了）
            shopRatingMapper.updateById(existingRating);
        } else {
            // 3. 如果不存在，则插入新评分
            // createdAt 会自动填充（如果配置了）
            shopRatingMapper.insert(shopRating);
        }

    }

    public Shop getShopByShopId(Integer shopId){
        if(shopId==null||shopId<=0){
            return null;
        }
        return this.getById(shopId);
    }



}
