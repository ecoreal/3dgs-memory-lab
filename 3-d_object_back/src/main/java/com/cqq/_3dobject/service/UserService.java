package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 验证密码，返回用户或 null
     */
    public User verifyPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    /**
     * 注册（BCrypt 加密）
     */
    public User register(String nickname, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("该邮箱已被注册，请直接登录");
        }

        User newUser = new User();
        newUser.setNickname(nickname);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long uid) {
        return userRepository.findById(uid).orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<Long, User> getUserMapByIds(Collection<Long> uids) {
        if (uids == null || uids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userRepository.findByUidIn(uids).stream()
                .collect(Collectors.toMap(User::getUid, Function.identity()));
    }

    public User updateUser(Long uid, User updatedInfo) {
        User user = getUserById(uid);
        if (user != null) {
            if (updatedInfo.getNickname() != null) user.setNickname(updatedInfo.getNickname());
            if (updatedInfo.getAvatarUrl() != null) user.setAvatarUrl(updatedInfo.getAvatarUrl());
            if (updatedInfo.getBio() != null) user.setBio(updatedInfo.getBio());
            return userRepository.save(user);
        }
        return null;
    }
}
