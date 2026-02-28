package com.eduication.language.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expireHours;

    public JwtService(@Value("${app.security.jwt-secret}") String jwtSecret,
                      @Value("${app.security.jwt-expire-hours}") long expireHours) {
        this.secretKey = Keys.hmacShaKeyFor(buildKeyBytes(jwtSecret));
        this.expireHours = expireHours;
    }

    public String generateToken(String username, Long userId) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expireHours, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object uid = parseClaims(token).get("uid");
        if (uid instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public boolean isTokenValid(String token, String username) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        return username.equals(claims.getSubject()) && expiration.after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private byte[] buildKeyBytes(String rawSecret) {
        try {
            byte[] decoded = Decoders.BASE64.decode(rawSecret);
            if (decoded.length >= 32) {
                return decoded;
            }
        } catch (Exception ignored) {
            // 若不是 base64 字符串，则走原始字符处理
        }
        byte[] bytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32) {
            return bytes;
        }
        byte[] expanded = new byte[32];
        for (int i = 0; i < expanded.length; i++) {
            expanded[i] = bytes[i % bytes.length];
        }
        return expanded;
    }
}
