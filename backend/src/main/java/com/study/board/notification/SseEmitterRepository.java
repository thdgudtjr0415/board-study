package com.study.board.notification;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// 통로를 넣고, 꺼내고, 빼는 일만 한다 (누구에게 보낼지 같은 규칙은 여기 없음)
@Component
public class SseEmitterRepository {
    // 회원 번호 -> 그 회원의 통로 목록 (탭이 여러 개면 통로도 여러 개)
    // ConcurrentHashMap: 여러 요청이 동시에 써도 안 깨지는 Map
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // add 람다 안쓴 것
    void add(Long memberId, SseEmitter emitter) {
        // 그 회원의 목록을 꺼낸다 (없으면 null), 서버를 처음 켰을때는 없을테느 조건으로 넣음
        List<SseEmitter> list = emitters.get(memberId);
        if (list == null) {                                // 목록이 아직 없으면
            // CopyOnWriteArrayList<>()
            // 여러 요청이 동시에 읽고 써도 안 깨지는 ArrayList로,
            // 고칠 때마다 통째로 복사해서 돌던 쪽에 영향이 없게 한다

            list = new CopyOnWriteArrayList<>();           // 새 목록을 만들고
            emitters.put(memberId, list);                  // Map에 넣는다
        }
        list.add(emitter);                                 // 그 목록에 통로를 추가한다
    }
//
    // 통로를 넣는다
//    void add(Long memberId, SseEmitter emitter) {
//        // 그 회원의 목록이 없으면 새로 만들어 Map에 넣고, 있으면 그대로 돌려준다
//        // (CopyOnWriteArrayList: 여러 요청이 동시에 읽고 써도 안 깨지는 List)
//        emitters.computeIfAbsent(memberId, key -> new CopyOnWriteArrayList<>())
//                .add(emitter);                 // 그 목록에 통로를 추가한다
//    }

    // 그 회원의 통로 목록을 꺼낸다 (없으면 빈 목록)
    List<SseEmitter> findByMemberId(Long memberId) {
        // List.of() : 빈 list 목록
        return emitters.getOrDefault(memberId, List.of());
    }

    // 그 회원의 목록에서 통로를 뺀다 (연결이 끝났을 때 호출)
    void remove(Long memberId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(memberId);   // 그 회원의 목록 (없으면 null)
        if (list != null) {                                // 목록이 있을 때만
            list.remove(emitter);                          // 통로를 뺀다
        }
    }
}