package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.SocialAccount;
import com.java_spring_boot.first_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount,Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<SocialAccount> findByUserAndProvider(User user, String provider);
}
