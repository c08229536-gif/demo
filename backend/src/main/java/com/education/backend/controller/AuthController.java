package com.education.backend.controller;

import com.education.backend.entity.User;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.SubmissionRepository;
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
    public Map<String, Object> getCurrentUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 统计数据
        int courseCount = studentCourseRepository.findByUserId(user.getUserId()).size();
        // 这里的 submissionRepository 需要加一个 countByStudentId 方法，或者直接查列表 size
        // 假设 Repository 里没有 count 方法，我们可以先查列表 (虽然性能不是最优，但简单)
        // int homeworkCount = submissionRepository.findByStudentId(user.getUserId()).size(); 
        // 暂时先返回 mock 数据或者你需要去 Repository 加方法
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("realName", user.getRealName());
        response.put("role", user.getRole());
        response.put("createTime", user.getCreateTime());
        response.put("courseCount", courseCount); // 加入课程数
        // response.put("homeworkCount", homeworkCount); 
        
        return response;
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
}