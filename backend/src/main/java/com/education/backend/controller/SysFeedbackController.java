package com.education.backend.controller;
import com.education.backend.entity.SysFeedback;
import com.education.backend.entity.User;
import com.education.backend.repository.SysFeedbackRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-feedback")
public class SysFeedbackController {
    @Autowired private SysFeedbackRepository feedbackRepository;
    @Autowired private UserRepository userRepository;

    // 提交反馈
    @PostMapping("/add")
    public String addFeedback(@RequestBody SysFeedback feedback) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        feedback.setUserId(user.getUserId());
        feedbackRepository.save(feedback);
        return "反馈提交成功，管理员会尽快处理！";
    }

    // 我的反馈记录
    @GetMapping("/my")
    public List<SysFeedback> getMyFeedback() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return feedbackRepository.findByUserIdOrderByCreateTimeDesc(user.getUserId());
    }

    // (管理员) 获取所有反馈
    @GetMapping("/all")
    public List<SysFeedback> getAllFeedback() {
        List<SysFeedback> list = feedbackRepository.findAllByOrderByCreateTimeDesc();
        list.forEach(f -> userRepository.findById(f.getUserId())
             .ifPresent(u -> f.setUsername(u.getRealName())));
        return list;
    }

    // (管理员) 回复反馈
    @PostMapping("/reply")
    public String replyFeedback(@RequestBody Map<String, Object> params) {
        Integer id = Integer.valueOf(params.get("id").toString());
        String reply = (String) params.get("reply");
        
        SysFeedback f = feedbackRepository.findById(id).orElseThrow();
        f.setReply(reply);
        f.setStatus("已回复");
        feedbackRepository.save(f);
        return "回复成功";
    }
}