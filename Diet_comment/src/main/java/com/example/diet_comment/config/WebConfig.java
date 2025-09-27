package com.example.diet_comment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.diet_comment.interceptor.LoginCheck;

public class WebConfig implements WebMvcConfigurer {
	@Autowired
	private LoginCheck loginCheck;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginCheck)
				.addPathPatterns("/**") // 拦截所有请求
				.excludePathPatterns(
						"/login",
						"/register"
				);
	}
	
}
