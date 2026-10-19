package com.study.board.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // 이거 value lombok 이랑 안 햇갈리게 조심
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// JWT (헤더.페이로드.서명) 으로 이루어짐
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration-ms}") long expirationMs){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String createToken(Long memberId){
        Date now = new Date();                      // 현재 시각

        return Jwts.builder()                       // 토큰을 만드는 도구를 꺼냄
                .subject(String.valueOf(memberId))  // 페이로드의 sub = 회원 id or 혹은 memberName/memberNickname을 넣을 때도 있음
                .issuedAt(now)                      // 페이로드의 iat = 지금
                .expiration(new Date(now.getTime() + expirationMs)) // 페이로드의 exp = 지금 + 1시간(지금 expiration-ms 가 1시간짜리임)
                .signWith(key)                      // 비밀 키로 서명을 계산해서 붙여요
                .compact();                         // 완성된 문자열 하나로 만들어요
    }

    // 토큰이 진짜인지 확인하고, 진짜면 안에 든 회원 id를 꺼낸다 (가짜/만료면 예외가 터진다)
    public Long getMemberId(String token) {
        String subject = Jwts.parser()              // 토큰을 읽는 도구를 꺼낸다 (createToken의 builder()의 반대)
                .verifyWith(key)                    // 이 비밀 키로 서명을 확인하겠다고 알려 준다
                .build()                            // 도구 설정을 마치고 완성한다
                .parseSignedClaims(token)           // 토큰을 읽는다. 서명 확인과 만료 확인이 여기서 자동으로 일어난다(핵심)
                .getPayload()                       // 통과했으면 페이로드(내용)를 꺼낸다
                .getSubject();                      // 페이로드의 sub 값("11")을 꺼낸다
        return Long.valueOf(subject);               // 글자 "11"을 숫자 11L로 바꿔서 돌려준다
    }
}
