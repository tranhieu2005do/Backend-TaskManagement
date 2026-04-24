package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.request.ChangePasswordRequest;

public interface IUserService {

    void changeUserName(String newUserName, Long userId);

    void changePassword(ChangePasswordRequest request, Long userId);
}
