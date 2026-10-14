
/* ====================================================================================
 * [학습 기록 보관] MemoryPostRepository (2026-10-04 ~ 10-13)
 *
 * 이 파일은 "지금은 쓰지 않지만 이렇게 했었다"를 남겨 두는 곳이다.
 * (위 HelloController 는 Boot 첫 API 연습, 아래는 DB 연결 전의 메모리 저장소)
 *
 * DB 없이 게시판 API를 만들 때 쓰던 "메모리 저장소"다.
 *  - 글을 ArrayList(store)에 담고, id 는 sequence++ 로 직접 올렸다.
 *  - 서버를 끄면 글이 모두 사라진다.
 *
 * 10/13 DB 연결 이후에는 MyBatisPostRepository(읽기는 MyBatis, 쓰기는 JPA)가 대신 쓰인다.
 *
 * 이 코드가 가르쳐 준 것
 *  1. PostRepository 인터페이스 덕분에 PostService 를 한 줄도 안 고치고 저장 방식을 갈아끼울 수 있었다.
 *  2. 구현체가 둘이면 서버 시작 때 NoUniqueBeanDefinitionException 이 나고, @Primary 로 해결한다.
 *     (요청 때가 아니라 "시작 단계"에서 실패한다)
 *  3. 직접 올리던 sequence++ 는 DB 의 AUTO_INCREMENT 가 맡는다. save 가 돌려준 객체에 id 가 채워진다.
 *  4. 메모리 버전에서는 같은 객체를 계속 들고 있어서 post.update() 만 해도 수정이 반영됐다.
 *     DB 버전에서는 UPDATE 를 보내는 코드가 따로 필요하다. (그래서 findByIdForUpdate 와 @Transactional)
 *
 * 주의: PostRepository 에 메소드가 추가되어도 이 코드는 구현하지 않는다.
 *       (아래 findByIdForUpdate 는 추가했던 당시의 모습)
 * ==================================================================================== */

// package com.study.board.post;
//
// import org.springframework.stereotype.Repository;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// @Repository
// public class MemoryPostRepository implements PostRepository{
//     private final List<Post> store = new ArrayList<>();
//     private Long sequence = 1L;
//
//     @Override
//     public Post save(Post post) {
//         Long id = sequence++;
//         String title = post.getTitle();
//         String content = post.getContent();
//         Long memberId = post.getMemberId();
//
//         Post saved = new Post(id, title, content, memberId);
//         store.add(saved);
//         System.out.println("게시글이 저장되었습니다.");
//         return saved;
//     }
//
//     @Override
//     public List<Post> findAll() {
//         System.out.println("모든 글을 불러 왔습니다.");
//         return store;
//     }
//
//     @Override
//     public Optional<Post> findById(Long id) {
//         return store.stream()                    // 리스트를 "하나씩 흘려보내는 통로"로 바꾼다
//                 .filter(post -> post.getId().equals(id))   // 조건에 맞는 것만 통과시킨다
//                 .findFirst();                    // 통과한 것 중 첫 번째를 Optional로 돌려준다
//     }
//     @Override
//     public void deleteById(Long id){
//         store.removeIf(post -> post.getId().equals(id));
//     }
//
//     // update
//     @Override
//     public Optional<Post> findByIdForUpdate(Long id) {
//         return store.stream().filter(post -> post.getId().equals(id)).findFirst();
//     }
// }
