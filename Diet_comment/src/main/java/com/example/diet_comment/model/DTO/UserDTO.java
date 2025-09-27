package com.example.diet_comment.model.DTO;

import com.example.diet_comment.model.User;



public class UserDTO {
	private Integer id;
	private String userName;
	private String email;
	private String avatarUrl;

	public UserDTO(Integer id, String userName, String email, String avatarUrl) {
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.avatarUrl = avatarUrl;
	}

	

	public UserDTO() {
	}


	public User toUserEntity(UserDTO userDTO) {
		User user = new User();
		user.setId(userDTO.getId());
		user.setUserName(userDTO.getUserName());
		user.setEmail(userDTO.getEmail());
		user.setAvatarUrl(userDTO.getAvatarUrl());
		return user;
	}

	public UserDTO fromUser(User user) {
		return new UserDTO(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getAvatarUrl()
		);
	}

	private Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
}
