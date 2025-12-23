package com.example.diet_comment.model.DTO;

import com.example.diet_comment.model.User;

public class UserReg {
    private String userName;
    private String email;
    private String password;
    private String code;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User toUserEntity() {
        User user = new User();
        user.setUserName(this.userName);
        user.setEmail(this.email);
        user.setPassword(this.password);
        return user;
    }
}
