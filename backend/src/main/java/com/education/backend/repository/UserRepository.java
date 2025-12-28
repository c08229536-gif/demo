package com.education.backend.repository;

import com.education.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 自动生成查询方法：通过用户名查找用户
    Optional<User> findByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.firstLogin = false WHERE u.username = :username")
    void updatePassword(@Param("username") String username, @Param("password") String password);
}