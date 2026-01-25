package com.education.backend.service;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class EduService {
    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private SysMessageRepository messageRepo;
    @Autowired private CourseProgressRepository progressRepo;
    @Autowired private ExamRepository examRepo;
    @Autowired private ExamQuestionRepository questionRepo;
    @Autowired private SysLogRepository logRepo;
    @Autowired private HomeBannerRepository bannerRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ExamResultRepository examResultRepo;
    @Autowired private StudentCourseRepository studentCourseRepo;

    public List<User> findAllUsers() { return userRepo.findAll(); }

    @Transactional
    public void addUser(String username, String realName, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setRole(roleName);
        user.setPassword(passwordEncoder.encode("123456"));
        Role role = roleRepo.findByRoleName(roleName.toUpperCase()).orElseThrow();
        user.setRoles(Collections.singleton(role));
        userRepo.save(user);
    }

    @Transactional
    public void updateUserRole(Integer userId, String newRoleName) {
        User user = userRepo.findById(userId).orElseThrow();
        user.setRole(newRoleName);
        Role role = roleRepo.findByRoleName(newRoleName.toUpperCase()).orElseThrow();
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepo.save(user);
    }

    @Transactional
    public void auditCourse(Integer courseId, Boolean pass) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        course.setStatus(pass ? 1 : 2);
        courseRepo.save(course);
    }

    public List<HomeBanner> getBanners() { return bannerRepo.findAll(); }
    public Exam getExam(Integer id) { return examRepo.findById(id).orElse(null); }
    public List<ExamQuestion> getQuestions(Integer examId) { return questionRepo.findByExamId(examId); }

    @Transactional
    public void updateProgress(CourseProgress p) {
        CourseProgress existing = progressRepo.findByUserIdAndChapterId(p.getUserId(), p.getChapterId()).orElse(p);
        existing.setProgressPercent(p.getProgressPercent());
        existing.setIsFinished(p.getProgressPercent() >= 100 ? 1 : 0);
        progressRepo.save(existing);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void saveLog(SysLog log) {
        log.setCreateTime(LocalDateTime.now());
        logRepo.save(log);
    }

    @Transactional
    public Map<String, Object> startExam(Integer userId, Integer examId) {
        ExamResult result = examResultRepo.findByStudentId(userId).stream()
                .filter(r -> r.getExamId().equals(examId))
                .findFirst()
                .orElse(null);
        
        if (result == null) {
            result = new ExamResult();
            result.setExamId(examId);
            result.setStudentId(userId);
            result.setStartTime(new Date());
            result.setStatus(0);
            result.setSwitchCount(0);
            examResultRepo.save(result);
        }

        // 计算剩余秒数
        Exam exam = examRepo.findById(examId).orElseThrow();
        long now = System.currentTimeMillis();
        long start = result.getStartTime().getTime();
        long elapsedSeconds = (now - start) / 1000;
        long totalSeconds = exam.getDuration() * 60;
        long remaining = totalSeconds - elapsedSeconds;

        // 核心修复：如果时间用完了，自动提交
        if (remaining <= 0 && result.getStatus() == 0) {
             result.setStatus(1);
             result.setSubmitTime(new Date());
             result.setScore(0); // 超时未提交，得0分或者保留已做部分
             examResultRepo.save(result);
             remaining = 0;
        }

        Map<String, Object> map = new java.util.HashMap<>();
        map.put("startTime", result.getStartTime());
        map.put("remainingSeconds", remaining > 0 ? remaining : 0);
        map.put("switchCount", result.getSwitchCount());
        map.put("status", result.getStatus());
        return map;
    }

    @Transactional
    public void updateSwitchCount(Integer userId, Integer examId) {
        ExamResult result = examResultRepo.findByStudentId(userId).stream()
                .filter(r -> r.getExamId().equals(examId))
                .findFirst()
                .orElse(null);
        if (result != null && result.getStatus() == 0) {
            result.setSwitchCount(result.getSwitchCount() + 1);
            examResultRepo.save(result);
        }
    }

    @Transactional
    public Integer submitExam(Integer examId, Map<Integer, String> answers, String username) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<ExamQuestion> questions = questionRepo.findByExamId(examId);

        int totalScore = 0;
        for (ExamQuestion q : questions) {
            String studentAnswer = answers.get(q.getId());
            // 简单判分逻辑：如果是客观题且答案完全一致（忽略大小写），则得分
            if (studentAnswer != null && q.getAnswer() != null) {
                 String type = q.getType();
                 if ("单选".equals(type) || "判断".equals(type) || "single".equalsIgnoreCase(type) || "judge".equalsIgnoreCase(type)) {
                     if (studentAnswer.trim().equalsIgnoreCase(q.getAnswer().trim())) {
                         totalScore += (q.getScore() != null ? q.getScore() : 0);
                     }
                 }
                 // 简答题暂不自动判分，或者可以根据关键词匹配
            }
        }

        ExamResult result = examResultRepo.findByStudentId(user.getUserId()).stream()
                .filter(r -> r.getExamId().equals(examId))
                .findFirst()
                .orElse(new ExamResult());

        if (result.getId() == null) {
            result.setExamId(examId);
            result.setStudentId(user.getUserId());
            result.setStartTime(new java.util.Date()); // 如果没开始过直接提交，也记录一下
        }
        
        result.setScore(totalScore);
        result.setSubmitTime(new java.util.Date());
        result.setStatus(1); // 标记为已提交
        try {
            result.setAnswers(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(answers));
        } catch (Exception e) {
            result.setAnswers("{}");
        }

        examResultRepo.save(result);
        return totalScore;
    }

    public List<Map<String, Object>> getStudentExams(Integer userId) {
        System.out.println("DEBUG: getStudentExams called for userId: " + userId);
        if (studentCourseRepo == null) {
            System.err.println("ERROR: studentCourseRepo is null!");
            return new java.util.ArrayList<>();
        }
        
        // 获取学生已选课程ID列表
        List<com.education.backend.entity.StudentCourse> courses = studentCourseRepo.findByUserId(userId);
        if (courses == null) {
            courses = new java.util.ArrayList<>();
        }
        
        List<Integer> enrolledCourseIds = courses.stream()
                .map(com.education.backend.entity.StudentCourse::getCourseId)
                .collect(java.util.stream.Collectors.toList());
        
        System.out.println("DEBUG: Enrolled courses: " + enrolledCourseIds);

        List<Exam> exams = examRepo.findAll();
        System.out.println("DEBUG: All exams count: " + exams.size());
        
        List<ExamResult> myResults = examResultRepo.findByStudentId(userId);
        System.out.println("DEBUG: User results count: " + myResults.size());
        
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (Exam e : exams) {
            System.out.println("DEBUG: Processing exam: " + e.getId() + ", state=" + e.getState() + ", courseId=" + e.getCourseId());
            if (e.getState() != 1) {
                System.out.println("DEBUG: Exam " + e.getId() + " skipped (state != 1)");
                continue; // 未发布的不显示
            }
            // 过滤掉非已选课程的考试
            if (e.getCourseId() != null && !enrolledCourseIds.contains(e.getCourseId())) {
                System.out.println("DEBUG: Exam " + e.getId() + " skipped (course not enrolled)");
                continue;
            }

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("duration", e.getDuration());
            map.put("totalScore", e.getTotalScore());
            map.put("courseId", e.getCourseId());
            
            // 补充课程名称
            courseRepo.findById(e.getCourseId()).ifPresent(c -> map.put("courseName", c.getTitle()));

            // 查找状态
            ExamResult r = myResults.stream().filter(res -> res.getExamId().equals(e.getId())).findFirst().orElse(null);
            if (r != null) {
                map.put("status", r.getStatus() == 1 ? "已提交" : "进行中");
                map.put("score", r.getScore());
            } else {
                map.put("status", "未开始");
            }
            list.add(map);
        }
        return list;
    }
}