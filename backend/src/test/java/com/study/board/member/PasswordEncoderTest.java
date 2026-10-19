package com.study.board.member;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void sameRawPwdGivesDifferentHash() {
        String h1 = encoder.encode("testpw1234");
        String h2 = encoder.encode("testpw1234");
        assertNotEquals(h1, h2);
    }

    @Test
    void matchesComparesRawWithHash() {
        String h = encoder.encode("testpw1234");
        assertTrue(encoder.matches("testpw1234", h));
        assertFalse(encoder.matches("wrongpw1234", h));
    }
//    75바이트 실패하는지 확인
//    @Test
//    void koreanOver72Bytes() {
//        String pwd25 = "가".repeat(25);   // 25글자 = UTF-8로 75바이트
//        String pwd24 = "가".repeat(24);   // 24글자 = 72바이트
//        String h = encoder.encode(pwd25); // 이거 75바이트가 되어서 72 바이트를 넘어 안됨
//        assertTrue(encoder.matches(pwd24, h));
//    }

    @Test
    void koreanOver72BytesIsRejected() {
        String pwd25 = "가".repeat(25);   // 25글자 = UTF-8로 75바이트, 72바이트를 넘음
        assertThrows(IllegalArgumentException.class, () -> encoder.encode(pwd25));
    }
}