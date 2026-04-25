package com.java_spring_boot.first_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableCaching
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.java_spring_boot.first_demo.repository")
@EnableElasticsearchRepositories(basePackages = "com.java_spring_boot.first_demo.repository.document")
@EnableMethodSecurity(proxyTargetClass = true, prePostEnabled = true)
public class Application {

	public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
        System.out.println("Checking line");
	}

}
