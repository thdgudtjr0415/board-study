package com.study.board.auth.dto;

// 로그인할 때 클라이언트가 보내는 아이디와 비밀번호
public record LoginRequest(String loginId, String pwd) {
}