package com.example.diet_comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.diet_comment.mapper")
public class DietCommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietCommentApplication.class, args);
    }
}
