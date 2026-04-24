package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.response.RefreshReponse;
import com.java_spring_boot.first_demo.entity.RefreshToken;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.InvalidTokenException;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.exception.RefreshTokenExpiredException;
import com.java_spring_boot.first_demo.exception.RefreshTokenRevokedException;
import com.java_spring_boot.first_demo.repository.RefreshTokenRepository;
import com.java_spring_boot.first_demo.service.interf.IRefreshTokenService;
import com.java_spring_boot.first_demo.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Audit(action = "CREATE_REFRESH_TOKEN", entity = "RefreshToken")
    @Override
    public String createRefreshToken(User user) {
        log.info("=== CREATE REFRESH TOKEN CALLED ===");
        log.info("User: {}, Stack trace:", user.getEmail());
//        Thread.dumpStack();
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken  newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshToken);
        return refreshToken;
    }

    @Audit(action = "REQUIRED_NEW_ACCESS_TOKEN", entity = "RefreshToken")
    @Override
    public RefreshReponse handleRefresh(String token) {
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository.findByToken(token);
        if(!existRefreshToken.isPresent()) {
            log.error("Refresh token: {} is not existed", token);
            throw new InvalidTokenException("Invalid Refresh Token");
        }
        RefreshToken refreshToken = existRefreshToken.get();
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.error("Refresh token is expired");
            throw new RefreshTokenExpiredException("Refresh token is expired");
        }
        if(refreshToken.getRevoked()){
            log.error("Refresh token is already revoked");
            throw new RefreshTokenRevokedException("Refresh token is already revoked");
        }
        User user = refreshToken.getUser();
        if(user == null){
            log.error("Refresh token user is null");
            throw new NotFoundException("Invalid Refresh Token");
        }
        return RefreshReponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getEmail()))
                .build();
    }

    @Audit(action = "LOGOUT", entity = "RefreshToken")
    public void revokedRefreshToken(String token){
        log.info("Revoking refresh token: {}", token);
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Invalid Refresh Token");
        }
        Optional<RefreshToken> optnRefreshToken = refreshTokenRepository.findByToken(token);
        if(!optnRefreshToken.isPresent()){
            log.error("Refresh token {} is not existed", token);
            throw new InvalidTokenException("Invalid Refresh Token");
        }
        RefreshToken refreshToken = optnRefreshToken.get();
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken){

        Cookie cookie = new Cookie("refresh_token",  refreshToken);
        cookie.setPath("/auth/refresh");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);
    }

    @Scheduled(cron = "0 0 0 * * ?") // mỗi ngày 0:00
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}
