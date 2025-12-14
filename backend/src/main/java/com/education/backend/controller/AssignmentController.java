package com.education.backend.controller;

import com.education.backend.entity.Assignment;
import com.education.backend.entity.HomeworkSubmission;
import com.education.backend.entity.StudentCourse;
import com.education.backend.entity.User;
import com.education.backend.repository.AssignmentRepository;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.SubmissionRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assignment")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    // === 1. 查询我的作业列表 (升级版：包含状态和分数) ===
    @GetMapping("/my-list")
    public List<Assignment> getMyAssignments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // 1. 找课
        Optional<StudentCourse> records = studentCourseRepository.findById(user.getUserId());
        if (records.isEmpty()) return new ArrayList<>();

        // 2. 找作业
        List<Integer> courseIds = records.stream().map(StudentCourse::getCourseId).toList();
        List<Assignment> assignments = assignmentRepository.findByCourseIdIn(courseIds);

        // 3. 遍历作业，检查提交状态和分数
        for (Assignment task : assignments) {
            // 查提交记录
            Optional<HomeworkSubmission> submissionOpt = submissionRepository
                    .findByStudentIdAndAssignmentId(user.getUserId(), task.getId());
            
            if (submissionOpt.isPresent()) {
                task.setStatus("已提交");
                // 👇 关键修改：如果有提交记录，就把分数取出来给前端
                // (前提是 Assignment 实体类里加了 score 字段)
                task.setScore(submissionOpt.get().getScore());
            } else {
                task.setStatus("待提交");
                task.setScore(null);
            }
        }

        return assignments;
    }

    // === 2. 提交作业接口 ===
    @PostMapping("/submit")
    public String submitHomework(@RequestBody HomeworkSubmission submission) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // 补全信息
        submission.setStudentId(user.getUserId());
        submission.setSubmitTime(LocalDateTime.now());
        
        // 保存到数据库
        submissionRepository.save(submission);
        
        return "作业提交成功！";
    }

    // === 3. (老师用) 查询某次作业的所有提交记录 ===
    @GetMapping("/{assignmentId}/submissions")
    public List<HomeworkSubmission> getSubmissions(@PathVariable Integer assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    // === 4. (老师用) 给作业打分 ===
    @PostMapping("/grade")
    public String gradeSubmission(@RequestBody HomeworkSubmission submission) {
        // 1. 找到这条提交记录
        HomeworkSubmission target = submissionRepository.findById(submission.getId())
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));
        
        // 2. 更新分数
        target.setScore(submission.getScore());
        submissionRepository.save(target);
        
        return "打分成功！";
    }
}