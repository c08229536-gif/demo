package com.education.backend.repository;

import com.education.backend.entity.CourseResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseResourceRepository extends JpaRepository<CourseResource, Integer> {
    List<CourseResource> findByCourseIdOrderByCreateTimeDesc(Integer courseId);
}