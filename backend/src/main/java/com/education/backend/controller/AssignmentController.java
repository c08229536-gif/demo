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

    @PostMapping("/add")
    public String addAssignment(@RequestBody Assignment assignment) {
        // 1. 简单校验
        if (assignment.getCourseId() == null) {
            throw new RuntimeException("必须指定所属课程");
        }
        if (assignment.getTitle() == null || assignment.getTitle().isEmpty()) {
            throw new RuntimeException("作业标题不能为空");
        }

        // 2. 设定默认值 (数据库里的 status 字段如果是指作业状态，可以设为'进行中')
        // 注意：如果是指学生的提交状态，这里不需要设，因为那是动态计算的
        // 这里我们仅仅保存作业的基本信息
        
        assignmentRepository.save(assignment);
        return "作业发布成功！学生现在可以看到并提交了。";
    }

    // === 1. 查询我的作业列表 (升级版：包含状态和分数) ===
   @GetMapping("/my-list")
    public List<Assignment> getMyAssignments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // 🔴 修复点：这里原来用的是 findById，必须改为 findByUserId
        // 这样才能查出这个学生(user_id)选的所有课程
        List<StudentCourse> records = studentCourseRepository.findByUserId(user.getUserId());
        
        if (records.isEmpty()) {
            return new ArrayList<>(); // 如果没选课，直接返回空列表
        }

        // 2. 提取课程ID列表
        List<Integer> courseIds = records.stream()
                .map(StudentCourse::getCourseId)
                .toList();

        // 3. 根据课程ID找作业
        List<Assignment> assignments = assignmentRepository.findByCourseIdIn(courseIds);

        // 4. 遍历作业，检查提交状态和分数
        for (Assignment task : assignments) {
            Optional<HomeworkSubmission> submissionOpt = submissionRepository
                    .findByStudentIdAndAssignmentId(user.getUserId(), task.getId());
            
            if (submissionOpt.isPresent()) {
                task.setStatus("已提交");
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
    @GetMapping("/all")
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
}