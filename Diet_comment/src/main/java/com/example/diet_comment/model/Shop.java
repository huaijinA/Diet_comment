package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@TableName("shops")
public class Shop {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("address")
    private String address;

	@TableField("description")
    private String description;

    @TableField("average_rating")
    private BigDecimal averageRating;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 以下字段不存在于数据库表中，用于关联查询时接收数据
    @TableField(exist = false)
    private List<Dish> dishes;

    @TableField(exist = false)
    private List<Post> posts;

    // Getters and Setters
}
