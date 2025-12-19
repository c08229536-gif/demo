package com.education.backend.repository;
import com.education.backend.entity.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    Optional<CourseProgress> findByUserIdAndChapterId(Long userId, Long chapterId);
}
