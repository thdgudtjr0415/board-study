package com.study.board.member;

import com.study.board.member.dto.MemberResponse;
import com.study.board.member.dto.MemberSignupRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    // 생성자
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@Valid @RequestBody MemberSignupRequest request){
        Member newMember = memberService.signup(request.loginId(), request.pwd(), request.name());
        MemberResponse response = MemberResponse.from(newMember);
        return response;
    }
}
