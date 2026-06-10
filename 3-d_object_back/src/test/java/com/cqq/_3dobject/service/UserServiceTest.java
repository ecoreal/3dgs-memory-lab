package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void verifyPasswordShouldReturnUserWhenHashMatches() {
        UserRepository userRepository = mock(UserRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUid(8L);
        user.setEmail("user@example.com");
        user.setPasswordHash(encoder.encode("secret"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        UserService userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        User verified = userService.verifyPassword("user@example.com", "secret");

        assertEquals(8L, verified.getUid());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail("user@example.com")).thenReturn(new User());

        UserService userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.register("Tester", "user@example.com", "secret"));

        assertEquals("该邮箱已被注册，请直接登录", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerShouldHashPasswordBeforeSaving() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setUid(10L);
            return saved;
        });

        UserService userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        User created = userService.register("Tester", "user@example.com", "secret");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals(10L, created.getUid());
        assertEquals("Tester", saved.getNickname());
        assertNotNull(saved.getPasswordHash());
        assertNotEquals("secret", saved.getPasswordHash());
        assertTrue(new BCryptPasswordEncoder().matches("secret", saved.getPasswordHash()));
    }

    @Test
    void getUserMapByIdsShouldReturnEmptyMapForEmptyInput() {
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        Map<Long, User> userMap = userService.getUserMapByIds(List.of());

        assertTrue(userMap.isEmpty());
        verify(userRepository, never()).findByUidIn(any());
    }

    @Test
    void updateUserShouldOnlyPatchProvidedFields() {
        UserRepository userRepository = mock(UserRepository.class);
        User existing = new User();
        existing.setUid(9L);
        existing.setNickname("Old");
        existing.setEmail("user@example.com");
        existing.setAvatarUrl("old-avatar");
        existing.setBio("old-bio");
        when(userRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserService userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        User patch = new User();
        patch.setNickname("New");
        patch.setBio("new-bio");

        User updated = userService.updateUser(9L, patch);

        assertEquals("New", updated.getNickname());
        assertEquals("old-avatar", updated.getAvatarUrl());
        assertEquals("new-bio", updated.getBio());
        assertEquals("user@example.com", updated.getEmail());
    }

    @Test
    void updateUserShouldReturnNullWhenUserMissing() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        UserService userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        User updated = userService.updateUser(404L, new User());

        assertNull(updated);
        verify(userRepository, never()).save(any(User.class));
    }
}
