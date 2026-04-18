package com.education.backend.service;

import com.education.backend.entity.SysMessage;
import java.util.List;

public interface MessageService {
    List<SysMessage> getMyMessages(Integer userId);

    int getUnreadCount(Integer userId);

    void markAsRead(Integer id);
}
