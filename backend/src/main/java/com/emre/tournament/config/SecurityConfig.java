package com.emre.tournament.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/tournaments/my-upcoming").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tournaments/my-upcoming-games").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/tournaments").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/tournaments/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tournaments/*/apply").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tournaments/*/seed").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/tournaments/**").permitAll()

                        .anyRequest().authenticated()
                )
                .build();
    }
}
