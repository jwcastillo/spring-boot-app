package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated().and())
                .httpBasic(Customizer.withDefaults()).exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> {
                    log.error("Full authentication is required to access this resource");
                    String json = String.format("{\"message\": \"%s\"}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(json);
                });

        return http.build();
    }
}
