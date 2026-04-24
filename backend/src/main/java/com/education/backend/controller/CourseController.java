package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.service.CourseService;
import com.education.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/{id}/is-enrolled")
    public boolean checkEnrollStatus(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return courseService.checkEnrollStatus(id, user.getUserId());
    }

    @GetMapping("/my-courses")
    public List<Course> getMyCourses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return courseService.getMyCourses(user.getUserId());
    }

    @PostMapping("/add")
    public String addCourse(@RequestBody Course course) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        
        course.setTeacherId(user.getUserId());
        course.setTeacher(user.getRealName() != null ? user.getRealName() : user.getUsername());
        
        courseService.addCourse(course);
        return "课程提交成功！请等待审核。";
    }

    @GetMapping("/{courseId}/chapters")
    public List<Chapter> getCourseChapters(@PathVariable Integer courseId) {
        return courseService.getCourseChapters(courseId);
    }

    @PostMapping("/chapter/add")
    public String addChapter(@RequestBody Chapter chapter) {
        courseService.addChapter(chapter);
        return "章节添加成功！";
    }

    @PostMapping("/chapter/{chapterId}/finish")
    public String finishChapter(@PathVariable Integer chapterId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return courseService.finishChapter(chapterId, user.getUserId());
    }

    @GetMapping("/{courseId}/progress")
    public List<Integer> getCourseProgress(@PathVariable Integer courseId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return courseService.getCourseProgress(courseId, user.getUserId());
    }

    @GetMapping("/{courseId}/students")
    public List<Map<String, Object>> getCourseStudents(@PathVariable Integer courseId) {
        return courseService.getCourseStudents(courseId);
    }

    @PostMapping("/{courseId}/update-cover")
    public String updateCourseCover(@PathVariable Integer courseId, @RequestBody Map<String, String> payload) {
        courseService.updateCourseCover(courseId, payload.get("coverUrl"));
        return "封面更新成功！";
    }

    @PostMapping("/progress")
    public String saveProgress(@RequestBody CourseProgress progress) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        progress.setUserId(Long.valueOf(user.getUserId()));
        courseService.updateProgress(progress);
        return "进度保存成功";
    }

    @GetMapping("/chapter/{chapterId}/progress")
    public CourseProgress getChapterProgress(@PathVariable Integer chapterId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return courseService.getChapterProgress(user.getUserId(), chapterId);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Integer id) {
        // 权限检查
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Course course = courseService.getCourseById(id);
        
        if (!"admin".equals(user.getRole()) && !user.getUserId().equals(course.getTeacherId())) {
            throw new RuntimeException("没有权限删除此课程");
        }
        
        courseService.deleteCourse(id);
        return "课程删除成功！";
    }
}