package com.education.backend.repository;

import com.education.backend.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    // 查某门课的所有章节，并按顺序排列
    List<Chapter> findByCourseIdOrderBySortOrderAsc(Integer courseId);
    // 👇 新增：统计某门课有多少章节
    int countByCourseId(Integer courseId);
}
