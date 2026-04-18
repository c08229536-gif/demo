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

@Aspect
@Component
public class LogAspect {
    @Autowired
    private SysLogService logService;
    @Autowired
    private HttpServletRequest request;

    // 👈 修复：切点覆盖 AdminController 的所有方法
    @AfterReturning(pointcut = "execution(* com.education.backend.controller.AdminController.*(..))")
    public void logAfterReturning(JoinPoint joinPoint) {
        try {
            SysLog sysLog = new SysLog();
            sysLog.setUsername("AdminSystem");
            sysLog.setOperation(joinPoint.getSignature().getName());
            sysLog.setMethod(joinPoint.getTarget().getClass().getSimpleName());
            sysLog.setParams(Arrays.toString(joinPoint.getArgs()));
            sysLog.setIp(request.getRemoteAddr());
            logService.saveLog(sysLog);
        } catch (Exception e) {
            System.err.println("日志记录异常: " + e.getMessage());
        }
    }
}