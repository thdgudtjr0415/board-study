package com.study.board.auth;

import com.study.board.auth.dto.LoginRequest;
import com.study.board.auth.dto.TokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 로그인: 아이디/비밀번호가 맞으면 토큰을 돌려준다
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        String token = authService.login(request.loginId(), request.pwd());
        return new TokenResponse(token);
    }
}