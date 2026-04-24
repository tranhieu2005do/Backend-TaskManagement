package com.java_spring_boot.first_demo.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.entity.AuditLog;
import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.repository.AuditLogRepository;
import com.java_spring_boot.first_demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Around("@annotation(audit)")
    public Object handleAudit(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

        String username = auth != null ? auth.getName() : "anonymous";

        AuditLog log = new AuditLog();
        log.setAction(audit.action());
        log.setEntity(audit.entity());
        log.setUserName(username);

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetail user) {
            log.setUserId(user.getId());
        }

        // IP + User-Agent
        log.setIp(getClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));

        // request args
        if (audit.logRequest()) {
            log.setOldValue(toJson(joinPoint.getArgs()));
        }

        // response
        if (audit.logResponse()) {
            log.setNewValue(toJson(result));
        }

        auditLogRepository.save(log);

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader != null) ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }

    private String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return "serialization_error";
        }
    }
}
