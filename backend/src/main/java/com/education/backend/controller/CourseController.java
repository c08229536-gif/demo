package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/course") // 👈 关键修改：统一加 /api 前缀
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private LearningRecordRepository learningRecordRepository;
    @Autowired
    private com.education.backend.repository.CourseReviewRepository reviewRepository;

    // === 1. 查询所有已上架课程 (包含评分) ===
    @GetMapping("/list")
    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findByStatus(1);
        for (Course c : courses) {
            List<CourseReview> reviews = reviewRepository.findByCourseIdOrderByCreateTimeDesc(c.getCourseId());
            if (reviews.isEmpty()) {
                c.setRating(0.0); 
            } else {
                double avg = reviews.stream().mapToInt(CourseReview::getRating).average().orElse(0.0);
                c.setRating(Math.round(avg * 10.0) / 10.0);
            }
        }
        return courses;
    }

    // === 2. 根据 ID 查询课程详情 ===
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    // === 3. 检查购买/选课状态 (修复 404 的核心方法) ===
    @GetMapping("/{id}/is-enrolled")
    public boolean checkEnrollStatus(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return studentCourseRepository.findByUserIdAndCourseId(user.getUserId(), id).isPresent();
    }

    // === 4. 查询我的课程列表 ===
    @GetMapping("/my-courses")
    public List<Course> getMyCourses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<StudentCourse> records = studentCourseRepository.findByUserId(user.getUserId());
        List<Integer> courseIds = records.stream().map(StudentCourse::getCourseId).toList();
        return courseRepository.findAllById(courseIds);
    }

    // === 5. 发布新课程 ===
    @PostMapping("/add")
    public String addCourse(@RequestBody Course course) {
        if (course.getTitle() == null || course.getTitle().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        course.setStatus(0); // 默认为待审核
        courseRepository.save(course);
        return "课程提交成功！请等待审核。";
    }

    // === 6. 获取章节列表 ===
    @GetMapping("/{courseId}/chapters")
    public List<Chapter> getCourseChapters(@PathVariable Integer courseId) {
        return chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
    }

    // === 7. 添加章节 ===
    @PostMapping("/chapter/add")
    public String addChapter(@RequestBody Chapter chapter) {
        chapterRepository.save(chapter);
        return "章节添加成功！";
    }

    // === 8. 标记章节学完 ===
    @PostMapping("/chapter/{chapterId}/finish")
    public String finishChapter(@PathVariable Integer chapterId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        if (learningRecordRepository.findByUserIdAndChapterId(user.getUserId(), chapterId).isPresent()) {
            return "已打卡";
        }

        LearningRecord record = new LearningRecord();
        record.setUserId(user.getUserId());
        record.setChapterId(chapterId);
        record.setCourseId(chapter.getCourseId());
        record.setStatus(1);
        record.setFinishTime(LocalDateTime.now());
        learningRecordRepository.save(record);
        return "打卡成功！";
    }

    // === 9. 查询课程学习进度 (修复 404 的核心方法) ===
    @GetMapping("/{courseId}/progress")
    public List<Integer> getCourseProgress(@PathVariable Integer courseId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return learningRecordRepository.findByUserIdAndCourseId(user.getUserId(), courseId)
                .stream().map(LearningRecord::getChapterId).collect(Collectors.toList());
    }

    // === 10. (老师用) 查询学员进度 ===
    @GetMapping("/{courseId}/students")
    public List<Map<String, Object>> getCourseStudents(@PathVariable Integer courseId) {
        List<StudentCourse> enrollments = studentCourseRepository.findByCourseId(courseId);
        int totalChapters = chapterRepository.countByCourseId(courseId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (StudentCourse sc : enrollments) {
            User student = userRepository.findById(sc.getUserId()).orElse(null);
            if (student == null) continue;
            int finishedCount = learningRecordRepository.countByUserIdAndCourseIdAndStatus(sc.getUserId(), courseId, 1);
            int progress = (totalChapters == 0) ? 0 : (finishedCount * 100 / totalChapters);

            Map<String, Object> map = new HashMap<>();
            map.put("studentName", student.getRealName());
            map.put("username", student.getUsername());
            map.put("progress", progress);
            resultList.add(map);
        }
        return resultList;
    }
}