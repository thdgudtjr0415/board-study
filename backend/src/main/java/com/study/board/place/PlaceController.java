package com.study.board.place;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final KakaoPlaceClient kakaoPlaceClient;

    public PlaceController(KakaoPlaceClient kakaoPlaceClient) {
        this.kakaoPlaceClient = kakaoPlaceClient;
    }

    // 임시: 카카오 응답을 글자 그대로 돌려줘서 모양을 확인한다
    @GetMapping
    public String search(@RequestParam String query) {
        return kakaoPlaceClient.searchKeyword(query);
    }
}