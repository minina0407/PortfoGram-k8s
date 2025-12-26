package com.api.localportfogram.auth.utils;

import com.api.localportfogram.auth.handler.JwtAccessDeniedHandler;
import com.api.localportfogram.auth.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // @EnableGlobalMethodSecurity is deprecated
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;

    // Swagger-ui v3 경로
    private static final String[] SWAGGER_URL_PATHS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(corsFilter)
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(authorize -> authorize
                                // Swagger UI 경로 허용
                                //     .requestMatchers(SWAGGER_URL_PATHS).permitAll()
                                // 기존의 다른 허용 경로들
                                .anyRequest().permitAll()
                        //    .requestMatchers("/api/v1/portfolios/**").permitAll()
                        //    .requestMatchers("/**").permitAll()
                        ///   .requestMatchers("/api/v1/auth/**", "/api/v1/users").permitAll() // 예: 로그인, 회원가입
                        //    .requestMatchers("/ws/**", "/actuator/**").permitAll()
                        // 나머지 요청은 인증 필요
                        //    .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        // JWT 필터 적용 (이 부분이 누락되었을 수 있습니다. JwtSecurityConfig 클래스를 만들어 적용하는 것이 일반적입니다.)
        // http.apply(new JwtSecurityConfig(jwtTokenProvider));

        return http.build();
    }
}
