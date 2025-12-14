package com.education.backend.repository;

import com.education.backend.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {
    // 检查某人是否已经选过某门课 (防止重复选)
    Optional<StudentCourse> findByUserIdAndCourseId(Integer userId, Integer courseId);
    
    // 查某人选了哪些课
    List<StudentCourse> findByUserId(Integer userId);
}