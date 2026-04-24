package com.java_spring_boot.first_demo.config.security;

import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthSecurity {

    public Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetail)) {
            return null;
        }

        return ((CustomUserDetail) auth.getPrincipal()).getId();
    }

    public boolean isCurrentUser(Long userId){
        Long currentId = getCurrentUserId();
        return currentId != null && currentId.equals(userId);
    }

    public boolean isAuthenticated() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetail;
    }
}
