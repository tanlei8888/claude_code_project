package com.hedgehog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类，负责 token 的生成与解析。
 *
 * <p>JWT payload 包含：
 * <ul>
 *   <li>subject：用户 ID</li>
 *   <li>claim "username"：用户名</li>
 *   <li>claim "role"：用户角色（ADMIN/USER）</li>
 *   <li>issuedAt / expiration：签发时间 / 过期时间</li>
 * </ul>
 */
@Component
public class JwtUtil {

    /** HMAC-SHA 密钥 */
    private final SecretKey key;
    /** token 过期时间（毫秒） */
    private final long expiration;

    /**
     * 从配置文件注入 JWT 密钥和过期时间。
     *
     * @param secret     JWT 签名密钥
     * @param expiration token 有效期（毫秒）
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 生成 JWT token。
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     用户角色
     * @return JWT token 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT token。
     *
     * @param token JWT token 字符串
     * @return token 中的 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 中提取用户 ID。
     *
     * @param token JWT token 字符串
     * @return 用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }
}
