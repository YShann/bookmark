package com.personal.bookmark_backend.config;

import com.personal.bookmark_backend.dto.response.ErrorResponse;
import com.personal.bookmark_backend.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ObjectMapper objectMapper;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        // =====================================================
        // 401：尚未登入或 Token 無效
        // =====================================================

        AuthenticationEntryPoint authenticationEntryPoint =
                (request, response, authException) -> {

                    writeErrorResponse(
                            response,
                            HttpStatus.UNAUTHORIZED,
                            "尚未登入或登入憑證已失效"
                    );
                };


        // =====================================================
        // 403：已登入，但沒有操作權限
        // =====================================================

        AccessDeniedHandler accessDeniedHandler =
                (request, response, accessDeniedException) -> {

                    writeErrorResponse(
                            response,
                            HttpStatus.FORBIDDEN,
                            "沒有權限執行此操作"
                    );
                };


        http
                // REST API 使用 JWT，不需要 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 不使用 Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // 關閉預設登入方式
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // 統一處理 401、403
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        authenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        accessDeniedHandler
                                )
                )

                // API 權限設定
                .authorizeHttpRequests(auth -> auth

                        // 不需要 Access Token 的 API
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/registration-code",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout"
                        ).permitAll()

                        // Spring Boot 錯誤處理端點
                        .requestMatchers("/error").permitAll()

                        // 頭像使用可公開讀取的圖片網址
                        .requestMatchers(HttpMethod.GET, "/uploads/avatars/**").permitAll()

                        // 其他 API 都需要登入
                        .anyRequest().authenticated()
                )

                // JWT Filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    // =====================================================
    // 統一輸出 JSON 錯誤回應
    // =====================================================

    private void writeErrorResponse(
            jakarta.servlet.http.HttpServletResponse response,
            HttpStatus status,
            String message
    ) throws IOException {

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now()
        );

        response.setStatus(status.value());

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}
