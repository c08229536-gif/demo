package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import com.education.backend.service.EduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // 👈 必须导入，修复 image_57f25f 报错

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/admin") // 👈 注意：去掉 /api，以适配 Vite 代理重写规则
@CrossOrigin
public class AdminController {

    @Autowired private EduService eduService;
    @Autowired private UserRepository userRepository;
    @Autowired private HomeBannerRepository bannerRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private ExamQuestionRepository questionRepository;
    @Autowired private SysLogRepository logRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;


    // === 1. 用户权限管理 (修复 404) ===
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return eduService.findAllUsers();
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addUser(@RequestBody User request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setRealName(request.getRealName());

        // 关键修复：从 RoleRepository 查找 Role 并设置到用户的 Set<Role> 中
        Role role = roleRepository.findByRoleName(request.getRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("错误：找不到角色: " + request.getRole()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);
        // 同时，为了兼容旧的前端逻辑或显示，仍然设置一下 string role
        newUser.setRole(request.getRole());

        newUser.setPassword(passwordEncoder.encode("123456"));
        newUser.setFirstLogin(true);

        userRepository.save(newUser);
        return ResponseEntity.ok("用户添加成功");
    }

    @PostMapping("/users/batch-add")
    @Transactional
    public ResponseEntity<?> batchAddUsers(@RequestBody List<User> users) {
        // 1. 预加载所有角色和已存在的用户名，提高效率
        Role studentRole = roleRepository.findByRoleName("STUDENT").orElseThrow(() -> new RuntimeException("角色不存在: STUDENT"));
        Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow(() -> new RuntimeException("角色不存在: TEACHER"));
        List<String> existingUsernames = userRepository.findAll().stream().map(User::getUsername).collect(java.util.stream.Collectors.toList());

        int successCount = 0;
        int skippedCount = 0;
        
        for (User userRequest : users) {
            // 2. 如果用户名已存在，则跳过
            if (existingUsernames.contains(userRequest.getUsername())) {
                skippedCount++;
                continue;
            }

            User newUser = new User();
            newUser.setUsername(userRequest.getUsername());
            newUser.setRealName(userRequest.getRealName());
            newUser.setPassword(passwordEncoder.encode("123456"));
            newUser.setFirstLogin(true);

            // 3. 分配角色
            Set<Role> roles = new HashSet<>();
            if ("student".equalsIgnoreCase(userRequest.getRole())) {
                roles.add(studentRole);
            } else if ("teacher".equalsIgnoreCase(userRequest.getRole())) {
                roles.add(teacherRole);
            } else {
                // 如果角色不是学生或老师，也跳过，或者可以抛出异常
                skippedCount++;
                continue;
            }
            newUser.setRoles(roles);
            newUser.setRole(userRequest.getRole()); // 兼容旧字段

            // 4. 保存新用户
            userRepository.save(newUser);
            successCount++;
        }

        String message = String.format("批量导入完成：成功新增 %d 人，因用户名已存在或角色无效而跳过 %d 人。", successCount, skippedCount);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setPassword(passwordEncoder.encode("123456"));
        user.setFirstLogin(true);
        userRepository.save(user);

        return ResponseEntity.ok("密码重置成功");
    }

    @GetMapping("/user/fix-roles")
    @Transactional
    public ResponseEntity<?> fixRoles() {
        List<User> allUsers = userRepository.findAll();
        Role studentRole = roleRepository.findByRoleName("STUDENT").orElseThrow();
        Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow();
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();

        for (User user : allUsers) {
            if (user.getRole() != null) {
                Set<Role> roles = new HashSet<>();
                switch (user.getRole()) {
                    case "student":
                        roles.add(studentRole);
                        break;
                    case "teacher":
                        roles.add(teacherRole);
                        break;
                    case "admin":
                        roles.add(adminRole);
                        break;
                }
                if (!roles.isEmpty()) {
                    user.setRoles(roles);
                    userRepository.save(user);
                }
            }
        }
        return ResponseEntity.ok("所有用户角色已校准。");
    }

    // === 2. 课程审核台 (修复 image_595a49) ===
    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(0);
    }

    @PostMapping("/course/audit")
    public String auditCourse(@RequestBody Map<String, Object> params) {
        eduService.auditCourse((Integer)params.get("courseId"), (Boolean)params.get("pass"));
        return "审核处理完毕";
    }

    // === 3. 首页运营管理 (轮播图) ===
    @PostMapping("/banners/save")
    public HomeBanner saveBanner(@RequestBody HomeBanner banner) {
        return bannerRepository.save(banner);
    }

    @DeleteMapping("/banners/{id}")
    public void deleteBanner(@PathVariable Integer id) {
        bannerRepository.deleteById(id);
    }

    @GetMapping("/banners")
    public List<HomeBanner> getBanners() {
        return bannerRepository.findAll();
    }

    // === 4. 发布试卷 (核心补全：解决学生端没内容问题) ===
    @PostMapping("/exam/publish")
    @Transactional
    @SuppressWarnings("unchecked")
    public String publishExam(@RequestBody Map<String, Object> payload) {
        Exam exam = new Exam();
        exam.setTitle((String) payload.get("title"));
        exam.setDuration((Integer) payload.get("duration"));
        exam.setCourseId((Integer) payload.get("courseId"));
        exam.setTotalScore((Integer) payload.get("totalScore"));
        Exam savedExam = examRepository.save(exam);

        List<Map<String, Object>> questions = (List<Map<String, Object>>) payload.get("questions");
        for (Map<String, Object> qMap : questions) {
            ExamQuestion q = new ExamQuestion();
            q.setExamId(savedExam.getId());
            q.setContent((String) qMap.get("content"));
            q.setType((String) qMap.get("type"));
            q.setOptions((String) qMap.get("options")); 
            q.setAnswer((String) qMap.get("answer"));
            q.setScore((Integer) qMap.get("score"));
            questionRepository.save(q);
        }
        return "试卷发布成功";
    }

    @GetMapping("/logs")
    public List<SysLog> getLogs() {
        return logRepository.findAll();
    }
}