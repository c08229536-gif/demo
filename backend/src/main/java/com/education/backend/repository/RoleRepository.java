package com.education.backend.repository;

import com.education.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // 通过角色名查找，例如 findByRoleName("STUDENT")
    Optional<Role> findByRoleName(String roleName);
}