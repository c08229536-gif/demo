package com.education.backend.controller;

import com.education.backend.entity.Course;
import com.education.backend.entity.StudentCourse;
import com.education.backend.entity.User;
import com.education.backend.repository.CourseRepository;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.UserRepository;
import com.education.backend.entity.Chapter;
import com.education.backend.repository.ChapterRepository;
import com.education.backend.entity.LearningRecord;
import com.education.backend.repository.LearningRecordRepository;
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
@RequestMapping("/course")
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

    
    

    // === 2. 根据 ID 查询课程详情 ===
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    // === 3. 加入课程接口 ===
    @PostMapping("/enroll/{courseId}")
    public String enrollCourse(@PathVariable Integer courseId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录或不存在"));

        if (studentCourseRepository.findByUserIdAndCourseId(user.getUserId(), courseId).isPresent()) {
            return "您已经加入过这门课了！";
        }

        StudentCourse record = new StudentCourse();
        record.setUserId(user.getUserId());
        record.setCourseId(courseId);
        record.setEnrollTime(LocalDateTime.now());

        studentCourseRepository.save(record);
        return "加入成功！开启学习之旅吧！";
    }

    // === 4. 获取我的课程 ===
    @GetMapping("/my-courses")
    public List<Course> getMyCourses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录"));

        List<StudentCourse> records = studentCourseRepository.findByUserId(user.getUserId());
        List<Integer> courseIds = records.stream()
                .map(StudentCourse::getCourseId)
                .toList();

        return courseRepository.findAllById(courseIds);
    }

    // === 5. 发布课程 (修改：默认为待审核 status=0) ===
    @PostMapping("/add")
    public String addCourse(@RequestBody Course course) {
        if (course.getTitle() == null || course.getTitle().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        
        if (course.getCover() == null || course.getCover().isEmpty()) {
            course.setCover("https://placeholder.co/300x200/808080/ffffff?text=No+Cover");
        }

        // 🟢 关键修改：强制设为 0 (待审核)，需要管理员审核通过后才能上架
        course.setStatus(0);

        courseRepository.save(course);
        return "课程提交成功！请等待管理员审核。";
    }

    // === 6. 获取章节列表 ===
    @GetMapping("/{courseId}/chapters")
    public List<Chapter> getCourseChapters(@PathVariable Integer courseId) {
        return chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
    }

    // === 7. 添加章节 ===
    @PostMapping("/chapter/add")
    public String addChapter(@RequestBody Chapter chapter) {
        if (chapter.getCourseId() == null) {
            throw new RuntimeException("必须指定课程ID");
        }
        chapterRepository.save(chapter);
        return "章节添加成功！";
    }

    // === 8. 标记章节学完 ===
    @PostMapping("/chapter/{chapterId}/finish")
    public String finishChapter(@PathVariable Integer chapterId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        Chapter chapter = chapterRepository.findById(chapterId)
            .orElseThrow(() -> new RuntimeException("章节不存在"));

        Optional<LearningRecord> existing = learningRecordRepository.findByUserIdAndChapterId(user.getUserId(), chapterId);
        if (existing.isPresent()) {
            return "已记录，无需重复";
        }

        LearningRecord record = new LearningRecord();
        record.setUserId(user.getUserId());
        record.setChapterId(chapterId);
        record.setCourseId(chapter.getCourseId());
        record.setStatus(1); // 1 表示已完成
        record.setFinishTime(LocalDateTime.now());
        
        learningRecordRepository.save(record);
        return "恭喜！本章学习完成！";
    }

    // === 9. 查询学习进度 ===
    @GetMapping("/{courseId}/progress")
    public List<Integer> getCourseProgress(@PathVariable Integer courseId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<LearningRecord> records = learningRecordRepository.findByUserIdAndCourseId(user.getUserId(), courseId);
        
        return records.stream()
                .map(LearningRecord::getChapterId)
                .collect(Collectors.toList());
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
            map.put("studentId", student.getUserId());
            map.put("studentName", student.getRealName());
            map.put("username", student.getUsername());
            map.put("enrollTime", sc.getEnrollTime());
            map.put("progress", progress);
            
            resultList.add(map);
        }
        return resultList;
    }
    // === 1. 查询所有课程接口 (修改：只返回已发布 status=1 的课程，且包含评分) ===
    @GetMapping("/list")
    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findByStatus(1);
        
        // 计算每一门课的平均分
        for (Course c : courses) {
            List<com.education.backend.entity.CourseReview> reviews = reviewRepository.findByCourseIdOrderByCreateTimeDesc(c.getCourseId());
            if (reviews.isEmpty()) {
                c.setRating(0.0); 
            } else {
                double avg = reviews.stream().mapToInt(com.education.backend.entity.CourseReview::getRating).average().orElse(0.0);
                // 保留1位小数
                c.setRating(Math.round(avg * 10.0) / 10.0);
            }
        }
        return courses;
    }
}