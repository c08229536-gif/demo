package com.education.backend.config;

import com.education.backend.entity.SysLog;
import com.education.backend.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private SysLogService logService;
    @Autowired
    private HttpServletRequest request;

    // 监听所有控制器的所有方法
    @AfterReturning(pointcut = "execution(* com.education.backend.controller.*.*(..))")
    public void logAfterReturning(JoinPoint joinPoint) {
        try {
            // 获取当前登录用户
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) return; // 忽略未登录的匿名操作

            SysLog sysLog = new SysLog();
            sysLog.setUsername(username);
            sysLog.setOperation(joinPoint.getSignature().getName());
            // 记录类全名+方法名
            sysLog.setMethod(joinPoint.getSignature().getDeclaringTypeName());
            sysLog.setParams(Arrays.toString(joinPoint.getArgs()));
            sysLog.setIp(request.getRemoteAddr());
            sysLog.setCreateTime(LocalDateTime.now());
            
            logService.saveLog(sysLog);
        } catch (Exception e) {
            // 日志报错不影响主业务
        }
    }
}