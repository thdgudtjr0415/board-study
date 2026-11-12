-- board-study 테이블 정의 (MariaDB, 문자셋 utf8mb4)
--
-- 이 파일은 "테이블을 만드는 문장"만 담는다.
-- DB(board_study)와 접속 계정(board_user)은 관리자가 따로 만든다.
-- 비밀번호는 이 파일에 쓰지 않는다.
--
-- 실행 순서가 중요하다: post가 member를, comment가 member와 post를,
-- notification이 member와 post와 comment를 참조(FK)하므로
-- member -> post -> comment -> notification 순서로 만든다.
-- 지울 때는 반대로 notification -> comment -> post -> member 순서로 지운다.


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
    image_name  VARCHAR(100),      -- 이미지 추가   
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

-- 알림 (내 글에 다른 사람이 댓글을 달면 글 작성자에게 생긴다)
-- 글이나 댓글이 지워지면 알림도 DB가 같이 지운다 (ON DELETE CASCADE)
CREATE TABLE notification (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	is_read TINYINT(1) NOT NULL DEFAULT 0,               -- 0 안 읽음, 1 읽음
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	member_id BIGINT NOT NULL,                           -- 알림을 받는 사람 (글 작성자)
	post_id BIGINT NOT NULL,                             -- 댓글이 달린 글
	comment_id BIGINT NOT NULL,                          -- 새로 달린 댓글
	FOREIGN KEY (member_id) REFERENCES member(id),
	FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
	FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE
);
SELECT id, title, image_name, member_id  FROM post ORDER BY id DESC;



SHOW INDEX FROM post;

CREATE TABLE post_exp LIKE post;
SHOW INDEX FROM post_exp;
SELECT seq FROM seq_1_to_3;   -- seq_1_to_3 이건 1 ~ 3 까지 가짜 테이블

-- seq_1_to_N: MariaDB가 제공하는 1부터 N까지 숫자가 한 줄씩 있는 가짜 테이블 (열 이름은 seq)
-- seq_1_to_N: 1부터 N까지 숫자 한 줄씩 (MariaDB 전용 숫자표, 열 이름 seq)
INSERT INTO post_exp (title, content, member_id)
SELECT CONCAT('글 제목 ', seq), '실험용 내용', 11
FROM seq_1_to_100000;

SELECT COUNT(*) FROM post_exp; -- 100,000 개 나오는지 확인

-- EXPLAIN: SQL 앞에 붙이면 실행 대신 "찾는 계획"을 보여 줌
-- type=ALL 전부 읽기 / key=쓴 인덱스(NULL이면 안 씀) / rows=읽을 줄 수 예상
EXPLAIN SELECT * FROM post_exp WHERE title = '글 제목 77777';

-- CREATE INDEX 이름 ON 테이블 (열): 그 열에 색인(인덱스)을 만들어 검색을 빠르게 함
CREATE INDEX idx_post_exp_title ON post_exp (title);

EXPLAIN SELECT * FROM post_exp WHERE title = '글 제목 77777';

EXPLAIN SELECT * FROM post_exp WHERE title LIKE '글 제목 7777%';   -- 앞부분이 정해진 검색
EXPLAIN SELECT * FROM post_exp WHERE title LIKE '%77777';          -- 앞부분이 안 정해진 검색