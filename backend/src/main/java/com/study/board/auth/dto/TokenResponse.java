package com.study.board.auth.dto;

// 로그인에 성공하면 돌려주는 토큰. 응답은 {"accessToken": "..."} 모양
public record TokenResponse(String accessToken) {
}
