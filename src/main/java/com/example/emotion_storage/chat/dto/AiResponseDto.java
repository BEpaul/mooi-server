package com.example.emotion_storage.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {
    private String response;
    private String sessionId;
    private LocalDateTime timestamp;
    private String emotion;
    private String confidence;
}
