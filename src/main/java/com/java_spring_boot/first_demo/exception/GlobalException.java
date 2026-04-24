package com.java_spring_boot.first_demo.exception;

import com.cloudinary.Api;
import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(RateLimitRedisException.class)
    public ResponseEntity<ApiResponse<Object>> handleRateLimitRedisException(RateLimitRedisException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.builder()
                        .statusCode(429)
                        .message(e.getMessage())
                        .build());
    }

    public ResponseEntity<ApiResponse<Object>> handleOtpException(OtpException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .statusCode(500)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthException(AuthException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .statusCode(401)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .statusCode(404)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidException(InvalidException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .statusCode(400)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .statusCode(400)
                        .message("INVALID REFRESH TOKEN")
                        .build());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<?> handleExpired() {
        return ResponseEntity.status(401).body("REFRESH_TOKEN_EXPIRED");
    }

    @ExceptionHandler(RefreshTokenRevokedException.class)
    public ResponseEntity<?> handleRevoked() {
        return ResponseEntity.status(401).body("REFRESH_TOKEN_REVOKED");
    }


    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLockException(OptimisticLockException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder()
                        .statusCode(409)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .statusCode(500)
                        .message(e.getMessage())
                        .build());
    }
}
