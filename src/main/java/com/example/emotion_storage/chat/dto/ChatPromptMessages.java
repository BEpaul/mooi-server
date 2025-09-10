package com.example.emotion_storage.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatPromptMessages {
    
    EMOTION_ANALYSIS("안녕하세요! 감정을 분석해드릴게요."),
    GENERAL_CHAT("안녕하세요! 무엇을 도와드릴까요?"),
    EMOTION_SUPPORT("감정적으로 힘든 시간을 보내고 계시는군요. 함께 이야기해보아요."),
    DAILY_CHECK("오늘 하루는 어떠셨나요? 기분을 나누어보세요.");
    
    private final String message;
}
