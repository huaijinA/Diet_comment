package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("shop_rating")
public class ShopRating {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("shop_id")
    private Integer shopId;

    @TableField("rating")
    private Integer rating;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
