package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.request.*;
import com.java_spring_boot.first_demo.dto.response.*;
import com.java_spring_boot.first_demo.service.impl.AuthService;
import com.java_spring_boot.first_demo.service.impl.OAuth2Service;
import com.java_spring_boot.first_demo.service.impl.OtpService;
import com.java_spring_boot.first_demo.service.impl.RefreshTokenService;
import com.java_spring_boot.first_demo.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

        private final AuthService authService;
        private final RefreshTokenService refreshTokenService;
        private final OAuth2Service oAuth2Service;
        private final OtpService otpService;
        private final JwtUtil jwtUtil;

        @org.springframework.beans.factory.annotation.Value("${FRONTEND_URL:http://localhost:3000}")
        private String frontendUrl;

        private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, int maxAge) {
                String cookie = "refresh_token=" + refreshToken +
                                "; HttpOnly" +
                                "; Secure" +
                                "; Path=/api/v1/auth" +
                                "; Max-Age=" + maxAge +
                                "; SameSite=None";

                response.setHeader("Set-Cookie", cookie);
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
                        HttpServletResponse response) {
                LoginResponse loginResponse = authService.login(loginRequest);
                String refreshToken = loginResponse.getRefreshToken();
                setRefreshTokenCookie(response, refreshToken, 7 * 24 * 60 * 60);
                loginResponse.setRefreshToken(null);

                return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                                .data(loginResponse)
                                .message("Login Successful")
                                .statusCode(HttpStatus.OK.value())
                                .build());
        }

        @PostMapping("/register")
        public ResponseEntity<ApiResponse<RegisterResponse>> register(
                        @Valid @RequestBody RegisterRequest registerRequest) {
                return ResponseEntity.ok(ApiResponse.<RegisterResponse>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .data(authService.register(registerRequest))
                                .message("Register Successful")
                                .build());
        }

        @PostMapping("/google")
        public ResponseEntity<ApiResponse<LoginResponse>> loginGoogle(@RequestBody Map<String, String> body,
                        HttpServletResponse response) {
                String code = body.get("code");
                String redirectUri = body.get("redirect_uri");
                LoginResponse loginResponse = oAuth2Service.handleGoogleLogin(code, redirectUri);
                String refreshToken = loginResponse.getRefreshToken();
                setRefreshTokenCookie(response, refreshToken, 7 * 24 * 60 * 60);
                loginResponse.setRefreshToken(null);

                return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                                .message("Login Successful")
                                .data(loginResponse)
                                .statusCode(HttpStatus.OK.value())
                                .build());
        }

        @PostMapping("/facebook")
        public ResponseEntity<ApiResponse<LoginResponse>> loginFacebook(@RequestBody Map<String, String> body,
                        HttpServletResponse response) {
                String code = body.get("code");
                String redirectUri = body.get("redirect_uri");
                LoginResponse loginResponse = oAuth2Service.handleFacebookLogin(code, redirectUri);
                String refreshToken = loginResponse.getRefreshToken();
                setRefreshTokenCookie(response, refreshToken, 7 * 24 * 60 * 60);
                loginResponse.setRefreshToken(null);

                return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                                .message("Login Successful")
                                .data(loginResponse)
                                .statusCode(HttpStatus.OK.value())
                                .build());
        }

        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<RefreshReponse>> refresh(@CookieValue("refresh_token") String refreshToken,
                        HttpServletResponse response) {
                RefreshReponse refreshResponse = refreshTokenService.handleRefresh(refreshToken);
                // If RefreshTokenService returns a NEW refreshToken, set it in cookie here.
                // For now, we assume access token only in response.
                return ResponseEntity.ok(ApiResponse.<RefreshReponse>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Refresh Successful")
                                .data(refreshResponse)
                                .build());
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Void>> logout(
                        @CookieValue("refresh_token") String refreshToken,
                        HttpServletResponse response) {
                log.info("Logout request with token from cookie");
                refreshTokenService.revokedRefreshToken(refreshToken);
                setRefreshTokenCookie(response, null, 0);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Logout Successful")
                                .build());
        }

        @PostMapping("/forgot-password")
        public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request)
                        throws MessagingException {
                otpService.sendOtp(request.getEmail());
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .message("Forgot Password Successful")
                                .statusCode(HttpStatus.OK.value())
                                .build());
        }

        @PostMapping("/verify-otp")
        public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOTP(
                        @RequestBody VerifyOTPRequest request) {
                return ResponseEntity.ok(ApiResponse.<VerifyOtpResponse>builder()
                                .data(otpService.verifyOtp(request.getEmail(), request.getOtp()))
                                .message("Verify OTP Successful")
                                .statusCode(HttpStatus.OK.value())
                                .build());
        }

        @PostMapping("/reset-password")
        public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
                authService.resetPassword(request.getEmail(), request.getResetToken(), request.getNewPassword());
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Reset Password Successful")
                                .build());
        }

        @GetMapping("/verify")
        public ResponseEntity<Void> verify(@RequestParam String token) {
                String primaryFrontendUrl = frontendUrl.contains(",") ? frontendUrl.split(",")[0] : frontendUrl;
                try {
                        authService.activeUser(token);
                        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                                        .location(URI.create(primaryFrontendUrl + "/login?verified=true"))
                                        .build();
                } catch (Exception e) {
                        log.error("Verification failed: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                                        .location(URI.create(primaryFrontendUrl + "/login?error=verification_failed"))
                                        .build();
                }
        }

        @PostMapping("/resend-verification")
        public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
                authService.resendVerification(email);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Verification email has been sent")
                                .build());
        }
}
