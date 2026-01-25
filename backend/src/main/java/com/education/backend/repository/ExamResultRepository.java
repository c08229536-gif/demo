package com.education.backend.repository;

import com.education.backend.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Integer> {
    List<ExamResult> findByStudentId(Integer studentId);
    List<ExamResult> findByExamId(Integer examId);
}
