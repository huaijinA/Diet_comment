package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("images")
public class Image {

    @TableId(type = IdType.AUTO)
    private Integer id;


    private String url;

    @TableField("alt_text")
    private String altText;

    @TableField("imageable_id")
    private Integer imageableId;

    //包括post和shop两个类型
    @TableField("imageable_type")
    private String imageableType;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // Getters and Setters
}
