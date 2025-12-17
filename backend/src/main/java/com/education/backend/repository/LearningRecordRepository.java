package com.education.backend.repository;

import com.education.backend.entity.LearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LearningRecordRepository extends JpaRepository<LearningRecord, Integer> {
    // 查某人某章的学习记录
    Optional<LearningRecord> findByUserIdAndChapterId(Integer userId, Integer chapterId);
    
    // 查某人某门课的所有学习记录 (用来前端回显哪些章节已学)
    List<LearningRecord> findByUserIdAndCourseId(Integer userId, Integer courseId);
    // 👇 新增：统计某学生在某门课里，状态为1(已完成)的记录数
    int countByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, Integer status);
}
