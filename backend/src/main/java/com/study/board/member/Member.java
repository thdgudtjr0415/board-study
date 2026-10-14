package com.study.board.member;

import jakarta.persistence.*;
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

    protected Member() {}
}
