package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private HomeBannerService bannerService;
    @Autowired
    private ExamService examService;
    @Autowired
    private SysLogService logService;

    // === 1. 用户权限管理 ===
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addUser(@RequestBody User request) {
        try {
            userService.addUser(request);
            return ResponseEntity.ok("用户添加成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/batch-add")
    public ResponseEntity<?> batchAddUsers(@RequestBody List<User> users) {
        try {
            userService.batchAddUsers(users);
            return ResponseEntity.ok("批量导入处理完成"); // Can refine logic inside service if exact counts are needed
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Integer> payload) {
        try {
            userService.resetPassword(payload.get("userId"));
            return ResponseEntity.ok("密码重置成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/fix-roles")
    public ResponseEntity<?> fixRoles() {
        userService.fixRoles();
        return ResponseEntity.ok("所有用户角色已校准。");
    }

    // === 2. 课程审核台 ===
    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
        return courseService.getPendingCourses();
    }

    @PostMapping("/course/audit")
    public String auditCourse(@RequestBody Map<String, Object> params) {
        courseService.auditCourse((Integer) params.get("courseId"), (Boolean) params.get("pass"));
        return "审核处理完毕";
    }

    // === 3. 首页运营管理 (轮播图) ===
    @PostMapping("/banners/save")
    public HomeBanner saveBanner(@RequestBody HomeBanner banner) {
        return bannerService.saveBanner(banner);
    }

    @DeleteMapping("/banners/{id}")
    public void deleteBanner(@PathVariable Integer id) {
        bannerService.deleteBanner(id);
    }

    @GetMapping("/banners")
    public List<HomeBanner> getBanners() {
        return bannerService.getBanners();
    }

    // === 4. 发布试卷 ===
    @PostMapping("/exam/publish")
    public String publishExam(@RequestBody Map<String, Object> payload) {
        return examService.publishExam(payload);
    }

    @PostMapping("/exam/parse-word")
    public List<Map<String, Object>> parseWordExam(@RequestBody Map<String, String> payload) {
        return examService.parseWordExam(payload);
    }

    // === 5. 系统日志 ===
    @GetMapping("/logs")
    public List<SysLog> getLogs() {
        return logService.getLogs();
    }
}
