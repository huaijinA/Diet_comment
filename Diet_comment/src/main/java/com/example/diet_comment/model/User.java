package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

	@TableField("username")
    private String username;

    @TableField("password_hash")
    private String passwordHash;

	@TableField("email")
    private String email;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 以下字段不存在于数据库表中，用于关联查询时接收数据
    @TableField(exist = false)
    private List<Post> posts;

    @TableField(exist = false)
    private List<Comment> comments;

	public String getUsername() {
		return username;
	}

    public void setUsername(String username) {
        this.username = username;
    }

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public List<Comment> getComments() {
		return comments;
	}
}
