package com.study.board.exception;

import com.study.board.auth.LoginFailedException;
import com.study.board.place.ExternalApiException;
import com.study.board.post.PostForbiddenException;
import com.study.board.post.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.study.board.member.DuplicateLoginIdException;

// 모든 컨트롤러에 적용되는 전역 예외 처리
// 예외 클래스는 각 도메인 패키지에 있고(무슨 일이 일어났나), 여기서는 그 예외를 HTTP 번호로 바꾼다(어떻게 응답하나)
//
// 응답 번호 정리
//   400 Bad Request          요청 내용이 잘못됨 (빈 값, 길이, 형식)           → 요청한 사람이 고치면 됨
//   401 Unauthorized         누구인지 확인 실패 (로그인 실패)                  → 인증 문제
//   403 Forbidden            누구인지는 아는데 권한이 없음 (남의 글 수정/삭제)  → 권한 문제
//   404 Not Found            찾는 것이 없음 (없는 글)
//   409 Conflict             지금 상태와 충돌 (이미 있는 아이디)
//   502 Bad Gateway          우리 서버는 정상인데 뒤의 외부 서버(카카오)가 이상함 → 사용자 잘못이 아님
//   500 Internal Server Error 아래에서 처리하지 않은 예외는 원래 500
//        (지금은 Security가 /error 주소도 막아서 403으로 보일 수 있음 → 보안 마무리에서 /error를 열어 줄 것)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: 없는 글을 조회, 수정, 삭제하려고 할 때 (PostService)
    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(PostNotFoundException e) {
        return e.getMessage();
    }

    // 400: 도메인 검사(Post, Member의 규칙)를 통과하지 못한 값, 존재하지 않는 회원 id 등
    //      → 우리가 검사하다 던진 IllegalArgumentException.
    //       라이브러리가 던지는 영어 메시지도 여기로 올 수 있어서 입력 단계에서 막는 게 좋음
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException e) {
        return e.getMessage();
    }

    // 409: 이미 있는 로그인 아이디로 가입하려 할 때 (MemberService). 이 예외만 409
    @ExceptionHandler(DuplicateLoginIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateLoginId(DuplicateLoginIdException e) {
        return e.getMessage();
    }

    // 400: 요청 DTO의 @Valid 검사에 실패했을 때 (가입 비밀번호의 @NotBlank, @Size, @Pattern 등). 첫 번째 메시지만 돌려줌
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    }

    // 401: 로그인 실패. 아이디가 없든 비밀번호가 틀리든 같은 메시지 (가입된 아이디인지 드러나지 않게) (AuthService)
    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleLoginFailed(LoginFailedException e) {
        return e.getMessage();
    }

    // 403: 로그인은 했지만 남의 글을 수정/삭제하려 할 때 (PostService에서 글의 작성자와 토큰의 회원 id를 비교)
    @ExceptionHandler(PostForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handlePostForbidden(PostForbiddenException e) {
        return e.getMessage();
    }

    // 502: 외부 API(카카오)가 에러를 주거나 연결이 안 될 때 (KakaoPlaceClient가 던짐)
    //      우리 서버나 사용자의 잘못이 아니라 뒤의 서버 문제라서 500도 400도 아닌 502. 처리하지 않으면 /error가 막혀 403으로 가려졌음
    @ExceptionHandler(ExternalApiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public String handleExternalApi(ExternalApiException e) {
        return e.getMessage();
    }
}
