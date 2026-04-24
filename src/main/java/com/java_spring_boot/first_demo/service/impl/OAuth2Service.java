package com.java_spring_boot.first_demo.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.response.LoginResponse;
import com.java_spring_boot.first_demo.entity.SocialAccount;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.AuthException;
import com.java_spring_boot.first_demo.repository.SocialAccountRepository;
import com.java_spring_boot.first_demo.repository.UserRepository;
import com.java_spring_boot.first_demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.security.oauth2.client.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.google.user-info-uri}")
    private String GOOGLE_USER_INFOR_URI;

    @Value("${spring.security.oauth2.client.facebook.client-id}")
    private String FACEBOOK_CLIENT_ID;

    @Value("${spring.security.oauth2.client.facebook.client-secret}")
    private String FACEBOOK_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.facebook.redirect-uri}")
    private String FACEBOOK_REDIRECT_URI;

    @Audit(action = "GOOGLE_LOGIN", entity = "OAuth2")
    public LoginResponse handleGoogleLogin(String code, String dynamicRedirectUri) {

        log.info("Logging form google account");

        String provider = "google";
        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_CLIENT_ID);
        params.put("client_secret", GOOGLE_CLIENT_SECRET);
        params.put("redirect_uri", dynamicRedirectUri != null && !dynamicRedirectUri.isEmpty() ? dynamicRedirectUri : GOOGLE_REDIRECT_URI);
        params.put("grant_type", "authorization_code");

        try {
            Map<String, Object> response = restTemplate.postForObject(tokenUrl, params, Map.class);

            if (response == null || !response.containsKey("id_token")) {
                throw new AuthException("Failed to get ID token from Google");
            }

            String idToken = (String) response.get("id_token");
            GoogleIdToken.Payload payload = verifyIdToken(idToken);

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleId = payload.getSubject();

            if(!payload.getEmailVerified()){
                log.error("Email verification failed");
                throw new AuthException("Email verification failed");
            }

            Optional<SocialAccount> optionalSocialAccount = socialAccountRepository
                    .findByProviderAndProviderUserId(provider, googleId);
            User user = new User();
            if(optionalSocialAccount.isPresent()){
                log.info("Social account already linked to provider user: {}, " +
                        "they logged in with google before", googleId);
                user = optionalSocialAccount.get().getUser();
                if(user.getIsActive().equals(false)){
                    log.warn("Login fail, user active is false");
                    throw new AuthException("Login fail, user active is false");
                }
            }
            else{
                log.info("Social account is not existed, this is the first time they've logged " +
                        "in with google");
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if(optionalUser.isPresent()){
                    log.info("User logged in by username and password before logged in by google");
                    user = optionalUser.get();
                    if(user.getIsActive().equals(false)){
                        log.warn("Login fail, user active is false");
                        throw new AuthException("Login fail, user active is false");
                    }
                }
                else{
                    log.info("This is the first time they've logged in website, and logged in by google");
                    User newUser = User.builder()
                            .email(email)
                            .passwordHash(null)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .isActive(true)
                            .fullName(name)
                            .build();
                    user =  userRepository.save(newUser);
                }
                SocialAccount socialAccount = SocialAccount.builder()
                        .email(email)
                        .providerUserId(googleId)
                        .user(user)
                        .provider(provider)
                        .createdAt(LocalDateTime.now())
                        .build();
                socialAccountRepository.save(socialAccount);
            }

            String token = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = refreshTokenService.createRefreshToken(user);
            return LoginResponse.builder()
                    .userId(user.getId())
                    .email(email)
                    .userName(user.getFullName())
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("Google login failed", e);
            throw new AuthException("Google login failed: " + e.getMessage());
        }
    }

    public GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new RuntimeException("Invalid ID token");
            }

        } catch (Exception e) {
            throw new RuntimeException("Token verification failed", e);
        }
    }

    @Audit(action = "FACEBOOK_LOGIN", entity = "OAuth2")
    public LoginResponse handleFacebookLogin(String code, String dynamicRedirectUri) {
        log.info("Logging form facebook account");

        String provider = "facebook";
        String redirectUri = dynamicRedirectUri != null && !dynamicRedirectUri.isEmpty() ? dynamicRedirectUri : FACEBOOK_REDIRECT_URI;
        String tokenUrl = "https://graph.facebook.com/v18.0/oauth/access_token?" +
                "client_id=" + FACEBOOK_CLIENT_ID +
                "&client_secret=" + FACEBOOK_CLIENT_SECRET +
                "&redirect_uri=" + redirectUri +
                "&code=" + code;

        try {
            var response = restTemplate.getForObject(tokenUrl, Map.class);
            if (response == null || !response.containsKey("access_token")) {
                throw new AuthException("Failed to get Access token from Facebook");
            }

            String accessToken = (String) response.get("access_token");

            String userInfoUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
            var userInfoResponse = restTemplate.getForObject(userInfoUrl, Map.class);

            if (userInfoResponse == null || !userInfoResponse.containsKey("id")) {
                throw new AuthException("Failed to get User Info from Facebook");
            }

            String facebookId = (String) userInfoResponse.get("id");
            String name = (String) userInfoResponse.get("name");
            String email = (String) userInfoResponse.get("email");

            if (email == null || email.isEmpty()) {
                log.error("Your facebook not linked to an email, you have to linked it");
                throw new AuthException("Your facebook not linked to an email");
            }

            User user = new User();
            Optional<SocialAccount> optionalSocialAccount = socialAccountRepository
                    .findByProviderAndProviderUserId(provider, facebookId);
            if(optionalSocialAccount.isPresent()){
                log.info("Social account already linked to provider user: {}, " +
                        "they logged in with facebook before", facebookId);
                user = optionalSocialAccount.get().getUser();
                if(user.getIsActive().equals(false)){
                    log.warn("Login fail, user active is false");
                    throw new AuthException("Login fail, user active is false");
                }
            }
            else{
                log.info("Social account is not existed, this is the first time they've logged " +
                        "in with facebook");
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if(optionalUser.isPresent()){
                    log.info("User logged in by username and password before logged in by facebook");
                    user = optionalUser.get();
                    if(user.getIsActive().equals(false)){
                        log.warn("Login fail, user active is false");
                        throw new AuthException("Login fail, user active is false");
                    }
                }
                else{
                    log.info("This is the first time they've logged in website, and logged in by facebook");
                    User newUser = User.builder()
                            .email(email)
                            .fullName(name)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .passwordHash(null)
                            .isActive(true)
                            .build();
                    user = userRepository.save(newUser);
                }
                SocialAccount newSocialAccount = SocialAccount.builder()
                        .user(user)
                        .provider(provider)
                        .providerUserId(facebookId)
                        .createdAt(LocalDateTime.now())
                        .email(email)
                        .build();
                socialAccountRepository.save(newSocialAccount);
            }

            String token = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = refreshTokenService.createRefreshToken(user);
            return LoginResponse.builder()
                    .userId(user.getId())
                    .email(email)
                    .userName(user.getFullName())
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("Facebook login failed", e);
            throw new AuthException("Facebook login failed: " + e.getMessage());
        }
    }

}
