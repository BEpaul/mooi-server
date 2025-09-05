package com.example.emotion_storage.chat.service;

import com.example.emotion_storage.chat.dto.AiMessageDto;
import com.example.emotion_storage.chat.dto.AiResponseDto;
import com.example.emotion_storage.global.config.websocket.WebSocketClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.net.URI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketClientService {

    private final WebSocketClientConfig webSocketClientConfig;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, CompletableFuture<AiResponseDto>> pendingRequests = new ConcurrentHashMap<>();
    private volatile WebSocketSession currentSession;

    public CompletableFuture<AiResponseDto> sendMessageToAI(AiMessageDto message) {
        CompletableFuture<AiResponseDto> future = new CompletableFuture<>();
        pendingRequests.put(message.getSessionId(), future);

        try {
            WebSocketSession session = getOrCreateSession();
            if (session != null && session.isOpen()) {
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
                log.info("메시지를 AI 서버로 전송했습니다: {}", messageJson);
            } else {
                future.completeExceptionally(new RuntimeException("AI 서버 연결 실패"));
            }
        } catch (Exception e) {
            log.error("AI 서버로 메시지 전송 중 오류 발생", e);
            future.completeExceptionally(e);
        }

        // 타임아웃 설정 (30초)
        future.orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    pendingRequests.remove(message.getSessionId());
                    return null;
                });

        return future;
    }

    private WebSocketSession getOrCreateSession() {
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = connectToAI();
        }
        return currentSession;
    }

    private WebSocketSession connectToAI() {
        try {
            WebSocketHandler handler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    log.info("AI 서버와 WebSocket 연결이 성공했습니다.");
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    if (message instanceof TextMessage) {
                        String payload = ((TextMessage) message).getPayload();
                        log.info("AI 서버로부터 응답을 받았습니다: {}", payload);
                        
                        AiResponseDto response = objectMapper.readValue(payload, AiResponseDto.class);
                        CompletableFuture<AiResponseDto> future = pendingRequests.remove(response.getSessionId());
                        if (future != null) {
                            future.complete(response);
                        }
                    }
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    log.error("WebSocket 전송 오류 발생", exception);
                    currentSession = null;
                    // 모든 대기 중인 요청에 대해 예외 처리
                    pendingRequests.values().forEach(future -> 
                        future.completeExceptionally(exception));
                    pendingRequests.clear();
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    log.info("AI 서버와의 WebSocket 연결이 종료되었습니다. 상태: {}", closeStatus);
                    currentSession = null;
                    // 모든 대기 중인 요청에 대해 예외 처리
                    pendingRequests.values().forEach(future -> 
                        future.completeExceptionally(new RuntimeException("WebSocket 연결 종료")));
                    pendingRequests.clear();
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };

            return webSocketClientConfig.webSocketClient()
                    .doHandshake(handler, null, URI.create(webSocketClientConfig.getAiWebSocketUrl()))
                    .get();

        } catch (Exception e) {
            log.error("AI 서버 연결 중 오류 발생", e);
            return null;
        }
    }
}
