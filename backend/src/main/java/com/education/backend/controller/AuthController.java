package com.education.backend.controller;

import com.education.backend.entity.UpdatePasswordRequest;
import com.education.backend.entity.User;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.SubmissionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

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
    public ResponseEntity<?> getCurrentUserInfo() {
        try {
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未登录"));
            }
            
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                 return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户不存在"));
            }
            
            // 统计数据
            int courseCount = studentCourseRepository.findByUserId(user.getUserId()).size();
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("realName", user.getRealName());
            response.put("role", user.getRole());
            response.put("createTime", user.getCreateTime());
            response.put("courseCount", courseCount); 
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
    @PostMapping("/update")
    public String updateInfo(@RequestBody User tempUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 只允许更新这三个字段，防止用户篡改 ID 或 角色
        if (tempUser.getPhone() != null) user.setPhone(tempUser.getPhone());
        if (tempUser.getEmail() != null) user.setEmail(tempUser.getEmail());
        if (tempUser.getStudentNo() != null) user.setStudentNo(tempUser.getStudentNo());
        
        userRepository.save(user);
        return "资料更新成功！";
    }

    @PostMapping("/change-password")
    @Transactional
    public String changePassword(@RequestBody Map<String, String> payload) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newPassword = payload.get("newPassword");

        if (newPassword == null || newPassword.isEmpty() || newPassword.length() < 6) {
            return "新密码不能为空或长度小于6位";
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(username, encodedPassword);

        return "密码修改成功！";
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("旧密码不正确");
        }

        // 2. Check if new password is valid
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest().body("新密码格式不正确 (长度至少为6位)");
        }

        // 3. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // If user was forced to change password, this flag should be updated
        if (user.isFirstLogin()) {
            user.setFirstLogin(false);
        }
        userRepository.save(user);

        return ResponseEntity.ok("密码更新成功");
    }
}