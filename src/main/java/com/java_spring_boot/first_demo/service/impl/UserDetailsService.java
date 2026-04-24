package com.java_spring_boot.first_demo.service.impl;


import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByEmail {}", email);
        Optional<User> user = userRepository.findByEmail(email);

        if(!user.isPresent()){
            log.info("User with email {} not found", email);
            throw new NotFoundException("User not found.");
        }
        return new CustomUserDetail(user.get());
    }
}
