package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@TableName("comments")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("post_id")
    private Integer postId;

    @TableField("user_id")
    private Integer userId;

    @TableField("parent_comment_id")
    private Integer parentCommentId;

	@TableField("content")
    private String content; // 允许为 null 支持纯图片

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 以下字段不存在于数据库表中，用于关联查询时接收数据
    @TableField(exist = false)
    private User user; // 评论作者信息

    @TableField(exist = false)
    private List<Comment> replies; // 子评论列表

    // Getters and Setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getParentCommentId() {
		return parentCommentId;
	}

	public void setParentCommentId(Integer parentCommentId) {
		this.parentCommentId = parentCommentId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
