package com.study.board.place;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final KakaoPlaceClient kakaoPlaceClient;

    public PlaceController(KakaoPlaceClient kakaoPlaceClient) {
        this.kakaoPlaceClient = kakaoPlaceClient;
    }

    // 임시: 카카오 응답을 글자 그대로 돌려줘서 모양을 확인한다
    // 여기서 쿼리들은 http://localhost:8080/places?query=강남역 에서 뒤에 강남역 부분
//    @GetMapping
//    public KakaoSearchResponse search(@RequestParam String query) {
//        return kakaoPlaceClient.searchKeyword(query);
//    }

    // 후 (고친 것)
    @GetMapping
    public List<PlaceResponse> search(@RequestParam String query) {
        KakaoSearchResponse result = kakaoPlaceClient.searchKeyword(query);
        return result.documents().stream()
                .map(PlaceResponse::from)
                .toList();
    }
}