package com.education.backend.repository;

import com.education.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    // 暂时不需要写代码，JPA 自带了 findAll() 方法
}