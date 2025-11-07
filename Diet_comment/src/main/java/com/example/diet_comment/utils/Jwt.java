package com.example.diet_comment.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Jwt {

    private static String SECRET;
    private static Long EXPIRATION_TIME;
    private static SecretKey SECRET_KEY;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        Jwt.SECRET = secret;
        Jwt.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.expiration}")
    public void setExpiration(Long expiration) {
        Jwt.EXPIRATION_TIME = expiration;
    }

    /**
     * 生成JWT
     * @param userId 用户ID
     * @param userName 用户名
     * @return JWT字符串
     */
    public static String generateJwt(String userId, String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("name", userName);

        return Jwts.builder()
                .setClaims(claims) 
                .setIssuedAt(new Date())
                //  .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))暂时移除
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWT
     * @param token JWT字符串
     * @return 用户ID
     */
    public static Integer parseJwt(String token) {
      
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) 
                .build()
                .parseClaimsJws(token)
                .getBody(); 

        return Integer.parseInt(claims.get("userId").toString());
    }
}