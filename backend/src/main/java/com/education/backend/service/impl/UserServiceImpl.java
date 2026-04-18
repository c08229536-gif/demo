package com.education.backend.service.impl;

import com.education.backend.entity.Role;
import com.education.backend.entity.User;
import com.education.backend.repository.RoleRepository;
import com.education.backend.repository.UserRepository;
import com.education.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void addUser(User request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setRealName(request.getRealName());

        Role role = roleRepository.findByRoleName(request.getRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("错误：找不到角色: " + request.getRole()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);
        newUser.setRole(request.getRole());

        newUser.setPassword(passwordEncoder.encode("123456"));
        newUser.setFirstLogin(true);

        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void batchAddUsers(List<User> users) {
        Role studentRole = roleRepository.findByRoleName("STUDENT")
                .orElseThrow(() -> new RuntimeException("角色不存在: STUDENT"));
        Role teacherRole = roleRepository.findByRoleName("TEACHER")
                .orElseThrow(() -> new RuntimeException("角色不存在: TEACHER"));
        List<String> existingUsernames = userRepository.findAll().stream().map(User::getUsername)
                .collect(Collectors.toList());

        for (User userRequest : users) {
            if (existingUsernames.contains(userRequest.getUsername())) {
                continue;
            }

            User newUser = new User();
            newUser.setUsername(userRequest.getUsername());
            newUser.setRealName(userRequest.getRealName());
            newUser.setPassword(passwordEncoder.encode("123456"));
            newUser.setFirstLogin(true);

            Set<Role> roles = new HashSet<>();
            if ("student".equalsIgnoreCase(userRequest.getRole())) {
                roles.add(studentRole);
            } else if ("teacher".equalsIgnoreCase(userRequest.getRole())) {
                roles.add(teacherRole);
            } else {
                continue;
            }
            newUser.setRoles(roles);
            newUser.setRole(userRequest.getRole());

            userRepository.save(newUser);
        }
    }

    @Override
    @Transactional
    public void resetPassword(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        user.setPassword(passwordEncoder.encode("123456"));
        user.setFirstLogin(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void fixRoles() {
        List<User> allUsers = userRepository.findAll();
        Role studentRole = roleRepository.findByRoleName("STUDENT").orElseThrow();
        Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow();
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();

        for (User user : allUsers) {
            if (user.getRole() != null) {
                Set<Role> roles = new HashSet<>();
                switch (user.getRole().toLowerCase()) {
                    case "student":
                        roles.add(studentRole);
                        break;
                    case "teacher":
                        roles.add(teacherRole);
                        break;
                    case "admin":
                        roles.add(adminRole);
                        break;
                }
                if (!roles.isEmpty()) {
                    user.setRoles(roles);
                    userRepository.save(user);
                }
            }
        }
    }

    @Override
    @Transactional
    public void recharge(Integer userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional
    public void addUser(String username, String realName, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setRole(roleName);
        user.setPassword(passwordEncoder.encode("123456"));
        Role role = roleRepository.findByRoleName(roleName.toUpperCase()).orElseThrow();
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Integer userId, String newRoleName) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(newRoleName);
        Role role = roleRepository.findByRoleName(newRoleName.toUpperCase()).orElseThrow();
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);
    }
}
