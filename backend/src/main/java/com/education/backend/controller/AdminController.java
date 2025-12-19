package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import com.education.backend.service.EduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // 👈 必须导入，修复 image_57f25f 报错

import java.util.List;
import java.util.Map;

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

    // === 1. 用户权限管理 (修复 404) ===
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return eduService.findAllUsers();
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