package com.example.diet_comment.controller;

import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.service.CollectService;
import com.example.diet_comment.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CollectService collectService;

    // 你可以把店铺的其他接口也放在这里，比如获取店铺详情等

    @PostMapping("/shop/collect")
    public Result collectShop(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("user_id");
        Integer shopId = payload.get("shop_id");
        if (userId == null || shopId == null) {
            return Result.error("user_id 和 shop_id 不能为空");
        }
        collectService.collectShop(userId, shopId);
        return Result.success("收藏成功");
    }

    @DeleteMapping("/shop/cancelcollect")
    public Result cancelCollectShop(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("user_id");
        Integer shopId = payload.get("shop_id");
        if (userId == null || shopId == null) {
            return Result.error("user_id 和 shop_id 不能为空");
        }
        collectService.cancelCollectShop(userId, shopId);
        return Result.success("取消收藏成功");
    }

    @PostMapping("/shop/collectStatus")
    public Result getShopCollectStatus(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("user_id");
        Integer shopId = payload.get("shop_id");
        if (userId == null || shopId == null) {
            return Result.error("user_id 和 shop_id 不能为空");
        }
        boolean isCollected = collectService.isShopCollected(userId, shopId);
        Map<String, Object> data = Map.of(
                "id", shopId,
                "collected", isCollected ? 1 : 0
        );
        return Result.success(data);
    }

    @GetMapping("/shop/collectedShops/{user_id}")
    public Result getCollectedShops(@PathVariable("user_id") Integer userId) {
        List<Shop> shops = collectService.getCollectedShopsByUserId(userId);
        return Result.success(shops);
    }

    @GetMapping("/shop/collectNum/{shop_id}")
    public Result getShopCollectNum(@PathVariable("shop_id") Integer shopId) {
        Long count = collectService.getShopCollectCount(shopId);
        Map<String, Long> data = Map.of("num", count);
        return Result.success(data);
    }
}
