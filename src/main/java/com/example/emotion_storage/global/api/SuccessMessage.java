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
    CHAT_SUCCESS("채팅 메시지 전송 성공");

    private final String message;
}
