package com.adityaraj.neurochat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            

            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    "/",
            			    "/*.html",          // ✅ THIS FIXES IT
            			    "/css/**",
            			    "/js/**",
            			    "/images/**",
            			    "/api/auth/**",
            			    "/api/payment/**",
            			    "/error"
            			).permitAll()

                .requestMatchers(
                    "/api/auth/**",
                    "/api/payment/**"
                ).permitAll()

                // 🔥 FIXED HERE
                .requestMatchers("/api/chat/**").permitAll()

                .anyRequest().authenticated()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(rateLimitFilter, JwtFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}