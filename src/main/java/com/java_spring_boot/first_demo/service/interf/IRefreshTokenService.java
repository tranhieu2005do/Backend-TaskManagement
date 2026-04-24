package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.response.RefreshReponse;
import com.java_spring_boot.first_demo.entity.User;

public interface IRefreshTokenService {

    String createRefreshToken(User user);

    RefreshReponse handleRefresh(String token);
}
