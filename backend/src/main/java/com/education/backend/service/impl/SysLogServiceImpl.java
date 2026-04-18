package com.education.backend.service.impl;

import com.education.backend.entity.SysLog;
import com.education.backend.repository.SysLogRepository;
import com.education.backend.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogRepository logRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(SysLog log) {
        log.setCreateTime(LocalDateTime.now());
        logRepository.save(log);
    }

    @Override
    public List<SysLog> getLogs() {
        return logRepository.findAll();
    }
}
