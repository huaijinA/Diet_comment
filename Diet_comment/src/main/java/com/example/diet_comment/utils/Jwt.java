package com.example.diet_comment.utils;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Jwt {
	

	@Value("${jwt.secret}")
	private static String SECRET; 

	@Value("${jwt.expiration}")
	private static Long EXPIRATION_TIME; 

	public static String generateJwt(String userId, String userName) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("name", userName);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
	}


	public static Integer parseJwt(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(SECRET)
			.parseClaimsJws(token)
			.getBody();

		return claims.get("userId", Integer.class);
	
	}





}
