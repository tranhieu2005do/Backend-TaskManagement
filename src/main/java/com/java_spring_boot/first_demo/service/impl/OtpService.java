package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.response.VerifyOtpResponse;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.OtpException;
import com.java_spring_boot.first_demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository  userRepository;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_TTL_MINUTES = 5;          // OTP tồn tại trong 5 phút
    private static final int OTP_RATE_LIMIT_SECONDS = 30;  // 30s mới được gửi lại
    private static final int OTP_MAX_ATTEMPTS = 5;// giới hạn số lần thử

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void sendOtp(String email) throws MessagingException {
        // check user có tồn tại với email, và có còn active
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent() || optionalUser.get().getIsActive().equals(false)){
            log.error("Account is not existed or your account has been inactive");
            throw new OtpException("Account is not existed or your account has been inactive");
        }
        String rateLimitKey = "otp_request:" + email;
        if (redisTemplate.hasKey(rateLimitKey)) {
            throw new RuntimeException("Please wait before requesting another OTP");
        }

        String otp = generateOtp();
        String otpKey = "otp:" + email;
        String hashedOtp = passwordEncoder.encode(otp);

        // lưu OTP
        redisTemplate.opsForValue().set(otpKey, hashedOtp, Duration.ofMinutes(OTP_TTL_MINUTES));

        // reset attempt
        redisTemplate.delete("otp_attempt:" + email);

        // set rate limit key
        redisTemplate.opsForValue().set(rateLimitKey, "1", Duration.ofSeconds(OTP_RATE_LIMIT_SECONDS));

        // gửi mail
        emailService.sendOtp(email, otp);
        log.info("OTP {} sent to email {}", otp, email);
    }

    public VerifyOtpResponse verifyOtp(String email, String otpInput) {
        log.info("Verifying OTP {} received from email {}", otpInput, email);
        String otpKey = "otp:" + email;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        if (storedOtp == null) {
            log.info("OTP expired or not found for {}", email);
            throw new OtpException("OTP expired or not found for " + email);
        }

        String attemptKey = "otp_attempt:" + email;
        Integer attempts = Optional.ofNullable(redisTemplate.opsForValue().get(attemptKey))
                .map(Integer::valueOf)
                .orElse(0);

        if (!passwordEncoder.matches(otpInput, storedOtp)) {
            attempts++;
            redisTemplate.opsForValue().set(attemptKey, String.valueOf(attempts),
                    Duration.ofMinutes(OTP_TTL_MINUTES)); // reset cùng TTL OTP
            log.info("OTP attempt {} failed for {}", attempts, email);

            if (attempts >= OTP_MAX_ATTEMPTS) {
                redisTemplate.delete(otpKey); // vượt quá số lần thử → xóa OTP
                redisTemplate.delete(attemptKey);
                log.info("OTP deleted due to max attempts for {}", email);
            }
            throw new OtpException("OTP expired or not found for " + email);
        }

        // OTP đúng → xóa khỏi Redis
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptKey);
        log.info("OTP verified successfully for {}", email);
        return VerifyOtpResponse.builder()
                .resetToken(createResetToken(email))
                .build();
    }

    public String createResetToken(String email) {
        log.info("Creating reset token for {}", email);
        String resetToken = UUID.randomUUID().toString();
        String key = "reset_token:" + email;
        // token tồn tại 10 phút
        redisTemplate.opsForValue().set(key, resetToken, Duration.ofMinutes(10));
        return resetToken;
    }

    public boolean validateResetToken(String email, String token) {
        log.info("Validating reset token for {}", email);
        String key = "reset_token:" + email;
        String storedToken = redisTemplate.opsForValue().get(key);
        if (storedToken == null || !storedToken.equals(token)) {
            return false;
        }
        // xóa token ngay khi dùng
        redisTemplate.delete(key);
        return true;
    }
}
