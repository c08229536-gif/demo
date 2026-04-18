package com.education.backend.service;

import com.education.backend.entity.SysLog;
import java.util.List;

public interface SysLogService {
    void saveLog(SysLog log);

    List<SysLog> getLogs();
}
