package com.ody.route.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DistanceMatrixElementStatus {

    OK("요청 성공"),
    NOT_FOUND("출발지 또는 목적지를 지오코딩할 수 없음"),
    ZERO_RESULTS("출발지와 목적지 사이에 경로를 찾을 수 없음"),
    MAX_ROUTE_LENGTH_EXCEEDED("요청된 경로가 너무 길어 처리할 수 없음");

    private final String description;
}
