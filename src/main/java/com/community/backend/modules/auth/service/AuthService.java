package com.community.backend.modules.auth.service;

import com.community.backend.common.exception.BusinessException;
import com.community.backend.modules.auth.dto.LoginRequest;
import com.community.backend.modules.auth.dto.RefreshTokenRequest;
import com.community.backend.modules.auth.dto.RegisterRequest;
import com.community.backend.modules.auth.dto.TokenResponse;
import com.community.backend.modules.user.dto.UserResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import com.community.backend.security.JwtProperties;
import com.community.backend.security.JwtTokenProvider;
import com.community.backend.security.JwtTokenProvider.RefreshTokenPayload;
import com.community.backend.security.LoginUser;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:";
    private static final String DEFAULT_ROLE = "USER";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       JwtProperties jwtProperties,
                       StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public UserResponse register(RegisterRequest request) {
        if (userService.getByUsername(request.username()) != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.username());
        // 数据库里只存 BCrypt 哈希，不存明文密码
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(StringUtils.hasText(request.nickname()) ? request.nickname() : request.username());
        user.setRole(DEFAULT_ROLE);

        try {
            userService.createUser(user);
        } catch (DuplicateKeyException e) {
            // 两个请求同时注册同一个用户名时，由数据库唯一约束兜底
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
        }
        // 重新查一次，拿到数据库生成的 create_time
        return UserResponse.from(userService.getUserById(user.getId()));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userService.getByUsername(request.username());
        // 用户不存在和密码错误返回同一句话，避免暴露哪些用户名已注册
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        return issueTokens(user);
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenPayload payload = jwtTokenProvider.parseRefreshToken(request.refreshToken())
                .orElseThrow(this::invalidRefreshToken);

        // 删除成功才算有效：每个 Refresh Token 只能用一次，用过或已登出的都会被拒绝
        if (!Boolean.TRUE.equals(stringRedisTemplate.delete(refreshTokenKey(payload.tokenId())))) {
            throw invalidRefreshToken();
        }

        User user = userService.getUserById(payload.userId());
        if (user == null) {
            throw invalidRefreshToken();
        }
        return issueTokens(user);
    }

    public void logout(RefreshTokenRequest request) {
        jwtTokenProvider.parseRefreshToken(request.refreshToken())
                .ifPresent(payload -> stringRedisTemplate.delete(refreshTokenKey(payload.tokenId())));
    }

    private TokenResponse issueTokens(User user) {
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getRole());
        String tokenId = UUID.randomUUID().toString();

        // Redis 里记录这个 Refresh Token 还有效，过期时间和 Token 本身一致
        stringRedisTemplate.opsForValue().set(
                refreshTokenKey(tokenId),
                String.valueOf(user.getId()),
                jwtProperties.refreshTokenTtl()
        );

        return new TokenResponse(
                jwtTokenProvider.createAccessToken(loginUser),
                jwtTokenProvider.createRefreshToken(user.getId(), tokenId),
                "Bearer",
                jwtProperties.accessTokenTtl().toSeconds()
        );
    }

    private String refreshTokenKey(String tokenId) {
        return REFRESH_TOKEN_KEY_PREFIX + tokenId;
    }

    private BusinessException invalidRefreshToken() {
        return new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh Token 无效或已过期");
    }
}
