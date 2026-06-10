package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.LoginResponse;
import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.service.AuthService;
import com.cqq._3dobject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * 登录：返回双 Token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String account = body.get("account");
        String password = body.get("password");
        String deviceInfo = request.getHeader("User-Agent");

        LoginResponse response = authService.login(account, password, deviceInfo);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "账号或密码错误"));
    }

    /**
     * 注册：返回精简信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                    body.get("nickname"),
                    body.get("email"),
                    body.get("password")
            );
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("uid", user.getUid());
            result.put("nickname", user.getNickname());
            result.put("email", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 刷新 Token（Rotation）
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        try {
            String refreshToken = body.get("refreshToken");
            LoginResponse response = authService.refresh(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 登出当前设备
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String sessionId = (String) request.getAttribute("current_sid");
        authService.logout(sessionId);
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    /**
     * 登出所有设备
     */
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("current_uid");
        authService.logoutAll(uid);
        return ResponseEntity.ok(Map.of("message", "已登出所有设备"));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("current_uid");
        User user = userService.getUserById(uid);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "用户不存在"));
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCurrentUser(@RequestBody User user, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("current_uid");
        User updated = userService.updateUser(uid, user);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "更新失败"));
    }
}
