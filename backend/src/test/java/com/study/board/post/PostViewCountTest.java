package com.study.board.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

// 조회수 동시성 실험: 스레드 100개가 거의 동시에 조회수를 올리면 몇이 되는가
// 진짜 DB(board_study)에 쓴다. 끝나면 @AfterEach가 조회수를 시작 전 값으로 되돌린다.
@SpringBootTest
class PostViewCountTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    // SQL을 코드 안에 문자열로 바로 보냄
    @Autowired
    JdbcTemplate jdbcTemplate;

    Long postId;
    int before;

    @BeforeEach
    void 시작값_기록() {
        // DB에 있는 첫 번째 글로 실험한다 (글이 하나도 없으면 먼저 글을 만들어야 함)
        postId = postService.findAllWithAuthor().get(0).id();
        before = postRepository.selectViewCount(postId);   // 시작 전 값을 기억해 둠 (0으로 되돌리지 않음)
    }

    @Test
    void 동시에_100번_조회() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // 여기 람다는 아직 실행 안한것 거의 if 문과 비슷
            Thread t = new Thread(() -> postService.increaseViewCount(postId, null));
            threads.add(t);
        }
        for (Thread t : threads) t.start();   // 100개를 한꺼번에 출발시킴
        for (Thread t : threads) t.join();    // 다 끝날 때까지 기다림

        int after = postRepository.selectViewCount(postId);
        // 100번 올렸으니 정확히 100 늘어나야 한다 (위험한 코드일 때는 6만 늘어났음)
        System.out.println("before=" + before + ", after=" + after);
        assertEquals(100, after - before);
    }

    @AfterEach
    void 원래대로() {
        jdbcTemplate.update("UPDATE post SET view_count = ? WHERE id = ?", before, postId);
    }
}
