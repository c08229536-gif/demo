package com.education.backend.service;

import com.education.backend.entity.User;
import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    List<User> findAllUsers();

    void addUser(User request);

    void batchAddUsers(List<User> users);

    void resetPassword(Integer userId);

    void fixRoles();

    void recharge(Integer userId, BigDecimal amount);

    User findByUsername(String username);

    User findById(Integer id);

    void addUser(String username, String realName, String roleName);

    void updateUserRole(Integer userId, String newRoleName);
}
