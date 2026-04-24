package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.request.ChangePasswordRequest;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.InvalidException;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.UserRepository;
import com.java_spring_boot.first_demo.service.interf.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Audit(action = "CHANGE_USERNAME", entity = "USER", logRequest = true)
    @PreAuthorize("@authService.isCurrentUser(#userId)")
    @Override
    public void changeUserName(String newUserName, Long userId) {
        log.info("Changing name for user with id {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setFullName(newUserName);
        userRepository.save(user);
    }

    @Audit(action = "CHANGE_PASSWORD", entity = "USER", logRequest = true)
    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    @Override
    public void changePassword(ChangePasswordRequest request, Long userId) {
        log.info("Changing password for user with id {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())){
            throw new InvalidException("Invalid old password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
