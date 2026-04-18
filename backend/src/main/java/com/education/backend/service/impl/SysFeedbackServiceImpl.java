package com.education.backend.service.impl;

import com.education.backend.entity.SysFeedback;
import com.education.backend.entity.User;
import com.education.backend.repository.SysFeedbackRepository;
import com.education.backend.repository.UserRepository;
import com.education.backend.service.SysFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysFeedbackServiceImpl implements SysFeedbackService {

    @Autowired
    private SysFeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void addFeedback(SysFeedback feedback, Integer userId) {
        feedback.setUserId(userId);
        feedbackRepository.save(feedback);
    }

    @Override
    public List<SysFeedback> getMyFeedback(Integer userId) {
        return feedbackRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<SysFeedback> getAllFeedback() {
        List<SysFeedback> list = feedbackRepository.findAllByOrderByCreateTimeDesc();
        list.forEach(f -> userRepository.findById(f.getUserId())
                .ifPresent(u -> f.setUsername(u.getRealName())));
        return list;
    }

    @Override
    @Transactional
    public void replyFeedback(Integer id, String reply) {
        SysFeedback f = feedbackRepository.findById(id).orElseThrow();
        f.setReply(reply);
        f.setStatus("已回复");
        feedbackRepository.save(f);
    }
}
