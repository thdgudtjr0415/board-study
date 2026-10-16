package com.study.board.member;

public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException(String loginId){
        super("중복된 아이디입니다 : " + loginId);
    }
}
