package com.education.backend.controller;

import com.education.backend.entity.User;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 注册接口 (保持不变)
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "失败：用户名已存在";
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("student");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(java.time.LocalDateTime.now());
        userRepository.save(user);
        return "注册成功！去登录吧。";
    }

    // === 👇 新增：获取当前登录用户信息 ===
    @GetMapping("/me")
    public User getCurrentUserInfo() {
        // 1. 从 Spring Security 上下文中获取当前用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 2. 查数据库
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 3. 把密码抹掉，不返回给前端 (安全起见)
        user.setPassword(null);
        
        return user;
    }
}