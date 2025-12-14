package com.education.backend.repository;

import com.education.backend.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    // 之前写的查作业列表的方法，保留这个
    List<Assignment> findByCourseIdIn(List<Integer> courseIds);
    

}