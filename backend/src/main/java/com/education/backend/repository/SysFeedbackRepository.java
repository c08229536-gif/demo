package com.education.backend.repository;
import com.education.backend.entity.SysFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SysFeedbackRepository extends JpaRepository<SysFeedback, Integer> {
    List<SysFeedback> findByUserIdOrderByCreateTimeDesc(Integer userId);
    // 管理员查所有
    List<SysFeedback> findAllByOrderByCreateTimeDesc();
}
