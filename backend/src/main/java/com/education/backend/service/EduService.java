package com.education.backend.service;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class EduService {
    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private SysMessageRepository messageRepo;
    @Autowired private CourseProgressRepository progressRepo;
    @Autowired private ExamRepository examRepo;
    @Autowired private ExamQuestionRepository questionRepo;
    @Autowired private SysLogRepository logRepo;
    @Autowired private HomeBannerRepository bannerRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() { return userRepo.findAll(); }

    @Transactional
    public void addUser(String username, String realName, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setRole(roleName);
        user.setPassword(passwordEncoder.encode("123456"));
        Role role = roleRepo.findByRoleName(roleName.toUpperCase()).orElseThrow();
        user.setRoles(Collections.singleton(role));
        userRepo.save(user);
    }

    @Transactional
    public void updateUserRole(Integer userId, String newRoleName) {
        User user = userRepo.findById(userId).orElseThrow();
        user.setRole(newRoleName);
        Role role = roleRepo.findByRoleName(newRoleName.toUpperCase()).orElseThrow();
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepo.save(user);
    }

    @Transactional
    public void auditCourse(Integer courseId, Boolean pass) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        course.setStatus(pass ? 1 : 2);
        courseRepo.save(course);
    }

    public List<HomeBanner> getBanners() { return bannerRepo.findAll(); }
    public Exam getExam(Integer id) { return examRepo.findById(id).orElse(null); }
    public List<ExamQuestion> getQuestions(Integer examId) { return questionRepo.findByExamId(examId); }

    @Transactional
    public void updateProgress(CourseProgress p) {
        CourseProgress existing = progressRepo.findByUserIdAndChapterId(p.getUserId(), p.getChapterId()).orElse(p);
        existing.setProgressPercent(p.getProgressPercent());
        existing.setIsFinished(p.getProgressPercent() >= 100 ? 1 : 0);
        progressRepo.save(existing);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void saveLog(SysLog log) {
        log.setCreateTime(LocalDateTime.now());
        logRepo.save(log);
    }
}