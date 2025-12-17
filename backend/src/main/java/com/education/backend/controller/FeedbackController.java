package com.education.backend.controller;

import com.education.backend.entity.Feedback;
import com.education.backend.entity.User;
import com.education.backend.repository.FeedbackRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;

    // 1. 获取某课程的所有问答 (带上学生名字)
    @GetMapping("/course/{courseId}")
    public List<Map<String, Object>> getCourseFeedback(@PathVariable Integer courseId) {
        List<Feedback> list = feedbackRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Feedback f : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("content", f.getContent());
            map.put("reply", f.getReply());
            map.put("createTime", f.getCreateTime());
            
            // 查学生名字
            userRepository.findById(f.getStudentId()).ifPresent(u -> {
                map.put("studentName", u.getRealName());
                map.put("avatar", u.getAvatar());
            });
            
            result.add(map);
        }
        return result;
    }

    // 2. 学生提问
    @PostMapping("/add")
    public String addFeedback(@RequestBody Feedback feedback) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        feedback.setStudentId(user.getUserId());
        feedbackRepository.save(feedback);
        return "提问成功！等待老师回复。";
    }

    // 3. 老师回复
    @PostMapping("/reply")
    public String replyFeedback(@RequestBody Feedback feedback) {
        // 这里前端传 id 和 reply 即可
        Feedback existing = feedbackRepository.findById(feedback.getId())
                .orElseThrow(() -> new RuntimeException("问题不存在"));
        
        existing.setReply(feedback.getReply());
        feedbackRepository.save(existing);
        return "回复成功！";
    }
}