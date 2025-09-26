package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("dishes")
public class Dish {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("shop_id")
    private Integer shopId;

	@TableField("name")
    private String name;

	@TableField("description")
    private String description;

	@TableField("price")
    private BigDecimal price;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // Getters and Setters
}
