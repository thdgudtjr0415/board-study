# board-study

스터디 Q&A 게시판을 설계부터 직접 만들며 배우는 학습 프로젝트입니다.
질문이 카카오톡에 묻히는 문제를 풀기 위해, 질문과 답변을 모아 검색할 수 있는 게시판을 목표로 합니다.

## 구조

| 폴더 | 내용 | 상태 |
|---|---|---|
| `backend/` | Spring Boot 4.0.8 (Java 17) API | 메모리 저장소 버전 완성 |
| `db/` | 테이블을 만드는 SQL (MariaDB) | 작성 예정 |
| `frontend/` | 화면 | 예정 |

## 백엔드 API (메모리 버전)

| 기능 | 요청 | 성공 | 실패 |
|---|---|---|---|
| 작성 | `POST /posts` | 201 | 400 |
| 목록 | `GET /posts` | 200 | |
| 조회 | `GET /posts/{id}` | 200 | 404 |
| 수정 | `PUT /posts/{id}` | 200 | 404, 400 |
| 삭제 | `DELETE /posts/{id}` | 204 | 404 |

## 백엔드 구조

```
Controller  ->  Service  ->  Repository (interface)  <-  MemoryPostRepository
(HTTP)         (업무 순서)     (저장하고 꺼냄)              (지금은 ArrayList, 이후 DB)
```

- `Post`: 제목과 내용이 비어 있으면 만들 수 없는 도메인 객체
- `dto/`: 요청과 응답을 담는 record (`PostCreateRequest`, `PostUpdateRequest`, `PostResponse`)
- `PostExceptionHandler`: 없는 글은 404, 규칙 위반은 400으로 응답

## 앞으로

- DB 연결: MariaDB, MyBatis(읽기), JPA(쓰기), 트랜잭션
- 회원, 로그인(Spring Security, JWT), 댓글
- 화면

## 실행

```
cd backend
./gradlew bootRun
```

DB를 연결한 뒤에는 접속 정보를 환경변수로 넣어야 합니다. 비밀번호는 저장소에 올리지 않습니다.
