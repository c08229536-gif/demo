package com.education.backend.controller;

import com.education.backend.entity.SysMessage;
import com.education.backend.entity.User;
import com.education.backend.service.MessageService;
import com.education.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("/my")
    public List<SysMessage> getMyMessages() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return messageService.getMyMessages(user.getUserId());
    }

    @GetMapping("/unread-count")
    public int getUnreadCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return messageService.getUnreadCount(user.getUserId());
    }

    @PostMapping("/read/{id}")
    public String markAsRead(@PathVariable Integer id) {
        messageService.markAsRead(id);
        return "success";
    }

    @PostMapping("/send-all")
    public String sendToAll(@RequestBody SysMessage payload) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        
        if (!"admin".equals(user.getRole())) {
            throw new RuntimeException("只有管理员可以发布公告");
        }
        
        messageService.sendToAll(payload.getTitle(), payload.getContent());
        return "公告发布成功！";
    }
}