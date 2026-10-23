-- board-study 테이블 정의 (MariaDB, 문자셋 utf8mb4)
--
-- 이 파일은 "테이블을 만드는 문장"만 담는다.
-- DB(board_study)와 접속 계정(board_user)은 관리자가 따로 만든다.
-- 비밀번호는 이 파일에 쓰지 않는다.
--
-- 실행 순서가 중요하다: post가 member를, comment가 member와 post를 참조(FK)하므로
-- member -> post -> comment 순서로 만든다.
-- 지울 때는 반대로 comment -> post -> member 순서로 지운다.


-- 회원
CREATE TABLE member (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    VARCHAR(20) NOT NULL UNIQUE,          -- 로그인 아이디, 중복 불가
    pwd         VARCHAR(60) NOT NULL,                 -- BCrypt 해시는 60자
    name        VARCHAR(50) NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- 게시글
CREATE TABLE post (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count  INT NOT NULL DEFAULT 0,
    member_id   BIGINT NOT NULL,                      -- 작성자
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 댓글
CREATE TABLE comment (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	content TEXT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	member_id BIGINT NOT NULL,
	post_id BIGINT NOT NULL,
	FOREIGN KEY (member_id) REFERENCES member(id),
	FOREIGN KEY (post_id) REFERENCES post(id)
);