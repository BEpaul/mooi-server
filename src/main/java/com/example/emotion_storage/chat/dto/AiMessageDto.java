package com.example.emotion_storage.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiMessageDto {
    private String type;
    private Payload payload;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Payload {
        @JsonProperty("session_id")
        private String sessionId;
        
        @JsonProperty("chat_prompt_message")
        private String chatPromptMessage;
        
        @JsonProperty("user_input")
        private String userInput;
    }

    public static AiMessageDto createChatStartMessage(String sessionId, String chatPromptMessage, String userInput) {
        return AiMessageDto.builder()
                .type("chat.start")
                .payload(Payload.builder()
                        .sessionId(sessionId)
                        .chatPromptMessage(chatPromptMessage)
                        .userInput(userInput)
                        .build())
                .build();
    }
}
