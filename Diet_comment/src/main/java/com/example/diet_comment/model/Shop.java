// package com.example.diet_comment.model;

// import com.baomidou.mybatisplus.annotation.*;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// @TableName("shops")
// public class Shop {

//     @TableId(type = IdType.AUTO)
//     private Integer id;

//     @TableField("name")
//     private String name;

//     @TableField("address")
//     private String address;

//     @TableField("description")
//     private String description;

//     @TableField("average_rating")
//     private BigDecimal averageRating;

//     @TableField(value = "created_at", fill = FieldFill.INSERT)
//     private LocalDateTime createdAt;

//     @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
//     private LocalDateTime updatedAt;

//     @TableField("tags")
//     private String tags;

//     // 以下字段不存在于数据库表中，用于关联查询时接收数据
//     @TableField(exist = false)
//     private List<Dish> dishes;

//     @TableField(exist = false)
//     private List<Post> posts;

//     // Getters and Setters

//     public Integer getId() {
//         return id;
//     }

//     public void setId(Integer id) {
//         this.id = id;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getAddress() {
//         return address;
//     }

//     public void setAddress(String address) {
//         this.address = address;
//     }

//     public void setTags(String tags) {
//         this.tags = tags;
//     }

//     public String getTags() {
//         return tags;
//     }
// }

package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 确保表名是正确的复数形式
@TableName("shops")
public class Shop {

    // 主键 ID
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 商店名称
    @TableField("name")
    private String name;

    // 地址
    @TableField("address")
    private String address;

    // 评分 (数据库为 FLOAT 或 DECIMAL，Java 用 BigDecimal 或 Double)
    // 推荐使用 BigDecimal 以保持浮点数精度
    @TableField("rating")
    private BigDecimal rating;

    // 评论数量 (数据库为 INT)
    @TableField("reviews")
    private Integer reviews;

    // 价格 (数据库为 FLOAT 或 DECIMAL)
    @TableField("price")
    private BigDecimal price;

    // 图片 URL
    @TableField("imgurl")
    private String imgurl;

    // 标签 (数据库为 JSON 或 VARCHAR)
    @TableField("tags")
    private String tags;

    // 创建时间 (数据库 TIMESTAMP)
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新时间 (数据库 TIMESTAMP)
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /*
     * * 关联字段：这些字段在数据库中不存在，用于接收关联查询结果
     */
    @TableField(exist = false)
    private List<Dish> dishes;

    @TableField(exist = false)
    private List<Post> posts;

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviews() {
        return reviews;
    }

    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}