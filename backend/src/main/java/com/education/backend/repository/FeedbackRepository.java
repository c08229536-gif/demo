package com.education.backend.repository;

import com.education.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    // 查某门课的所有问答，按时间倒序（最新的在前面）
    List<Feedback> findByCourseIdOrderByCreateTimeDesc(Integer courseId);
}
