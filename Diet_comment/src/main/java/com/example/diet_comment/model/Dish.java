// package com.example.diet_comment.model;

// import com.baomidou.mybatisplus.annotation.*;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// @TableName("dishes")
// public class Dish {

//     @TableId(type = IdType.AUTO)
//     private Integer id;

//     @TableField("shop_id")
//     private Integer shopId;

// 	@TableField("name")
//     private String name;

// 	@TableField("description")
//     private String description;

// 	@TableField("price")
//     private BigDecimal price;

//     @TableField(value = "created_at", fill = FieldFill.INSERT)
//     private LocalDateTime createdAt;

//     @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
//     private LocalDateTime updatedAt;

//     // Getters and Setters
// }

package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("dishes")
public class Dish {

    // 主键 ID
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 外键：所属商店 ID
    @TableField("shop_id")
    private Integer shopId; // 假设 shop_id 是 INT 类型

    // 菜品名称
    @TableField("name")
    private String name;

    // 菜品价格
    @TableField("price")
    private BigDecimal price;

    // 菜品图片 URL (新增字段)
    @TableField("imgurl")
    private String imgurl;

    // 创建时间
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新时间
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /*
     * 已移除 'description' 字段，因为它不存在于您的数据库表中。
     */

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}