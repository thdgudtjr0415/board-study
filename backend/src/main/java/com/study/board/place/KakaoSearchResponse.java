package com.study.board.place;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// 카카오가 준 JSON을 담는 틀. JSON 모양 그대로 안에 안이 있음: { documents: [장소들], meta: {검색 정보} }
// ignoreUnknown = true : 틀에 없는 필드(id, distance 등)가 와도 에러 없이 무시
// @JsonProperty("JSON 이름") : JSON은 place_name(밑줄), 자바는 placeName이라 이름이 달라서 연결해 줌 (이름이 같으면 안 써도 됨)
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoSearchResponse(List<Document> documents, Meta meta) {   // documents = 장소 목록, meta = 검색 정보

    // 장소 하나
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Document(
                            @JsonProperty("place_name") String placeName,             // 장소 이름
                            @JsonProperty("address_name") String addressName,         // 지번 주소
                            @JsonProperty("road_address_name") String roadAddressName, // 도로명 주소
                            String phone,                                             // 전화번호 (없으면 빈 글자)
                            String x,                                                 // 경도 (글자로 옴)
                            String y,                                                 // 위도 (글자로 옴)
                            @JsonProperty("place_url") String placeUrl){              // 카카오맵 장소 페이지
    }

    // 검색 정보
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
            @JsonProperty("total_count") int totalCount,   // 검색에 걸린 전체 개수
            @JsonProperty("is_end") boolean isEnd){        // 마지막 페이지인가 (false면 더 있음)
    }
}
