package com.education.backend.repository;

import com.education.backend.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Integer> {
    List<CourseReview> findByCourseIdOrderByCreateTimeDesc(Integer courseId);
    
    // 查某学生是否评价过某课 (防止刷分)
    boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId);
}