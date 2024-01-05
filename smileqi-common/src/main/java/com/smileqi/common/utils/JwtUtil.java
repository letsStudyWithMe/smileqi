package com.smileqi.common.utils;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @ClassName: JwtUtil  JWT的工具封装
 */
public class JwtUtil {
 
    /**
     * @Field: KEY  加密的秘钥
     */
    private final static String KEY="smileqi";

    /**
     * 解析token ，获取当前用户id
     *
     * @return 令牌
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseToken(token);
        Long userId = (Long)claims.get("userId");
        return userId;
    }

    /**
     * 创建令牌
     *
     * @param userId 用户Id
     * @return 令牌
     */
    public static String createToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))//24小时
                .signWith(SignatureAlgorithm.HS512, KEY).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody();
    }
 
}