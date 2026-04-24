package com.education.backend.service.impl;

import com.education.backend.entity.SysMessage;
import com.education.backend.repository.SysMessageRepository;
import com.education.backend.repository.UserRepository;
import com.education.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private SysMessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<SysMessage> getMyMessages(Integer userId) {
        return messageRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public int getUnreadCount(Integer userId) {
        return messageRepository.countByUserIdAndIsRead(userId, 0);
    }

    @Override
    @Transactional
    public void markAsRead(Integer id) {
        messageRepository.findById(id).ifPresent(msg -> {
            msg.setIsRead(1);
            messageRepository.save(msg);
        });
    }

    @Override
    @Transactional
    public void sendToAll(String title, String content) {
        userRepository.findAll().forEach(user -> {
            SysMessage msg = new SysMessage();
            msg.setUserId(user.getUserId());
            msg.setTitle(title);
            msg.setContent(content);
            msg.setIsRead(0);
            messageRepository.save(msg);
        });
    }
}
