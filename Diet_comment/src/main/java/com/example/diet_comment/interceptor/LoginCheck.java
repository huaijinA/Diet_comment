package com.example.diet_comment.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.diet_comment.utils.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import com.example.diet_comment.utils.Jwt;


@Component
public class LoginCheck implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader("token");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 简单调试日志，确认实际请求路径
        System.out.println("[LoginCheck] " + request.getMethod() + " " + request.getRequestURI());

		if (token == null || token.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		Integer userId;

		try {
			userId = Jwt.parseJwt(token);


		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		request.setAttribute("userId", userId);
        System.out.println("logincheck"+userId);
		
		return true;
		
	}


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// No post-processing needed
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// No after-completion processing needed
	}



}
