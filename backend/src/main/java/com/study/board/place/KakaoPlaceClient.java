package com.study.board.place;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class KakaoPlaceClient {

    private final RestClient restClient;

    public KakaoPlaceClient(@Value("${kakao.rest-key}") String restKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://dapi.kakao.com")                       // 카카오 서버 주소
                .defaultHeader("Authorization", "KakaoAK " + restKey)    // 모든 요청에 붙는 증표(앱 키)
                .build();
    }

    // 키워드로 장소를 검색하고, 응답 JSON을 글자 그대로 돌려준다 (먼저 눈으로 보려고)
    public KakaoSearchResponse searchKeyword(String query) {
        try {
            return restClient.get()
                    .uri("/v2/local/search/keyword.json?query={query}", query)
//                .uri("/v2/local/search/keyword-wrong.json?query={query}", query)  /*틀린 url 이면 예외가 나옴*/
                    .retrieve()
                    .body(KakaoSearchResponse.class);
        } catch (RestClientException e) {
            log.warn("카카오 장소 검색 실패 {}", e.getClass().getSimpleName());   // 예외 종류만 남긴다
            throw new ExternalApiException("장소 검색 서비스에 문제가 있습니다. 잠시 후 다시 시도해 주세요.");
        }
    }
}
