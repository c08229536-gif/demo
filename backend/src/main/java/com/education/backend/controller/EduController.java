package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.service.EduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/") // 👈 适配 Vite 代理
@CrossOrigin
public class EduController {

    @Autowired private EduService eduService;

    // 首页获取已启用的轮播图
    @GetMapping("/banners")
    public List<HomeBanner> getActiveBanners() {
        return eduService.getBanners();
    }

    // 学生端获取考试详情
    @GetMapping("/exam/{id}")
    public Map<String, Object> getExam(@PathVariable Integer id) {
        Exam info = eduService.getExam(id);
        List<ExamQuestion> questions = eduService.getQuestions(id);
        return Map.of("info", info != null ? info : new Exam(), "questions", questions);
    }

    // 进度记录接口
    @PostMapping("/progress/update")
    public String updateProgress(@RequestBody CourseProgress progress) {
        eduService.updateProgress(progress);
        return "success";
    }
}