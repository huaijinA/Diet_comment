package com.example.diet_comment.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

	@TableField("username")
    private String userName;

    @TableField("password_hash")
    private String password;

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

	public String getUserName() {
		return userName;
	}

    public void setUsername(String username) {
        this.userName = username;
    }

	public void setPassword(String password) {
		this.password = password;
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


	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id2) {
		this.id = id2;
	}

	public String getPassword() {
		return password;
	}






}
