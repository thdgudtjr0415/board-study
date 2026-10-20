package com.study.board.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    @Test
    void createTokenHasThreeParts() {
        // 테스트용 임시 키 (진짜 JWT_SECRET이 아니라 실행할 때마다 새로 만들어요)
        // [구간 1] 토큰 만들기: secret이 쓰임
        String secret = Encoders.BASE64.encode(Jwts.SIG.HS512.key().build().getEncoded());

        JwtTokenProvider provider = new JwtTokenProvider(secret, 3600000);     // 한시간 설정

        String token = provider.createToken(11L); // memberId 가 Long 이라 L붙여야함

        String[] parts = token.split("\\."); // regex 정규표현식 \\. 은 .으로 나누겠다는 의미

        // 기대값이 3 이고 그에 해당하는것이 parts.length가 3인지 확인
        // 이유는 JWT를 .으로 나누면 헤더.페이로드.서명 이기때문에 3이 나옴
        assertEquals(3, parts.length);

        // [구간 2] 페이로드 읽기: secret이 안 쓰임
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        System.out.println(payload);
        assertTrue(payload.contains("\"sub\":\"11\"")); // 여기서 주황색 \"는 큰따움표 " 이거 출력하려고 하는거
    }

    @Test
    void getMemberIdReturnsId() {
        String secret = Encoders.BASE64.encode(Jwts.SIG.HS512.key().build().getEncoded());
        JwtTokenProvider provider = new JwtTokenProvider(secret, 3600000);

        String token = provider.createToken(11L);

        // 11L은 Long 값이라는 표시. assertEquals는 int 11과 비교해도 값이 같으면 통과하지만,
        // 읽을 때 Long인 게 드러나도록 L을 붙임
        // (createToken(11L)의 L은 필수. 파라미터가 Long이라 int 11을 넘기면 컴파일 에러)
        assertEquals(11L, provider.getMemberId(token));
    }

    @Test
    void forgedTokenIsRejected() {
        String secret = Encoders.BASE64.encode(Jwts.SIG.HS512.key().build().getEncoded());
        JwtTokenProvider provider = new JwtTokenProvider(secret, 3600000);

        String[] token11 = provider.createToken(11L).split("\\.");
        String[] token12 = provider.createToken(12L).split("\\.");

        // 12번 회원의 헤더.페이로드 + 11번 회원의 서명
        String forged = token12[0] + "." + token12[1] + "." + token11[2];

        assertThrows(JwtException.class, () -> provider.getMemberId(forged));
    }

    @Test
    void expiredTokenIsRejected() {
        String secret = Encoders.BASE64.encode(Jwts.SIG.HS512.key().build().getEncoded());
        JwtTokenProvider provider = new JwtTokenProvider(secret, -1000);   // 만료 1초후

        String token = provider.createToken(11L);

        assertThrows(ExpiredJwtException.class, () -> provider.getMemberId(token));
    }
}