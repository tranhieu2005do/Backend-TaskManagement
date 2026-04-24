package com.java_spring_boot.first_demo.config.security;

import com.java_spring_boot.first_demo.service.impl.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider providers = new DaoAuthenticationProvider();
        providers.setPasswordEncoder(passwordEncoder());
        providers.setUserDetailsService(userDetailsService);
        return providers;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            DaoAuthenticationProvider provider = daoAuthenticationProvider();
            if (provider.supports(authentication.getClass())) {
                return provider.authenticate(authentication);
            }
            throw new BadCredentialsException("Unsupported authentication");
        };
    }
//    public OAuth2UserService oAuth2UserService(){}
}
