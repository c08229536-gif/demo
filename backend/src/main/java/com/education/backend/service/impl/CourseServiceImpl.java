package com.education.backend.service.impl;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import com.education.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private LearningRecordRepository learningRecordRepository;
    @Autowired
    private CourseReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseProgressRepository progressRepository;

    @Override
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

    @Override
    public Course getCourseById(Integer id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @Override
    public boolean checkEnrollStatus(Integer courseId, Integer userId) {
        return studentCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
    }

    @Override
    public List<Course> getMyCourses(Integer userId) {
        List<StudentCourse> records = studentCourseRepository.findByUserId(userId);
        List<Integer> courseIds = records.stream().map(StudentCourse::getCourseId).collect(Collectors.toList());
        return courseRepository.findAllById(courseIds);
    }

    @Override
    @Transactional
    public void addCourse(Course course) {
        if (course.getTitle() == null || course.getTitle().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        course.setStatus(0);
        courseRepository.save(course);
    }

    @Override
    public List<Chapter> getCourseChapters(Integer courseId) {
        return chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
    }

    @Override
    @Transactional
    public void addChapter(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    @Override
    @Transactional
    public String finishChapter(Integer chapterId, Integer userId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        if (learningRecordRepository.findByUserIdAndChapterId(userId, chapterId).isPresent()) {
            return "已打卡";
        }

        LearningRecord record = new LearningRecord();
        record.setUserId(userId);
        record.setChapterId(chapterId);
        record.setCourseId(chapter.getCourseId());
        record.setStatus(1);
        record.setFinishTime(LocalDateTime.now());
        learningRecordRepository.save(record);
        return "打卡成功！";
    }

    @Override
    public List<Integer> getCourseProgress(Integer courseId, Integer userId) {
        return learningRecordRepository.findByUserIdAndCourseId(userId, courseId)
                .stream().map(LearningRecord::getChapterId).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCourseStudents(Integer courseId) {
        List<StudentCourse> enrollments = studentCourseRepository.findByCourseId(courseId);
        int totalChapters = chapterRepository.countByCourseId(courseId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (StudentCourse sc : enrollments) {
            User student = userRepository.findById(sc.getUserId()).orElse(null);
            if (student == null)
                continue;
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

    @Override
    @Transactional
    public void updateCourseCover(Integer courseId, String coverUrl) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        if (coverUrl == null || coverUrl.isEmpty()) {
            throw new RuntimeException("封面URL不能为空");
        }
        course.setCover(coverUrl);
        courseRepository.save(course);
    }

    @Override
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(0);
    }

    @Override
    @Transactional
    public void auditCourse(Integer courseId, Boolean pass) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setStatus(pass ? 1 : 2);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void updateProgress(CourseProgress p) {
        CourseProgress existing = progressRepository.findByUserIdAndChapterId(p.getUserId(), p.getChapterId())
                .orElse(p);
        existing.setProgressPercent(p.getProgressPercent());
        existing.setIsFinished(p.getProgressPercent() >= 100 ? 1 : 0);
        progressRepository.save(existing);
    }
}
