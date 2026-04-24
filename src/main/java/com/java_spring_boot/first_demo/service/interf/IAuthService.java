package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.request.LoginRequest;
import com.java_spring_boot.first_demo.dto.request.RegisterRequest;
import com.java_spring_boot.first_demo.dto.response.ForgotPasswordResponse;
import com.java_spring_boot.first_demo.dto.response.LoginResponse;
import com.java_spring_boot.first_demo.dto.response.RegisterResponse;

public interface IAuthService {

    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    void resendVerification(String email);

    // ForgotPasswordResponse forgotPassword(String email);
}
