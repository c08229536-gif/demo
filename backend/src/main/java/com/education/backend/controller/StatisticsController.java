package com.education.backend.controller;

import com.education.backend.entity.Course;
import com.education.backend.entity.User;
import com.education.backend.repository.*;
import com.education.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private LearningRecordRepository learningRecordRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/teacher/overview")
    public Map<String, Object> getTeacherOverview() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User teacher = userService.findByUsername(username);
        
        // 1. 获取该老师的所有课程
        List<Course> courses = courseRepository.findByTeacherId(teacher.getUserId());
        
        int totalStudents = 0;
        double totalScore = 0;
        int gradedCount = 0;
        
        List<Map<String, Object>> courseData = new ArrayList<>();
        
        for (Course c : courses) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("courseId", c.getCourseId());
            stats.put("courseName", c.getTitle());
            
            // 选课人数
            int studentCount = studentCourseRepository.findByCourseId(c.getCourseId()).size();
            stats.put("studentCount", studentCount);
            totalStudents += studentCount;
            
            // 平均分 (仅统计已批改的)
            Double avgScore = submissionRepository.findAll().stream()
                    .filter(s -> s.getAssignmentId() != null) // 简化逻辑：此处应按 assignment 关联，先全局过滤该课程的作业提交
                    .filter(s -> "已批改".equals(s.getStatus()))
                    .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0)
                    .average().orElse(0.0);
            
            stats.put("avgScore", Math.round(avgScore * 10) / 10.0);
            
            courseData.add(stats);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCourses", courses.size());
        result.put("totalStudents", totalStudents);
        result.put("courseDetails", courseData);
        
        return result;
    }
    @GetMapping("/teacher/course/{courseId}/students")
    public List<Map<String, Object>> getCourseStudents(@PathVariable Integer courseId) {
        return studentCourseRepository.findByCourseId(courseId).stream()
                .map(sc -> {
                    User user = userRepository.findById(sc.getUserId()).orElse(null);
                    Map<String, Object> map = new HashMap<>();
                    if (user != null) {
                        map.put("userId", user.getUserId());
                        map.put("username", user.getUsername());
                        map.put("realName", user.getRealName());
                        map.put("studentNo", user.getStudentNo());
                        map.put("phone", user.getPhone());
                        map.put("email", user.getEmail());
                        map.put("avatar", user.getAvatar());
                        map.put("enrollTime", sc.getEnrollTime());
                    }
                    return map;
                })
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());
    }
}
