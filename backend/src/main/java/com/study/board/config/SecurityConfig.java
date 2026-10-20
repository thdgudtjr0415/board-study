package com.study.board.config;

import com.study.board.auth.JwtAuthenticationFilter;
import com.study.board.auth.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider)  throws Exception {
        http
                .csrf(csrf -> csrf.disable())          // 토큰 방식이라 CSRF(쿠키 기반 공격) 보호는 끈다
                .formLogin(form -> form.disable())     // 기본 로그인 화면을 끈다 (우리는 /auth/login API가 있음)
                .httpBasic(basic -> basic.disable())   // 브라우저 기본 로그인 팝업을 끈다
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 만들지 않는다 (토큰으로만 확인)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()                         // 로그인 API는 누구나
                        .requestMatchers(HttpMethod.POST, "/members").permitAll()        // 회원 가입은 누구나
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()  // 글 읽기는 누구나
                        .anyRequest().authenticated()                                    // 나머지는 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}