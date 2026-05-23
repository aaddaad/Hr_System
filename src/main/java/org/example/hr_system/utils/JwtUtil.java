package org.example.hr_system.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.hr_system.entity.SysUser;
import java.util.Date;

public class JwtUtil {
    // 密钥
    private static final String SECRET_KEY = "hr_system_2025";
    // 24小时过期
    private static final long EXPIRE = 24 * 60 * 60 * 1000L;

    // 生成Token
    public static String createToken(SysUser user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 解析Token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}