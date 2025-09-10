package com.example.emotion_storage.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiResponseDto {
    private String type;
    private String text; // chat.delta 메시지의 text 필드
    private String message; // error 메시지용
    private String sessionId;
    private String fullResponse;
    
    public String getResponse() {
        return fullResponse != null ? fullResponse : text;
    }
    
    public String getTimestamp() {
        return java.time.LocalDateTime.now().toString();
    }
    
    public String getEmotion() {
        return null;
    }
    
    public String getConfidence() {
        return null;
    }
    
    // 팩토리 메서드들
    public static AiResponseDto createChatComplete(String sessionId, String fullResponse) {
        return AiResponseDto.builder()
                .type("chat.complete")
                .sessionId(sessionId)
                .fullResponse(fullResponse)
                .build();
    }
    
    public static AiResponseDto createError(String message) {
        return AiResponseDto.builder()
                .type("error")
                .message(message)
                .build();
    }
}
