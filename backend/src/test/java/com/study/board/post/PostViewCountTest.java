package com.study.board.post;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

// 조회수 동시성 실험: 스레드 100개가 거의 동시에 조회수를 올리면 몇이 되는가
// 진짜 DB(board_study)에 쓴다. 실행할 때마다 첫 번째 글의 조회수가 100씩 늘어난다.
@SpringBootTest
class PostViewCountTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    @Test
    void 동시에_100번_조회() throws InterruptedException {
        // DB에 있는 첫 번째 글로 실험한다 (글이 하나도 없으면 먼저 글을 만들어야 함)
        Long postId = postService.findAllWithAuthor().get(0).id();
        int before = postRepository.selectViewCount(postId);   // 시작 전 값을 기억해 둠 (0으로 되돌리지 않음)

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> postService.increaseViewCount(postId));
            threads.add(t);
        }
        for (Thread t : threads) t.start();   // 100개를 한꺼번에 출발시킴
        for (Thread t : threads) t.join();    // 다 끝날 때까지 기다림

        int after = postRepository.selectViewCount(postId);
        // 100번 올렸으니 정확히 100 늘어나야 한다 (위험한 코드일 때는 6만 늘어났음)
        assertEquals(100, after - before);
    }
}
