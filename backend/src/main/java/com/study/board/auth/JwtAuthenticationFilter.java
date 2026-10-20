package com.study.board.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;

    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization"); // 1) 헤더를 읽는다
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);      // 2) "Bearer " 7글자를 뗀 나머지가 토큰
            try {
                Long memberId = jwtTokenProvider.getMemberId(token);   // 서명과 만료 확인, 틀리면 예외
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(memberId, null, List.of());   // "이 요청은 회원 memberId", 가운데 null 인 이유는 토큰을 포스트맨에서 넘겨준거라서
                SecurityContextHolder.getContext().setAuthentication(authentication);          // Security에 저장

                log.info("토큰 확인 성공 memberId = {}", memberId);
            } catch (JwtException e) {
                log.warn("토큰 확인 실패 {}", e.getClass().getSimpleName());   // 예외 종류만 남긴다 (토큰 값은 남기지 않음)
            }
        }
        filterChain.doFilter(request, response);              // 5) 다음 단계로 넘긴다
    }
}