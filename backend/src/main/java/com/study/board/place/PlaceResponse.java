package com.study.board.place;

public record PlaceResponse(String name, String address, String roadAddress,
                            String phone, double longitude, double latitude, String url) {

    // 카카오 응답의 장소 하나를 우리 응답으로 바꾼다 (x는 경도, y는 위도, 글자로 오니 숫자로)
    public static PlaceResponse from(KakaoSearchResponse.Document d){
        return new PlaceResponse(
                d.placeName(), d.addressName(),d.roadAddressName(),d.phone(),
                Double.parseDouble(d.x()),Double.parseDouble(d.y()), d.placeUrl());
    }
}
