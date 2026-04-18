package com.education.backend.controller;

import com.education.backend.entity.SysFeedback;
import com.education.backend.entity.User;
import com.education.backend.service.SysFeedbackService;
import com.education.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-feedback")
public class SysFeedbackController {

    @Autowired
    private SysFeedbackService feedbackService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String addFeedback(@RequestBody SysFeedback feedback) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        feedbackService.addFeedback(feedback, user.getUserId());
        return "反馈提交成功，管理员会尽快处理！";
    }

    @GetMapping("/my")
    public List<SysFeedback> getMyFeedback() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return feedbackService.getMyFeedback(user.getUserId());
    }

    @GetMapping("/all")
    public List<SysFeedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }

    @PostMapping("/reply")
    public String replyFeedback(@RequestBody Map<String, Object> params) {
        Integer id = Integer.valueOf(params.get("id").toString());
        String reply = (String) params.get("reply");
        feedbackService.replyFeedback(id, reply);
        return "回复成功";
    }
}