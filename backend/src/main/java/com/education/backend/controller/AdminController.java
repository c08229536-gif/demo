package com.education.backend.controller;

import com.education.backend.entity.Course;
import com.education.backend.entity.Role;
import com.education.backend.entity.User;
import com.education.backend.repository.CourseRepository;
import com.education.backend.repository.RoleRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/user/add")
    public String addUser(@RequestBody Map<String, String> params) {
        String username = params.get("username"); // 前端填写的学号或工号
        String realName = params.get("realName");
        String roleName = params.get("role"); // "student", "teacher", "admin"

        // 1. 检查账号是否存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("该学号/工号已存在！");
        }

        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setRole(roleName);
        // 默认密码 123456
        user.setPassword(passwordEncoder.encode("123456")); 
        
        // 设置关联权限
        String dbRoleName = roleName.toUpperCase();
        Role role = roleRepository.findByRoleName(dbRoleName)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);
        return "用户添加成功！初始密码为 123456";
    }

    // 1. 获取所有用户列表
    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        // 清空密码，防止泄露给前端
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    // 2. 修改用户角色 (最核心功能：提拔老师)
   @PostMapping("/user/role")
    public String changeUserRole(@RequestBody Map<String, Object> params) {
        // 🛑 修复1：防止 Integer/Long 类型转换报错
        Integer userId = Integer.valueOf(params.get("userId").toString());
        String newRoleName = (String) params.get("role"); // "student", "teacher", "admin"

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // A. 修改字符串字段
        user.setRole(newRoleName);

        // B. 修改关联表权限
        String dbRoleName = newRoleName.toUpperCase();
        Role role = roleRepository.findByRoleName(dbRoleName)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + dbRoleName));
        
        // 🛑 修复2：必须用可变的 HashSet，不能直接用 Collections.singleton，否则 Hibernate 可能会报错
        user.setRoles(new HashSet<>(Collections.singletonList(role))); 

        userRepository.save(user);
        return "角色修改成功！";
    }

    // 3. 重置密码 (管理员特权)
    @PostMapping("/user/reset-password")
    public String resetPassword(@RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 默认重置为 123456
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        return "密码已重置为 123456";
    }
    // === 👇 新增：获取待审核课程列表 ===
    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(0); // 查所有 status=0 的
    }
    // === 👇 新增：审核课程 (通过/驳回) ===
    @PostMapping("/course/audit")
    public String auditCourse(@RequestBody Map<String, Object> params) {
        Integer courseId = Integer.valueOf(params.get("courseId").toString());
        Boolean pass = (Boolean) params.get("pass"); // true:通过, false:驳回

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (pass) {
            course.setStatus(1); // 变更为已发布
            courseRepository.save(course);
            return "审核通过！课程已上架。";
        } else {
            course.setStatus(2); // 变更为已驳回
            courseRepository.save(course);
            return "已驳回该课程。";
        }
    }
}
