package com.study.board.auth;

import com.study.board.member.Member;
import com.study.board.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(MemberRepository memberRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 로그인: 아이디로 회원을 찾고 -> 비밀번호를 비교하고 -> 맞으면 토큰을 만들어 돌려준다
    public String login(String loginId, String pwd){
        // 아이디가 DB에 없으면 로그인 실패 (Optional이 비어 있으면 예외)
        // LoginFailedException::new 는 () -> new LoginFailedException() 을 줄인 표기 (넘길 값이 없는 생성자라 ::new 사용 가능)
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    log.warn("로그인 실패: 존재하지 않는 아이디");
                    return new LoginFailedException();
                });
        // 입력한 원문(pwd)과 DB에 저장된 해시(member.getPwd())를 비교. 틀리면 로그인 실패
        if(!passwordEncoder.matches(pwd, member.getPwd())){
            log.warn("로그인 실패: 비밀번호 불일치 memberId = {}", member.getId());
            throw new LoginFailedException();
        }
        // id만 남긴다 (비밀번호·해시·토큰은 로그에 남기지 않음)
        log.info("로그인 성공 memberId = {}", member.getId());
        return jwtTokenProvider.createToken(member.getId());
    }

}
