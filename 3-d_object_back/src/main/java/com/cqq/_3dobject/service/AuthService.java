package com.cqq._3dobject.service;

import com.cqq._3dobject.dto.LoginResponse;
import com.cqq._3dobject.entity.RefreshSession;
import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.repository.RefreshSessionRepository;
import com.cqq._3dobject.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshSessionRepository refreshSessionRepository;

    @Value("${app.jwt.refresh-token-ttl}")
    private long refreshTokenTtlMs;

    // ==================== 登录 ====================

    public LoginResponse login(String email, String password, String deviceInfo) {
        User user = userService.verifyPassword(email, password);
        if (user == null) {
            return null;
        }
        return createTokenPair(user, deviceInfo);
    }

    // ==================== 刷新（Rotation） ====================

    @Transactional
    public LoginResponse refresh(String rawRefreshToken) {
        String tokenHash = sha256(rawRefreshToken);

        RefreshSession session = refreshSessionRepository.findByRefreshTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("无效的 Refresh Token"));

        // 重放检测：如果该 token 已经被使用过
        if (session.getStatus() == RefreshSession.Status.used) {
            // 整条会话作废
            refreshSessionRepository.revokeBySessionId(session.getSessionId());
            throw new RuntimeException("检测到 Refresh Token 重放，会话已吊销，请重新登录");
        }

        if (session.getStatus() == RefreshSession.Status.revoked) {
            throw new RuntimeException("Refresh Token 已吊销，请重新登录");
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(RefreshSession.Status.revoked);
            refreshSessionRepository.save(session);
            throw new RuntimeException("Refresh Token 已过期，请重新登录");
        }

        // 标记旧 token 为 used
        session.setStatus(RefreshSession.Status.used);
        session.setLastUsedAt(LocalDateTime.now());
        refreshSessionRepository.save(session);

        // 签发新的 token 对（沿用同一 sessionId）
        User user = userService.getUserById(session.getUid());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String sessionId = session.getSessionId();
        String newAccessToken = jwtUtils.generateAccessToken(user.getUid(), user.getEmail(), sessionId);
        String newRefreshToken = UUID.randomUUID().toString();

        RefreshSession newSession = new RefreshSession();
        newSession.setSessionId(sessionId);
        newSession.setUid(user.getUid());
        newSession.setRefreshTokenHash(sha256(newRefreshToken));
        newSession.setStatus(RefreshSession.Status.active);
        newSession.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenTtlMs / 1000));
        newSession.setDeviceInfo(session.getDeviceInfo());
        refreshSessionRepository.save(newSession);

        return new LoginResponse(
                newAccessToken,
                (int) jwtUtils.getAccessTokenTtlSeconds(),
                newRefreshToken,
                (int) (refreshTokenTtlMs / 1000),
                buildUserInfo(user)
        );
    }

    // ==================== 登出 ====================

    @Transactional
    public void logout(String sessionId) {
        refreshSessionRepository.revokeBySessionId(sessionId);
    }

    @Transactional
    public void logoutAll(Long uid) {
        refreshSessionRepository.revokeAllByUid(uid);
    }

    // ==================== 内部方法 ====================

    private LoginResponse createTokenPair(User user, String deviceInfo) {
        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtUtils.generateAccessToken(user.getUid(), user.getEmail(), sessionId);
        String refreshToken = UUID.randomUUID().toString();

        RefreshSession session = new RefreshSession();
        session.setSessionId(sessionId);
        session.setUid(user.getUid());
        session.setRefreshTokenHash(sha256(refreshToken));
        session.setStatus(RefreshSession.Status.active);
        session.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenTtlMs / 1000));
        session.setDeviceInfo(deviceInfo);
        refreshSessionRepository.save(session);

        return new LoginResponse(
                accessToken,
                (int) jwtUtils.getAccessTokenTtlSeconds(),
                refreshToken,
                (int) (refreshTokenTtlMs / 1000),
                buildUserInfo(user)
        );
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("uid", user.getUid());
        info.put("nickname", user.getNickname());
        info.put("email", user.getEmail());
        info.put("avatarUrl", user.getAvatarUrl());
        info.put("bio", user.getBio());
        return info;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
