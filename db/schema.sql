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

-- member와 같은 모양의 실험용 테이블 (login_id의 UNIQUE 인덱스도 같이 복사됨)
CREATE TABLE member_exp LIKE member;

-- 회원 1000명: user1~user1000 (login_id는 중복 불가라 번호를 붙여 다 다르게 만듦)
INSERT INTO member_exp (login_id, pwd, name)
SELECT CONCAT('user', seq), 'x', CONCAT('회원', seq)  -- login, pwd, name 순으로 들어가는것
FROM seq_1_to_1000;

SELECT COUNT(*) FROM member_exp;     -- 기대: 1000
SELECT MIN(id), MAX(id) FROM member_exp;   -- 기대: 1, 1000

SELECT * FROM member_exp; 

-- % 는 나머지: 글 번호를 1000으로 나눈 나머지에 1을 더해 작성자를 1~1000번에 돌아가며 배정
UPDATE post_exp SET member_id = (id % 1000) + 1;

SELECT * FROM post_exp;

SELECT MIN(member_id), MAX(member_id) FROM post_exp;           -- 기대: 1, 1000
SELECT COUNT(DISTINCT member_id) FROM post_exp;                -- 기대: 1000 (서로 다른 작성자 수)

-- 글 목록 쿼리 (PostMapper.findAllWithAuthor)의 실험용 복제본. 테이블 이름만 _exp로 바꿈
EXPLAIN
SELECT p.id, p.title, p.content, p.member_id, m.name AS author_name, p.image_name, p.view_count
FROM post_exp p
JOIN member_exp m ON p.member_id = m.id
ORDER BY p.id;


-- 댓글 5만 개: 작성자는 1~1000번, 글은 1~100000번에 돌아가며 배정
CREATE TABLE comment_exp LIKE comment;
INSERT INTO comment_exp (content, member_id, post_id)
SELECT '실험용 댓글', (seq % 1000) + 1, (seq % 100000) + 1
FROM seq_1_to_50000;

-- 알림 5만 개: 받는 사람 1~1000번, comment_id는 방금 만든 댓글 번호(1~50000)와 짝
CREATE TABLE notification_exp LIKE notification;
INSERT INTO notification_exp (is_read, member_id, post_id, comment_id)
SELECT seq % 2, (seq % 1000) + 1, (seq % 100000) + 1, seq
FROM seq_1_to_50000;

SELECT COUNT(*) FROM comment_exp;        -- 기대: 50000
SELECT COUNT(*) FROM notification_exp;   -- 기대: 50000

-- 알림 목록 쿼리 (NotificationMapper.findByMemberId)의 실험용 복제본. 테이블 이름만 _exp로, #{memberId}는 7로 바꿈
EXPLAIN
SELECT n.id, n.post_id, n.comment_id, m.name AS commenter_name,
       CONCAT(LEFT(c.content, 10), IF(CHAR_LENGTH(c.content) > 10, '...', '')) AS comment_preview,
       n.is_read, n.created_at
FROM notification_exp n
JOIN comment_exp c ON n.comment_id = c.id
JOIN member_exp m ON c.member_id = m.id
WHERE n.member_id = 7
ORDER BY n.id DESC;

-- 댓글 목록 쿼리 (CommentMapper.findByPostIdWithAuthor)의 실험용 복제본. 테이블 이름만 _exp로, #{postId}는 777로 바꿈
EXPLAIN
SELECT c.id, c.content, c.member_id, c.post_id, m.name AS author_name
FROM comment_exp c
JOIN member_exp m ON c.member_id = m.id
WHERE c.post_id = 777
ORDER BY c.id;


-- ===== 페이지네이션: OFFSET이 클 때 (2026-11-15) =====
-- ANALYZE: EXPLAIN과 달리 쿼리를 실제로 실행하고, 실제로 읽은 행 수(r_rows)까지 보여 준다
-- 세 쿼리 모두 결과는 글 10개로 같다. 다른 것은 "그 10개를 얻으려고 DB가 몇 개를 읽었나"

-- 1페이지: 기본키 인덱스 끝에서 10개만 읽고 멈춤 (p의 r_rows 10, 0.001초)
ANALYZE
SELECT p.id, p.title, p.content, p.member_id, m.name AS author_name, p.image_name, p.view_count
FROM post_exp p
JOIN member_exp m ON p.member_id = m.id
ORDER BY p.id DESC
LIMIT 10 OFFSET 0;

-- 10000페이지 OFFSET: 건너뛸 99990개까지 약 10만 개를 다 읽고 버림 (회원 1000 x 글 100, 0.171초)
-- → OFFSET은 건너뛰는 행도 읽기 때문에 뒤 페이지일수록 느려진다
ANALYZE
SELECT p.id, p.title, p.content, p.member_id, m.name AS author_name, p.image_name, p.view_count
FROM post_exp p
JOIN member_exp m ON p.member_id = m.id
ORDER BY p.id DESC
LIMIT 10 OFFSET 99990;

-- 10000페이지를 키셋 방식으로 (9999페이지의 마지막 id = 11)
-- 키셋: "몇 개 건너뛸지(OFFSET)" 대신 "마지막으로 본 글의 키(id)보다 작은 것"으로 다음 페이지를 찾는다
-- 기본키 인덱스에서 11 바로 아래로 바로 건너가 10개만 읽음 (type range, r_rows 10, 0.001초)
-- → 몇 번째 페이지든 빠르지만, "37페이지로 바로 가기"는 못 한다 (앞 페이지의 마지막 id를 알아야 하므로)
ANALYZE
SELECT p.id, p.title, p.content, p.member_id, m.name AS author_name, p.image_name, p.view_count
FROM post_exp p
JOIN member_exp m ON p.member_id = m.id
WHERE p.id < 11
ORDER BY p.id DESC LIMIT 10;

-- 회원별 비밀번호가 BCrypt 해시인지 확인 (해시는 $2a$10$ 로 시작)
SELECT
id, 
login_id, 
name, 
LEFT(pwd, 7) AS pwd_head,
CHAR_LENGTH(pwd) AS pwd_len
FROM member
ORDER BY id;

-- 비번 tmpmember 로 바꿈
UPDATE member m
JOIN member t ON t.id = 15
SET m.pwd = t.pwd
WHERE m.id IN (8,10);

