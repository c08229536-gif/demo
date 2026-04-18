package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin
public class EduController {

    @Autowired
    private HomeBannerService bannerService;
    @Autowired
    private ExamService examService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;

    @GetMapping("/banners")
    public List<HomeBanner> getActiveBanners() {
        return bannerService.getBanners();
    }

    @GetMapping("/exam/{id}")
    public Map<String, Object> getExam(@PathVariable Integer id) {
        Exam info = examService.getExam(id);
        List<ExamQuestion> questions = examService.getQuestions(id);
        return Map.of("info", info != null ? info : new Exam(), "questions", questions);
    }

    @PostMapping("/progress/update")
    public String updateProgress(@RequestBody CourseProgress progress) {
        courseService.updateProgress(progress);
        return "success";
    }

    @PostMapping("/exam/submit")
    public Map<String, Object> submitExam(@RequestBody Map<String, Object> payload) {
        Integer examId = (Integer) payload.get("examId");

        Map<String, String> rawAnswers = (Map<String, String>) payload.get("answers");
        java.util.Map<Integer, String> answers = new java.util.HashMap<>();
        if (rawAnswers != null) {
            for (Map.Entry<String, String> entry : rawAnswers.entrySet()) {
                try {
                    answers.put(Integer.valueOf(entry.getKey()), entry.getValue());
                } catch (NumberFormatException e) {
                }
            }
        }

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("请先登录");
        }

        Integer score = examService.submitExam(examId, answers, username);
        return Map.of("score", score);
    }

    @GetMapping("/exam/my-list")
    public org.springframework.http.ResponseEntity<?> getMyExams() {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth == null) {
                return org.springframework.http.ResponseEntity.ok(java.util.Collections.emptyList());
            }
            String username = auth.getName();
            if ("anonymousUser".equals(username)) {
                return org.springframework.http.ResponseEntity.ok(java.util.Collections.emptyList());
            }

            User user = userService.findByUsername(username);
            List<Map<String, Object>> exams = examService.getStudentExams(user.getUserId());
            return org.springframework.http.ResponseEntity.ok(exams);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/exam/start")
    public Map<String, Object> startExam(@RequestBody Map<String, Integer> payload) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return examService.startExam(user.getUserId(), payload.get("examId"));
    }

    @PostMapping("/exam/switch-blur")
    public void recordSwitch(@RequestBody Map<String, Integer> payload) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        examService.updateSwitchCount(user.getUserId(), payload.get("examId"));
    }
}