package com.example.emotion_storage.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private String sessionId;
    private String timestamp;
    private boolean isFromAI;
}
