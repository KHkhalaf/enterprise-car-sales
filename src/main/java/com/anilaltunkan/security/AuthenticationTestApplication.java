package com.anilaltunkan.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class AuthenticationTestApplication {

	public AuthenticationTestApplication() {
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationTestApplication.class, args);

	}
}
