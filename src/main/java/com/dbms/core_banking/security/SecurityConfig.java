package com.dbms.core_banking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/loans/**").hasRole("CUSTOMER")
                .requestMatchers("/api/accounts/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/transfers/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic();

        return http.build();
    }
}
