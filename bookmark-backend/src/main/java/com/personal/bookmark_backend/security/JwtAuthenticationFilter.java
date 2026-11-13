package com.personal.bookmark_backend.security;


import com.personal.bookmark_backend.service.JwtService;
import com.personal.bookmark_backend.repository.RefreshTokenRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokens;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 取得 Authorization Header
        String authorizationHeader = request.getHeader("Authorization");

        // 沒有 Bearer Token，就交給後續的 Security 機制處理
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 移除 Bearer 前綴，取得真正的 JWT
        String token = authorizationHeader.substring(7);

        try {
            // 從 Token 取得使用者 ID
            var identity = jwtService.authenticate(token);

            // 如果目前尚未建立 Authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null &&
                    refreshTokens.existsActiveSession(identity.sessionId(), identity.userId())) {

                // 建立使用者身分資訊
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                identity.userId(),
                                null,
                                Collections.emptyList()
                        );

                // 設定 Request 詳細資訊
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // 將使用者身分放入 Security Context
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException | IllegalArgumentException e) {
            // Token 格式錯誤、簽章錯誤或解析失敗時，不建立登入狀態
            SecurityContextHolder.clearContext();
        }

        // 繼續執行後續 Filter
        filterChain.doFilter(request, response);
    }
}
