package com.example.diet_comment.service.Impl;

import com.example.diet_comment.model.DTO.UserDTO;
import com.example.diet_comment.model.Post;
import com.example.diet_comment.model.Shop;
import com.example.diet_comment.service.CollectService;
import com.example.diet_comment.service.PostService;
import com.example.diet_comment.service.ShopService;
import com.example.diet_comment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ShopService shopService;

    private static final String POST_COLLECTED_BY_USER_KEY_PREFIX = "user:collect:post:"; // 用户收藏的帖子集合
    private static final String POST_COLLECTORS_KEY_PREFIX = "post:collectors:"; // 收藏某帖子的用户集合

    private static final String SHOP_COLLECTED_BY_USER_KEY_PREFIX = "user:collect:shop:"; // 用户收藏的店铺集合
    private static final String SHOP_COLLECTORS_KEY_PREFIX = "shop:collectors:"; // 收藏某店铺的用户集合

    // --- Post Collection ---

    @Override
    public void collectPost(Integer userId, Integer postId) {
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(POST_COLLECTED_BY_USER_KEY_PREFIX + userId, postId, score);
        redisTemplate.opsForZSet().add(POST_COLLECTORS_KEY_PREFIX + postId, userId, score);
    }

    @Override
    public void cancelCollectPost(Integer userId, Integer postId) {
        redisTemplate.opsForZSet().remove(POST_COLLECTED_BY_USER_KEY_PREFIX + userId, postId);
        redisTemplate.opsForZSet().remove(POST_COLLECTORS_KEY_PREFIX + postId, userId);
    }

    @Override
    public boolean isPostCollected(Integer userId, Integer postId) {
        return redisTemplate.opsForZSet().score(POST_COLLECTED_BY_USER_KEY_PREFIX + userId, postId) != null;
    }

    @Override
    public Long getPostCollectCount(Integer postId) {
        return redisTemplate.opsForZSet().zCard(POST_COLLECTORS_KEY_PREFIX + postId);
    }

    @Override
    public List<Post> getCollectedPostsByUserId(Integer userId) {
        // 1. 从 Redis 获取用户收藏的所有帖子 ID
        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(POST_COLLECTED_BY_USER_KEY_PREFIX + userId, 0, -1);

        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 将 Set<Object> 转换为 List<Integer>
        List<Integer> ids = postIds.stream()
                .map(id -> (Integer) id)
                .collect(Collectors.toList());

        // 3. 从数据库中根据 ID 批量查询 Post 对象
        List<Post> posts = postService.listByIds(ids);
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        // 4. (新增) 提取所有帖子的用户ID和店铺ID，用于后续批量查询
        List<Integer> postUserIds = posts.stream().map(Post::getUserId).distinct().collect(Collectors.toList());
        List<Integer> postShopIds = posts.stream().map(Post::getShopId).distinct().collect(Collectors.toList());

        // 5. (新增) 批量查询用户信息和店铺信息，并转换为Map以便快速查找
        Map<Integer, UserDTO> userDTOMap = userService.listByIds(postUserIds).stream()
                .map(user -> new UserDTO().fromUser(user))
                .collect(Collectors.toMap(UserDTO::getId, userDTO -> userDTO));

        Map<Integer, Shop> shopMap = shopService.listByIds(postShopIds).stream()
                .collect(Collectors.toMap(Shop::getId, shop -> shop));

        // 6. (新增) 遍历帖子列表，设置关联的 user 和 shop 对象
        posts.forEach(post -> {
            post.setUser(userDTOMap.get(post.getUserId()));
            post.setShop(shopMap.get(post.getShopId()));
        });

        // 7. 为了保证返回的列表顺序与收藏时间一致，进行排序
        Map<Integer, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));

        return ids.stream()
                .map(postMap::get)
                .filter(Objects::nonNull) // 过滤掉可能在数据库中已被删除的帖子
                .collect(Collectors.toList());
    }

    // --- Shop Collection ---

    @Override
    public void collectShop(Integer userId, Integer shopId) {
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(SHOP_COLLECTED_BY_USER_KEY_PREFIX + userId, shopId, score);
        redisTemplate.opsForZSet().add(SHOP_COLLECTORS_KEY_PREFIX + shopId, userId, score);
    }

    @Override
    public void cancelCollectShop(Integer userId, Integer shopId) {
        redisTemplate.opsForZSet().remove(SHOP_COLLECTED_BY_USER_KEY_PREFIX + userId, shopId);
        redisTemplate.opsForZSet().remove(SHOP_COLLECTORS_KEY_PREFIX + shopId, userId);
    }

    @Override
    public boolean isShopCollected(Integer userId, Integer shopId) {
        return redisTemplate.opsForZSet().score(SHOP_COLLECTED_BY_USER_KEY_PREFIX + userId, shopId) != null;
    }

    @Override
    public Long getShopCollectCount(Integer shopId) {
        return redisTemplate.opsForZSet().zCard(SHOP_COLLECTORS_KEY_PREFIX + shopId);
    }

    @Override
    public List<Shop> getCollectedShopsByUserId(Integer userId) {
        Set<Object> shopIds = redisTemplate.opsForZSet().reverseRange(SHOP_COLLECTED_BY_USER_KEY_PREFIX + userId, 0, -1);
        if (shopIds == null || shopIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> ids = shopIds.stream().map(id -> (Integer) id).collect(Collectors.toList());
        return shopService.listByIds(ids);
    }
}
