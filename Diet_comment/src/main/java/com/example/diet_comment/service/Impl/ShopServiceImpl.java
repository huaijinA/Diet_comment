package com.example.diet_comment.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.ShopMapper;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.service.ShopService;
import org.springframework.stereotype.Service;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper,Shop> implements ShopService {

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


    public Shop getShopByShopId(Integer shopId){
        if(shopId==null||shopId<=0){
            return null;
        }
        return this.getById(shopId);
    }



}
