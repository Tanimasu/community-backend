package com.community.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // jjwt 按密钥长度自动选择 HS256/HS384/HS512，至少要 32 字节，太短会直接抛异常
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(LoginUser loginUser) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(loginUser.id()))
                .claim(CLAIM_USERNAME, loginUser.username())
                .claim(CLAIM_ROLE, loginUser.role())
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.accessTokenTtl())))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId, String tokenId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(tokenId)
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.refreshTokenTtl())))
                .signWith(secretKey)
                .compact();
    }

    public Optional<LoginUser> parseAccessToken(String token) {
        return parse(token, TYPE_ACCESS).map(claims -> new LoginUser(
                Long.valueOf(claims.getSubject()),
                claims.get(CLAIM_USERNAME, String.class),
                claims.get(CLAIM_ROLE, String.class)
        ));
    }

    public Optional<RefreshTokenPayload> parseRefreshToken(String token) {
        return parse(token, TYPE_REFRESH).map(claims -> new RefreshTokenPayload(
                Long.valueOf(claims.getSubject()),
                claims.getId()
        ));
    }

    // 签名不对、已过期、格式错误、类型不匹配，统一返回 empty
    private Optional<Claims> parse(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!expectedType.equals(claims.get(CLAIM_TYPE, String.class))) {
                return Optional.empty();
            }
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public record RefreshTokenPayload(Long userId, String tokenId) {
    }
}
