package com.education.backend.repository;

import com.education.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 自动生成查询方法：通过用户名查找用户
    Optional<User> findByUsername(String username);
}