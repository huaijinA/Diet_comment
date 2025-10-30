package com.example.diet_comment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
						"/login",
						"/register",
                        "/error"
				);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 保持已有拦截器配置...
    // 映射 /images/** 到项目运行目录下的 ./images/ 文件夹
    registry.addResourceHandler("/images/**")
            .addResourceLocations("file:./images/");
	}
}
