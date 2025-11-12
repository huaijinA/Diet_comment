package com.example.diet_comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.diet_comment.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Select("SELECT " +
            "p.id AS post_id, p.title AS post_title, p.content AS post_content, p.user_id AS post_user_id, p.shop_id AS post_shop_id, p.created_at AS post_created_at, p.updated_at AS post_updated_at, " +
            "u.id AS user_id, u.username AS user_name, u.email AS user_email, u.avatar_url AS user_avatar_url, " +
            "s.id AS shop_id, s.name AS shop_name, s.address AS shop_address, s.rating AS shop_rating, s.reviews AS shop_reviews, s.price AS shop_price, s.imgurl AS shop_imgurl, s.tags AS shop_tags, s.created_at AS shop_created_at, s.updated_at AS shop_updated_at " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN shops s ON p.shop_id = s.id " +
            "WHERE " +
            "p.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.content LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR s.name LIKE CONCAT('%', #{keyword}, '%')")
    @ResultMap("PostWithUserAndShopMap") // 引用在XML中定义的ResultMap
    List<Post> searchWithUserAndShop(@Param("keyword") String keyword);
}
