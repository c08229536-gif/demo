package com.education.backend.repository;

import com.education.backend.entity.HomeworkSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<HomeworkSubmission, Integer> {
    
    // 保留这一行即可
    Optional<HomeworkSubmission> findByStudentIdAndAssignmentId(Integer studentId, Integer assignmentId);

    List<HomeworkSubmission> findByAssignmentId(Integer assignmentId);

    
}