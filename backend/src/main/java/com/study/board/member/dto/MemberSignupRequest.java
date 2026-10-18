package com.study.board.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberSignupRequest(
        String loginId,
        // null, 빈칸, 공백만 있는 값을 거절 (Member.check의 value == null || value.isBlank()와 같은 일)
        @NotBlank(message = "비밀번호를 입력해주세요")
        // @Size만 있으면 null과 공백만 있는 값(예: 공백 8개)이 통과함. 둘은 서로 보완하는 관계이고 실행 순서는 정해져 있지 않음
        // 72: BCrypt 한계(72바이트). 이 버전은 넘으면 encode가 예외를 던짐.
        // @Pattern으로 영문/숫자/특수문자(1글자 = 1바이트)만 허용해서 글자 수 72가 곧 바이트 72가 되게 함
        @Size(min = 8, max = 72, message = "비밀번호는 8 ~ 72자여야 합니다.")
        @Pattern(regexp = "^[\\x21-\\x7E]+$", message = "비밀번호는 영문, 숫자, 특수문자만 사용할 수 있습니다.") //
        String pwd,
        String name) {
}
