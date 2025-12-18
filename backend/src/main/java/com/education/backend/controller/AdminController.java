package com.education.backend.controller;

import com.education.backend.entity.Course;
import com.education.backend.entity.Role;
import com.education.backend.entity.User;
import com.education.backend.entity.SysMessage; // 👈 导入这个
import com.education.backend.repository.CourseRepository;
import com.education.backend.repository.RoleRepository;
import com.education.backend.repository.UserRepository;
import com.education.backend.repository.SysMessageRepository; // 👈 导入这个
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet; // 👈 导入这个，解决 HashSet 报错
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
    @Autowired
    private SysMessageRepository messageRepository;

    // === 1. 新增用户 (管理员分配账号) ===
    @PostMapping("/user/add")
    public String addUser(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String realName = params.get("realName");
        String roleName = params.get("role");

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("该学号/工号已存在！");
        }

        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setRole(roleName);
        user.setPassword(passwordEncoder.encode("123456")); 
        
        String dbRoleName = roleName.toUpperCase();
        Role role = roleRepository.findByRoleName(dbRoleName)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);
        return "用户添加成功！初始密码为 123456";
    }

    // === 2. 获取用户列表 ===
    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    // === 3. 修改用户角色 ===
    @PostMapping("/user/role")
    public String changeUserRole(@RequestBody Map<String, Object> params) {
        Integer userId = Integer.valueOf(params.get("userId").toString());
        String newRoleName = (String) params.get("role");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setRole(newRoleName);

        String dbRoleName = newRoleName.toUpperCase();
        Role role = roleRepository.findByRoleName(dbRoleName)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + dbRoleName));
        
        user.setRoles(new HashSet<>(Collections.singletonList(role))); 

        userRepository.save(user);
        return "角色修改成功！";
    }

    // === 4. 重置密码 ===
    @PostMapping("/user/reset-password")
    public String resetPassword(@RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        return "密码已重置为 123456";
    }

    // === 5. 获取待审核课程 ===
    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(0);
    }

    // === 6. 审核课程 (核心修复部分) ===
    @PostMapping("/course/audit")
    public String auditCourse(@RequestBody Map<String, Object> params) {
        Integer courseId = Integer.valueOf(params.get("courseId").toString());
        Boolean pass = (Boolean) params.get("pass");

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (pass) {
            course.setStatus(1); // 上架
            courseRepository.save(course); // 👈 别忘了保存课程状态！

            // 发消息给老师
            // ⚠️ 注意：确保 Course 实体里有 teacherId 字段，否则这里 getTeacherId() 会报错
            if (course.getTeacherId() != null) {
                SysMessage msg = new SysMessage();
                msg.setUserId(course.getTeacherId());
                msg.setTitle("课程审核通过");
                msg.setContent("恭喜！您的课程《" + course.getTitle() + "》已通过审核并上架。");
                messageRepository.save(msg);
            }
            return "审核通过！课程已上架。"; // 👈 加上返回值
        } else {
            course.setStatus(2); // 驳回
            courseRepository.save(course); // 👈 别忘了保存课程状态！

            // 发消息给老师
            if (course.getTeacherId() != null) {
                SysMessage msg = new SysMessage();
                msg.setUserId(course.getTeacherId());
                msg.setTitle("课程审核驳回");
                msg.setContent("很遗憾，您的课程《" + course.getTitle() + "》未通过审核。");
                messageRepository.save(msg);
            }
            return "已驳回该课程。"; // 👈 加上返回值
        }
    }
}