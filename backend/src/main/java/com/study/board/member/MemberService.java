package com.study.board.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 맴버 생성
    // log 찍을 때 {} 잘 활용 하기
    public Member signup(String loginId, String pwd, String name){
        log.info("회원 가입 요청 loginId = {}", loginId);
        Member newMember = new Member(loginId, pwd, name);
        if(memberRepository.existsByLoginId(loginId)){
            throw new DuplicateLoginIdException(loginId);
        }
        Member saved = memberRepository.save(newMember);
        return saved;
    }

}
