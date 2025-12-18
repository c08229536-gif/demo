package com.education.backend.controller;
import com.education.backend.entity.SysMessage;
import com.education.backend.entity.User;
import com.education.backend.repository.SysMessageRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired private SysMessageRepository messageRepository;
    @Autowired private UserRepository userRepository;

    // 获取我的消息列表
    @GetMapping("/my")
    public List<SysMessage> getMyMessages() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return messageRepository.findByUserIdOrderByCreateTimeDesc(user.getUserId());
    }

    // 获取未读数量
    @GetMapping("/unread-count")
    public int getUnreadCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return messageRepository.countByUserIdAndIsRead(user.getUserId(), 0);
    }

    // 标记已读
    @PostMapping("/read/{id}")
    public String markAsRead(@PathVariable Integer id) {
        messageRepository.findById(id).ifPresent(msg -> {
            msg.setIsRead(1);
            messageRepository.save(msg);
        });
        return "success";
    }
}