package com.example.diet_comment.controller;

import com.example.diet_comment.model.Dish;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.model.ShopRating;
import com.example.diet_comment.service.CollectService;
import com.example.diet_comment.service.DishService;
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

    @Autowired
    private DishService dishService;

    // 你可以把店铺的其他接口也放在这里，比如获取店铺详情等

    /**
     * 获取所有店铺列表
     * 
     * @return 店铺列表
     */
    @GetMapping("/shop")
    public Result getAllShops() {
        List<Shop> shops = shopService.list();
        return Result.success(shops);
    }

    @GetMapping("/shop/{id}")
    public Result getShopById(@PathVariable("id") Integer id) {
        Shop shop = shopService.getById(id);
        if (shop != null) {
            return Result.success(shop);
        } else {
            return Result.error("店铺不存在");
        }
    }

    /**
     * 创建新店铺
     * 
     * @param shop 店铺信息
     * @return 创建结果
     */
    @PostMapping("/shop")
    public Result createShop(@RequestBody Shop shop) {
        boolean success = shopService.save(shop);
        if (success) {
            return Result.success(shop);
        } else {
            return Result.error("创建店铺失败");
        }
    }

    /**
     * 根据关键词搜索店铺
     * 
     * @param keyword 关键词，可以匹配店铺名称或标签
     * @return 匹配的店铺列表
     */
    @GetMapping("/shop/search")
    public Result searchShops(@RequestParam("keyword") String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("关键词不能为空");
        }
        List<Shop> shops = shopService.searchShopsByKeyword(keyword);
        return Result.success(shops);
    }

    /**
     * 为店铺评分
     * 
     * @param shopRating 包含 userId, shopId, rating 的评分信息
     * @return 评分结果
     */
    @PostMapping("/shop/rating")
    public Result rateShop(@RequestBody ShopRating shopRating) {
        if (shopRating.getUserId() == null || shopRating.getShopId() == null || shopRating.getRating() == null) {
            return Result.error("用户ID、店铺ID和评分不能为空");
        }

        // 可以在Service层增加逻辑，比如更新店铺的平均分
        if (shopRating.getRating() < 0 || shopRating.getRating() > 5) {
            return Result.error("评分必须在0到5之间");
        }
        shopService.addShopRating(shopRating);
        return Result.success("评分成功");
    }

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
                "collected", isCollected ? 1 : 0);
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

    /**
     * 根据店铺ID获取菜品列表
     * 
     * @param shop_id 店铺ID
     * @return 菜品列表
     */
    @GetMapping("/dishes/{shop_id}")
    public Result getDishes(@PathVariable("shop_id") Integer shop_id) {
        if (shop_id == null || shop_id <= 0) {
            return Result.error("店铺ID无效");
        }
        List<Dish> dishes = dishService.getDishesByShopId(shop_id);
        return Result.success(dishes);
    }
}
