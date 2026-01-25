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

    // 提交试卷
    @PostMapping("/exam/submit")
    public Map<String, Object> submitExam(@RequestBody Map<String, Object> payload) {
        Integer examId = (Integer) payload.get("examId");
        
        // 处理 answers，JSON中的key是String，需要转为Integer
        Map<String, String> rawAnswers = (Map<String, String>) payload.get("answers");
        java.util.Map<Integer, String> answers = new java.util.HashMap<>();
        if (rawAnswers != null) {
            for (Map.Entry<String, String> entry : rawAnswers.entrySet()) {
                try {
                    answers.put(Integer.valueOf(entry.getKey()), entry.getValue());
                } catch (NumberFormatException e) {
                    // ignore non-integer keys
                }
            }
        }

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        // 简单处理：如果是匿名用户，可能需要前端传userId，这里假设都已登录
        if (username == null || "anonymousUser".equals(username)) {
            // throw new RuntimeException("请先登录");
            // 开发测试阶段，如果没登录，暂时硬编码一个用户，或者让前端传userId
            // 这里为了稳妥，如果拿不到username，尝试从payload拿 (如果前端改了传userId)
            // 暂时报错吧
             throw new RuntimeException("请先登录");
        }

        Integer score = eduService.submitExam(examId, answers, username);
        return Map.of("score", score);
    }

    @GetMapping("/exam/my-list")
    public org.springframework.http.ResponseEntity<?> getMyExams() {
        System.err.println("DEBUG: Entering getMyExams...");
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            // Hardcode check
            if (auth == null) {
                System.err.println("DEBUG: Auth is null");
                return org.springframework.http.ResponseEntity.ok(java.util.Collections.emptyList());
            }
            
            String username = auth.getName();
            System.err.println("DEBUG: Username is " + username);
            
            if ("anonymousUser".equals(username)) {
                System.err.println("DEBUG: Anonymous user, returning empty list");
                return org.springframework.http.ResponseEntity.ok(java.util.Collections.emptyList());
            }
            
            com.education.backend.entity.User user = eduService.findAllUsers().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
            
            if (user == null) {
                System.err.println("DEBUG: User not found");
                return org.springframework.http.ResponseEntity.ok(java.util.Collections.emptyList());
            }
            
            System.err.println("DEBUG: Fetching exams for user " + user.getUserId());
            List<Map<String, Object>> exams = eduService.getStudentExams(user.getUserId());
            System.err.println("DEBUG: Found " + exams.size() + " exams");
            return org.springframework.http.ResponseEntity.ok(exams);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CRITICAL ERROR: " + e.getMessage());
            return org.springframework.http.ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/exam/start")
    public Map<String, Object> startExam(@RequestBody Map<String, Integer> payload) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.education.backend.entity.User user = eduService.findAllUsers().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElseThrow();
        return eduService.startExam(user.getUserId(), payload.get("examId"));
    }

    @PostMapping("/exam/switch-blur")
    public void recordSwitch(@RequestBody Map<String, Integer> payload) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.education.backend.entity.User user = eduService.findAllUsers().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElseThrow();
        eduService.updateSwitchCount(user.getUserId(), payload.get("examId"));
    }
}