package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.request.LoginRequest;
import com.java_spring_boot.first_demo.dto.request.RegisterRequest;
import com.java_spring_boot.first_demo.dto.response.LoginResponse;
import com.java_spring_boot.first_demo.dto.response.RegisterResponse;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.AuthException;
import com.java_spring_boot.first_demo.exception.InvalidException;
import com.java_spring_boot.first_demo.exception.InvalidTokenException;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.UserRepository;
import com.java_spring_boot.first_demo.service.interf.IAuthService;
import com.java_spring_boot.first_demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Audit(action = "LOGIN", entity = "Auth", logRequest = true)
    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (!user.isPresent()) {
            log.error("Login fail, user not found with email {}", loginRequest.getEmail());
            throw new AuthException("Login fail, user not found with email");
        }

        boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.get().getPasswordHash());
        if (!matches) {
            log.error("Login fail, user password invalid");
            throw new AuthException("Login fail, user password invalid");
        }

        if (user.get().getIsActive().equals(false)) {
            log.warn("Login fail, user active is false");
            throw new AuthException("Login fail, user active is false");
        }

        String accessToken = jwtUtil.generateAccessToken(loginRequest.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.get());

        return LoginResponse.builder()
                .userId(user.get().getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userName(user.get().getFullName())
                .email(loginRequest.getEmail())
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        Optional<User> user = userRepository.findByEmail(registerRequest.getEmail());

        if (user.isPresent()) {
            log.error("Register fail, user already exists");
            throw new AuthException("User email already exists");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            log.error("Register fail, confirm password not match");
            throw new AuthException("Register fail, confirm password not match");
        }

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(false)
                .build();
        log.info("Register success before active by email {}", newUser);
        userRepository.save(newUser);

        String verifyToken = jwtUtil.generateVerifyToken(newUser.getEmail());
        emailService.sendActiveAccountMail(newUser.getEmail(), verifyToken);
        return RegisterResponse.builder()
                .email(newUser.getEmail())
                .fullName(newUser.getFullName())
                .build();
    }

    @Audit(action = "RESET_PASSWORD", entity = "Auth")
    public void resetPassword(String email, String resetToken, String newPassword) {
        if (!otpService.validateResetToken(email, resetToken)) {
            throw new InvalidException("Invalid or expired reset token");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void activeUser(String token) {
        if (!jwtUtil.validateVerifyToken(token)) {
            log.error("Invalid token");
            throw new InvalidTokenException("Invalid verify token");
        }
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getIsActive()) {
            throw new AuthException("User is already verified");
        }

        String verifyToken = jwtUtil.generateVerifyToken(user.getEmail());
        emailService.sendActiveAccountMail(user.getEmail(), verifyToken);
        log.info("Resend verification success for email {}", email);
    }

}
