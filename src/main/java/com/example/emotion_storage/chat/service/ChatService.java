package com.example.emotion_storage.chat.service;

import com.example.emotion_storage.chat.dto.AiMessageDto;
import com.example.emotion_storage.chat.dto.AiResponseDto;
import com.example.emotion_storage.chat.dto.ChatPromptMessages;
import com.example.emotion_storage.chat.dto.request.ChatRequest;
import com.example.emotion_storage.chat.dto.response.ChatResponse;
import com.example.emotion_storage.global.api.ApiResponse;
import com.example.emotion_storage.global.api.SuccessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final WebSocketClientService webSocketClientService;

    public ApiResponse<ChatResponse> sendMessage(ChatRequest request, String userId) {
        try {
            String sessionId = request.getSessionId() != null ?
                request.getSessionId() : userId + "-" + UUID.randomUUID().toString();

            AiMessageDto aiMessage = AiMessageDto.createChatStartMessage(
                sessionId,
                ChatPromptMessages.EMOTION_ANALYSIS.getMessage(),
                request.getMessage()
            );

            log.info("사용자 {}의 메시지를 AI 서버로 전송합니다: {}", userId, request.getMessage());

            // AI 서버로 메시지 전송 및 응답 대기
            AiResponseDto aiResponse = webSocketClientService.sendMessageToAI(aiMessage).get();

            // AI 응답을 클라이언트용 응답으로 변환
            ChatResponse response = new ChatResponse(
                aiResponse.getResponse(),
                sessionId,
                aiResponse.getTimestamp(),
                true
            );

            log.info("AI 서버로부터 응답을 받았습니다: {}", aiResponse.getResponse());

            return ApiResponse.success(SuccessMessage.CHAT_SUCCESS.getMessage(), response);

        } catch (Exception e) {
            log.error("채팅 메시지 처리 중 오류 발생", e);
            throw new RuntimeException("AI 서버와의 통신 중 오류가 발생했습니다.", e);
        }
    }

    public ApiResponse<ChatResponse> sendUserMessage(ChatRequest request, String userId) {
        try {
            String sessionId = request.getSessionId() != null ?
                request.getSessionId() : userId + "-" + UUID.randomUUID().toString();

            log.info("사용자 {}가 비동기 메시지를 전송했습니다: {}", userId, request.getMessage());

            // 사용자 메시지에 대한 즉시 응답 (AI 응답 없이)
            ChatResponse response = new ChatResponse(
                "메시지를 받았습니다. AI가 응답을 준비 중입니다...",
                sessionId,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                false
            );

            // 백그라운드에서 AI 서버로 메시지 전송
            AiMessageDto aiMessage = AiMessageDto.createChatStartMessage(
                sessionId,
                ChatPromptMessages.EMOTION_ANALYSIS.getMessage(),
                request.getMessage()
            );

            // 비동기로 AI 응답 처리
            webSocketClientService.sendMessageToAI(aiMessage)
                .thenAccept(aiResponse -> {
                    log.info("사용자 {}의 AI 응답을 받았습니다: {}", userId, aiResponse.getResponse());
                    // 여기서 WebSocket을 통해 클라이언트에게 실시간으로 응답 전송
                    // TODO: 실시간 전송

                })
                .exceptionally(throwable -> {
                    log.error("사용자 {}의 AI 응답 처리 중 오류 발생", userId, throwable);
                    return null;
                });

            return ApiResponse.success(SuccessMessage.CHAT_SUCCESS.getMessage(), response);

        } catch (Exception e) {
            log.error("사용자 메시지 처리 중 오류 발생", e);
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다.", e);
        }
    }
}
