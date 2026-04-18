package com.education.backend.service;

import com.education.backend.entity.SysFeedback;
import java.util.List;

public interface SysFeedbackService {
    void addFeedback(SysFeedback feedback, Integer userId);

    List<SysFeedback> getMyFeedback(Integer userId);

    List<SysFeedback> getAllFeedback();

    void replyFeedback(Integer id, String reply);
}
