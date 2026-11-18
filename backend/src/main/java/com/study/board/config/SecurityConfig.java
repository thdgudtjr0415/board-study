package com.study.board.config;

import com.study.board.auth.JwtAuthenticationFilter;
import com.study.board.auth.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider)  throws Exception {
        http
                .csrf(csrf -> csrf.disable())          // 토큰 방식이라 CSRF(쿠키 기반 공격) 보호는 끈다
                .cors(Customizer.withDefaults())             // CORS 검사를 켠다 (허락 목록은 아래 corsConfigurationSource 빈을 쓴다)
                .formLogin(form -> form.disable())     // 기본 로그인 화면을 끈다 (우리는 /auth/login API가 있음)
                .httpBasic(basic -> basic.disable())   // 브라우저 기본 로그인 팝업을 끈다
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 만들지 않는다 (토큰으로만 확인)
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))) // 토큰이 없거나 잘못되면 401 (누구인지 모름)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()                         // 로그인 API는 누구나
                        .requestMatchers(HttpMethod.POST, "/members").permitAll()        // 회원 가입은 누구나
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()  // 글 읽기는 누구나
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll() // 이미지 보는 것은 누구나
                        .requestMatchers("/error").permitAll()  // 에러 응답을 만드는 주소는 누구나 (막으면 진짜 에러가 403으로 가려짐)
                        .anyRequest().authenticated()                                    // 나머지는 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    // 다른 출처(Vue, 5173)의 브라우저가 응답을 읽어도 되도록 허락 목록을 만든다: 출처 / 방식 / 헤더
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}