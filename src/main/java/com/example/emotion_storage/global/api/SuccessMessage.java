package com.example.emotion_storage.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    LOGIN_SUCCESS("로그인 성공"),
    SIGNUP_SUCCESS("회원가입 성공"),
    ACCESS_TOKEN_REISSUE_SUCCESS("액세스 토큰 재발급 성공"),
    SESSION_CHECK_SUCCESS("세션 확인 성공"),

    CHAT_ROOM_CREATE_SUCCESS("대화 시작 성공"),
    CHAT_ROOM_CLOSE_SUCCESS("대화 종료 성공"),

    // Home
    GET_TICKETS_SUCCESS("티켓 개수 조회 성공"),
    GET_KEYS_SUCCESS("열쇠 개수 조회 성공"),
    GET_NEW_TIME_CAPSULE_SUCCESS("도착한 타임캡슐 여부 조회 성공"),
    GET_NEW_DAILY_REPORT_SUCCESS("새로운 일일리포트 여부 조회 성공"),
    GET_NEW_NOTIFICATION_SUCCESS("신규 알림 여부 조회 성공"),
    GET_HOME_INFO_SUCCESS("홈 화면 정보 API 조회 성공"),

    // Report
    DAILY_REPORT_DETAIL_GET_SUCCESS("일일리포트 상세 조회 성공");

    private final String message;
}
