package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.LoginResponse;
import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.service.AuthService;
import com.cqq._3dobject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @Test
    void loginShouldUseAccountAndUserAgent() throws Exception {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        LoginResponse response = new LoginResponse("access", 900, "refresh", 86400, Map.of("uid", 7L));
        when(authService.login("user@example.com", "secret", "JUnit")).thenReturn(response);

        MockMvc mockMvc = buildMockMvc(userService, authService);

        mockMvc.perform(post("/api/users/login")
                        .header("User-Agent", "JUnit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "user@example.com",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"))
                .andExpect(jsonPath("$.userInfo.uid").value(7));
    }

    @Test
    void registerShouldReturnBadRequestWhenServiceRejects() throws Exception {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        when(userService.register("Tester", "user@example.com", "secret"))
                .thenThrow(new RuntimeException("该邮箱已被注册，请直接登录"));

        MockMvc mockMvc = buildMockMvc(userService, authService);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "Tester",
                                  "email": "user@example.com",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("该邮箱已被注册，请直接登录"));
    }

    @Test
    void getCurrentUserShouldReturnNotFoundWhenUserMissing() throws Exception {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        when(userService.getUserById(7L)).thenReturn(null);

        MockMvc mockMvc = buildMockMvc(userService, authService);

        mockMvc.perform(get("/api/users/me").requestAttr("current_uid", 7L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    @Test
    void updateCurrentUserShouldReturnUpdatedUser() throws Exception {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        User updated = new User();
        updated.setUid(7L);
        updated.setNickname("New Name");
        when(userService.updateUser(eq(7L), any(User.class))).thenReturn(updated);

        MockMvc mockMvc = buildMockMvc(userService, authService);

        mockMvc.perform(put("/api/users/update")
                        .requestAttr("current_uid", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "New Name"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(7))
                .andExpect(jsonPath("$.nickname").value("New Name"));
    }

    @Test
    void logoutAllShouldUseCurrentUidAttribute() throws Exception {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);

        MockMvc mockMvc = buildMockMvc(userService, authService);

        mockMvc.perform(post("/api/users/logout-all").requestAttr("current_uid", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已登出所有设备"));

        verify(authService).logoutAll(7L);
    }

    private static MockMvc buildMockMvc(UserService userService, AuthService authService) {
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "authService", authService);
        return MockMvcBuilders.standaloneSetup(controller).build();
    }
}
