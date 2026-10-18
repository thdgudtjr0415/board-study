package com.study.board.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login_id")
    private String loginId;
    @Column(name = "pwd")
    private String pwd;
    @Column(name = "name")
    private String name;

//    private LocalDateTime created_at; -> DEFAULT CURRENT_TIMESTAMP 으로 알아서 들어감

    // JPA가 DB에서 읽은 행으로 객체를 만들 때 쓰는 기본 생성자 (직접 new 하지 못하게 protected)
    protected Member() {}

    private static void check(String value, String label, int min, int max){
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException(label + "을(를) 입력해주세요");
        }
        if(value.length() < min || value.length() > max){  // null 순서 잘 보기
            throw new IllegalArgumentException(label + "은(는) " + min + " ~ " + max + "자여야 합니다.");
        }
    }

    private static void validate(String loginId, String pwd, String name){
        check(loginId, "로그인 아이디", 1, 20);
        // 여기서 받는 pwd는 BCrypt 해시(항상 60자). 8자 이상 등 원문 규칙은 DTO(@Size)가 담당하고,
        // 여기는 비어 있지 않고 DB 컬럼(varchar 60)에 들어가는지만 확인
        check(pwd, "비밀번호", 1, 60);
        check(name, "이름", 1, 50);
    }

    // loginId, pwd, name 을 받는 생성자
    public Member(String loginId, String pwd, String name){
        validate(loginId, pwd, name);
        this.loginId = loginId;
        this.pwd = pwd;
        this.name = name;
    }
}
