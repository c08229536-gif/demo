package com.education.backend.service;

import com.education.backend.entity.Chapter;
import com.education.backend.entity.Course;
import com.education.backend.entity.CourseProgress;

import java.util.List;
import java.util.Map;

public interface CourseService {
    List<Course> getAllCourses();

    Course getCourseById(Integer id);

    boolean checkEnrollStatus(Integer courseId, Integer userId);

    List<Course> getMyCourses(Integer userId);

    void addCourse(Course course);

    List<Chapter> getCourseChapters(Integer courseId);

    void addChapter(Chapter chapter);

    String finishChapter(Integer chapterId, Integer userId);

    List<Integer> getCourseProgress(Integer courseId, Integer userId);

    List<Map<String, Object>> getCourseStudents(Integer courseId);

    void updateCourseCover(Integer courseId, String coverUrl);

    // from Admin / EduService
    List<Course> getPendingCourses();

    void auditCourse(Integer courseId, Boolean pass);

    // for progress
    void updateProgress(CourseProgress p);

    CourseProgress getChapterProgress(Integer userId, Integer chapterId);

    void deleteCourse(Integer courseId);
}
