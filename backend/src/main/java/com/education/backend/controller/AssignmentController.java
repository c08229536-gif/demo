package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
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
    
    // 👇 注入消息仓库 (用于发通知)
    @Autowired
    private SysMessageRepository messageRepository;

    // === 发布作业 ===
    @PostMapping("/add")
    public String addAssignment(@RequestBody Assignment assignment) {
        if (assignment.getCourseId() == null) {
            throw new RuntimeException("必须指定所属课程");
        }
        if (assignment.getTitle() == null || assignment.getTitle().isEmpty()) {
            throw new RuntimeException("作业标题不能为空");
        }
        assignmentRepository.save(assignment);
        return "作业发布成功！";
    }

    // === 1. 查询我的作业列表 (包含状态、分数、评语) ===
    @GetMapping("/my-list")
    public List<Assignment> getMyAssignments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<StudentCourse> records = studentCourseRepository.findByUserId(user.getUserId());
        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> courseIds = records.stream()
                .map(StudentCourse::getCourseId)
                .toList();

        List<Assignment> assignments = assignmentRepository.findByCourseIdIn(courseIds);

        for (Assignment task : assignments) {
            Optional<HomeworkSubmission> submissionOpt = submissionRepository
                    .findByStudentIdAndAssignmentId(user.getUserId(), task.getId());
            
            if (submissionOpt.isPresent()) {
                HomeworkSubmission sub = submissionOpt.get();
                
                // 👇 优先使用数据库里的真实状态 (可能是"已批改")
                if (sub.getStatus() != null) {
                    task.setStatus(sub.getStatus());
                } else {
                    task.setStatus("已提交");
                }
                
                // 👇 填充分数和评语
                task.setScore(sub.getScore());
                task.setFeedback(sub.getFeedback()); 
            } else {
                task.setStatus("待提交");
                task.setScore(null);
            }
        }
        return assignments;
    }

    // === 2. 提交作业 ===
    @PostMapping("/submit")
    public String submitHomework(@RequestBody HomeworkSubmission submission) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (submissionRepository.findByStudentIdAndAssignmentId(user.getUserId(), submission.getAssignmentId()).isPresent()) {
             throw new RuntimeException("您已提交过该作业，请勿重复提交");
        }

        submission.setStudentId(user.getUserId());
        submission.setSubmitTime(LocalDateTime.now());
        submission.setStatus("已提交"); // 初始状态
        
        submissionRepository.save(submission);
        return "作业提交成功！";
    }

    // === 3. (老师用) 查询某次作业的所有提交 ===
    @GetMapping("/{assignmentId}/submissions")
    public List<HomeworkSubmission> getSubmissions(@PathVariable Integer assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    // === 4. (老师用) 获取所有待批改作业 ===
    @GetMapping("/teacher-list")
    public List<HomeworkSubmission> getTeacherAssignments() {
        List<HomeworkSubmission> list = submissionRepository.findAll();
        // 填充额外信息供前端显示
        for (HomeworkSubmission s : list) {
            assignmentRepository.findById(s.getAssignmentId())
                .ifPresent(a -> s.setAssignmentTitle(a.getTitle()));
            userRepository.findById(s.getStudentId())
                .ifPresent(u -> s.setStudentName(u.getRealName()));
        }
        return list;
    }

    // === 5. (老师用) 打分 + 评语 + 发通知 ===
    @PostMapping("/grade")
    public String gradeSubmission(@RequestBody HomeworkSubmission submission) {
        HomeworkSubmission target = submissionRepository.findById(submission.getId())
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));
        
        // 👇 更新分数、评语、状态
        target.setScore(submission.getScore());
        target.setFeedback(submission.getFeedback()); // 保存评语
        target.setStatus("已批改"); // 更新状态
        
        submissionRepository.save(target);
        
        // 👇 发送系统消息
        try {
            SysMessage msg = new SysMessage();
            msg.setUserId(target.getStudentId());
            msg.setTitle("作业已批改");
            
            String title = assignmentRepository.findById(target.getAssignmentId())
                    .map(Assignment::getTitle).orElse("作业");
            
            msg.setContent("您的作业《" + title + "》已被老师批改，得分：" + submission.getScore());
            messageRepository.save(msg);
        } catch (Exception e) {
            System.err.println("发送消息失败: " + e.getMessage());
        }
        
        return "批改完成，已通知学生！";
    }

    @GetMapping("/all")
    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = assignmentRepository.findAll();
        for (Assignment a : assignments) {
            // 统计该作业下状态为 "已提交" 的记录数
            int count = submissionRepository.countByAssignmentIdAndStatus(a.getId(), "已提交");
            a.setPendingCount(count);
        }
        return assignments;
    }
}