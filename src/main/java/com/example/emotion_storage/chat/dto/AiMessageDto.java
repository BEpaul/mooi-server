package com.example.emotion_storage.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiMessageDto {
    private String message;
    private String sessionId;
    private Long userId;
}
