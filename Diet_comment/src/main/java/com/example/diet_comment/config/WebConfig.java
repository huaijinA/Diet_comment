package com.example.diet_comment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.diet_comment.interceptor.LoginCheck;


@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private LoginCheck loginCheck;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginCheck)
				.addPathPatterns("/**") // 拦截所有请求
				.excludePathPatterns(
                        "/images/**",
						"/login",
						"/register",
                        "/error"
				);
	}



    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://localhost:5173") // 允许你的前端源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowCredentials(true) // 允许携带凭证（如cookies）
                .allowedHeaders("*") // 允许所有请求头
                .maxAge(3600); // 预检请求的有效期
    }
	
}
