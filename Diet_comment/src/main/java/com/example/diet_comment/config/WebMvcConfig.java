package com.example.diet_comment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

        @Value("${app.upload-dir}")
        private String uploadDir;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                String location = dir.toUri().toString() + "/";

                registry.addResourceHandler("/static/uploads/**")
                                .addResourceLocations(location);

                registry.addResourceHandler("/images/**")
                                .addResourceLocations(location);

                registry.addResourceHandler("/food_picture/**")
                                .addResourceLocations(location);
        }
}
