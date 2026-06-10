package com.cqq._3dobject.service;

import com.cqq._3dobject.dto.LoginResponse;
import com.cqq._3dobject.entity.RefreshSession;
import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.repository.RefreshSessionRepository;
import com.cqq._3dobject.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void loginShouldCreateTokenPairAndPersistRefreshSession() {
        UserService userService = mock(UserService.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        RefreshSessionRepository refreshSessionRepository = mock(RefreshSessionRepository.class);
        when(userService.verifyPassword("user@example.com", "secret")).thenReturn(buildUser());
        when(jwtUtils.generateAccessToken(eq(7L), eq("user@example.com"), anyString())).thenReturn("access-token");
        when(jwtUtils.getAccessTokenTtlSeconds()).thenReturn(900L);
        when(refreshSessionRepository.save(any(RefreshSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthService authService = new AuthService();
        ReflectionTestUtils.setField(authService, "userService", userService);
        ReflectionTestUtils.setField(authService, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authService, "refreshSessionRepository", refreshSessionRepository);
        ReflectionTestUtils.setField(authService, "refreshTokenTtlMs", 86400000L);

        LoginResponse response = authService.login("user@example.com", "secret", "Chrome");

        ArgumentCaptor<RefreshSession> sessionCaptor = ArgumentCaptor.forClass(RefreshSession.class);
        verify(refreshSessionRepository).save(sessionCaptor.capture());
        RefreshSession savedSession = sessionCaptor.getValue();

        assertEquals("access-token", response.getAccessToken());
        assertEquals(900, response.getExpiresIn());
        assertNotNull(response.getRefreshToken());
        assertEquals(86400, response.getRefreshExpiresIn());
        assertEquals(7L, response.getUserInfo().get("uid"));
        assertEquals("Tester", response.getUserInfo().get("nickname"));
        assertEquals(RefreshSession.Status.active, savedSession.getStatus());
        assertEquals(7L, savedSession.getUid());
        assertEquals("Chrome", savedSession.getDeviceInfo());
        assertNotNull(savedSession.getSessionId());
        assertNotNull(savedSession.getRefreshTokenHash());
        assertTrue(savedSession.getExpiresAt().isAfter(LocalDateTime.now().plusHours(23)));
    }

    @Test
    void refreshShouldRejectReplayAndRevokeSession() {
        UserService userService = mock(UserService.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        RefreshSessionRepository refreshSessionRepository = mock(RefreshSessionRepository.class);
        RefreshSession session = new RefreshSession();
        session.setSessionId("session-1");
        session.setStatus(RefreshSession.Status.used);
        when(refreshSessionRepository.findByRefreshTokenHash(anyString())).thenReturn(Optional.of(session));

        AuthService authService = new AuthService();
        ReflectionTestUtils.setField(authService, "userService", userService);
        ReflectionTestUtils.setField(authService, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authService, "refreshSessionRepository", refreshSessionRepository);
        ReflectionTestUtils.setField(authService, "refreshTokenTtlMs", 86400000L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refresh("raw-token"));

        assertEquals("检测到 Refresh Token 重放，会话已吊销，请重新登录", ex.getMessage());
        verify(refreshSessionRepository).revokeBySessionId("session-1");
        verify(refreshSessionRepository, never()).save(any(RefreshSession.class));
    }

    @Test
    void refreshShouldRevokeExpiredSession() {
        UserService userService = mock(UserService.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        RefreshSessionRepository refreshSessionRepository = mock(RefreshSessionRepository.class);
        RefreshSession session = new RefreshSession();
        session.setSessionId("session-2");
        session.setUid(7L);
        session.setStatus(RefreshSession.Status.active);
        session.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(refreshSessionRepository.findByRefreshTokenHash(anyString())).thenReturn(Optional.of(session));
        when(refreshSessionRepository.save(any(RefreshSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthService authService = new AuthService();
        ReflectionTestUtils.setField(authService, "userService", userService);
        ReflectionTestUtils.setField(authService, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authService, "refreshSessionRepository", refreshSessionRepository);
        ReflectionTestUtils.setField(authService, "refreshTokenTtlMs", 86400000L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refresh("expired-token"));

        assertEquals("Refresh Token 已过期，请重新登录", ex.getMessage());
        assertEquals(RefreshSession.Status.revoked, session.getStatus());
        verify(refreshSessionRepository).save(session);
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void refreshShouldRotateTokensWithinSameSession() {
        UserService userService = mock(UserService.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        RefreshSessionRepository refreshSessionRepository = mock(RefreshSessionRepository.class);
        RefreshSession session = new RefreshSession();
        session.setSessionId("session-3");
        session.setUid(7L);
        session.setStatus(RefreshSession.Status.active);
        session.setDeviceInfo("Safari");
        session.setExpiresAt(LocalDateTime.now().plusHours(1));
        when(refreshSessionRepository.findByRefreshTokenHash(anyString())).thenReturn(Optional.of(session));
        when(refreshSessionRepository.save(any(RefreshSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.getUserById(7L)).thenReturn(buildUser());
        when(jwtUtils.generateAccessToken(7L, "user@example.com", "session-3")).thenReturn("rotated-access");
        when(jwtUtils.getAccessTokenTtlSeconds()).thenReturn(900L);

        AuthService authService = new AuthService();
        ReflectionTestUtils.setField(authService, "userService", userService);
        ReflectionTestUtils.setField(authService, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authService, "refreshSessionRepository", refreshSessionRepository);
        ReflectionTestUtils.setField(authService, "refreshTokenTtlMs", 7200000L);

        LoginResponse response = authService.refresh("valid-token");

        ArgumentCaptor<RefreshSession> sessionCaptor = ArgumentCaptor.forClass(RefreshSession.class);
        verify(refreshSessionRepository, times(2)).save(sessionCaptor.capture());
        RefreshSession previousSession = sessionCaptor.getAllValues().get(0);
        RefreshSession newSession = sessionCaptor.getAllValues().get(1);

        assertEquals("rotated-access", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(RefreshSession.Status.used, previousSession.getStatus());
        assertNotNull(previousSession.getLastUsedAt());
        assertEquals("session-3", newSession.getSessionId());
        assertEquals(RefreshSession.Status.active, newSession.getStatus());
        assertEquals("Safari", newSession.getDeviceInfo());
        assertEquals(7L, response.getUserInfo().get("uid"));
        assertEquals("user@example.com", response.getUserInfo().get("email"));
    }

    private static User buildUser() {
        User user = new User();
        user.setUid(7L);
        user.setNickname("Tester");
        user.setEmail("user@example.com");
        user.setAvatarUrl("https://img.example/avatar.png");
        user.setBio("bio");
        return user;
    }
}
