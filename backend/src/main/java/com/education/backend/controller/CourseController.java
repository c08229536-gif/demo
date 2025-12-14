package com.education.backend.controller;

import com.education.backend.entity.Course;
import com.education.backend.entity.StudentCourse;
import com.education.backend.entity.User;
import com.education.backend.repository.CourseRepository;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private UserRepository userRepository;

    // === 1. 查询所有课程接口 ===
    @GetMapping("/list")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // === 2. 根据 ID 查询课程详情 ===
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    // === 3. 加入课程接口 (新增) ===
    @PostMapping("/enroll/{courseId}")
    public String enrollCourse(@PathVariable Integer courseId) {
        // 1. 获取当前登录用户的用户名 (Spring Security 的魔法)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. 查出用户实体，获取 User ID
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录或不存在"));

        // 3. 检查是否已经选过这门课 (防止重复点击)
        if (studentCourseRepository.findByUserIdAndCourseId(user.getUserId(), courseId).isPresent()) {
            return "您已经加入过这门课了！";
        }

        // 4. 保存选课记录到数据库
        StudentCourse record = new StudentCourse();
        record.setUserId(user.getUserId());
        record.setCourseId(courseId);
        record.setEnrollTime(LocalDateTime.now());

        studentCourseRepository.save(record);
        return "加入成功！开启学习之旅吧！";
    }
    @GetMapping("/my-courses")
    public List<Course> getMyCourses() {
        // 1. 获取当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录"));

        // 2. 查出他在 student_course 表里的所有记录
        List<StudentCourse> records = studentCourseRepository.findByUserId(user.getUserId());

        // 3. 提取出所有的 courseId (比如他选了课 1 和 3，这里就拿到 [1, 3])
        List<Integer> courseIds = records.stream()
                .map(StudentCourse::getCourseId)
                .toList();

        // 4. 去 course 表里把这些课的详情全查出来
        return courseRepository.findAllById(courseIds);
    }
    @PostMapping("/add")
    public String addCourse(@RequestBody Course course) {
        // 简单校验
        if (course.getTitle() == null || course.getTitle().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        
        // 如果没填封面，给个默认图
        if (course.getCover() == null || course.getCover().isEmpty()) {
            course.setCover("https://placeholder.co/300x200/808080/ffffff?text=No+Cover");
        }

        courseRepository.save(course);
        return "课程发布成功！";
    }
}