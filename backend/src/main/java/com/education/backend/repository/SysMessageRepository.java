package com.education.backend.repository;
import com.education.backend.entity.SysMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SysMessageRepository extends JpaRepository<SysMessage, Integer> {
    List<SysMessage> findByUserIdOrderByCreateTimeDesc(Integer userId);
    // 统计未读数
    int countByUserIdAndIsRead(Integer userId, Integer isRead);
}
