package com.education.backend.repository;

import com.education.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    // 暂时不需要写代码，JPA 自带了 findAll() 方法
    // 查某状态的所有课 (给学生看 status=1)
    List<Course> findByStatus(Integer status);
}