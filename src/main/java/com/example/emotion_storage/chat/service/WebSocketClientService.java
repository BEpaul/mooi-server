package com.example.emotion_storage.chat.service;

import com.example.emotion_storage.chat.dto.AiMessageDto;
import com.example.emotion_storage.chat.dto.AiResponseDto;
import com.example.emotion_storage.global.config.websocket.WebSocketClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;

import java.net.URI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketClientService {

    // 상수 정의
    private static final int REQUEST_TIMEOUT_SECONDS = 30;
    private static final String MESSAGE_TYPE_CHAT_DELTA = "chat.delta";
    private static final String MESSAGE_TYPE_CHAT_END = "chat.end";
    private static final String MESSAGE_TYPE_ERROR = "error";

    private final WebSocketClientConfig webSocketClientConfig;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, CompletableFuture<AiResponseDto>> pendingRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, StringBuilder> responseBuilders = new ConcurrentHashMap<>();
    private volatile WebSocketSession currentSession;
    private volatile String currentSessionId;

    public CompletableFuture<AiResponseDto> sendMessageToAI(AiMessageDto message) {
        CompletableFuture<AiResponseDto> future = new CompletableFuture<>();
        String sessionId = message.getPayload().getSessionId();

        // 요청 등록
        registerRequest(sessionId, future);

        try {
            WebSocketSession session = getOrCreateSession();
            if (session != null && session.isOpen()) {
                sendMessageToSession(session, message);
            } else {
                future.completeExceptionally(new RuntimeException("AI 서버 연결 실패"));
            }
        } catch (Exception e) {
            log.error("AI 서버로 메시지 전송 중 오류 발생", e);
            future.completeExceptionally(e);
        }

        // 타임아웃 설정
        setupTimeout(future, sessionId);

        return future;
    }

    private void registerRequest(String sessionId, CompletableFuture<AiResponseDto> future) {
        pendingRequests.put(sessionId, future);
        responseBuilders.put(sessionId, new StringBuilder());
        currentSessionId = sessionId;
    }

    private void sendMessageToSession(WebSocketSession session, AiMessageDto message) throws Exception {
        String messageJson = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(messageJson));
        log.info("메시지를 AI 서버로 전송했습니다: {}", messageJson);
    }

    private void setupTimeout(CompletableFuture<AiResponseDto> future, String sessionId) {
        future.orTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    cleanupRequest(sessionId);
                    return null;
                });
    }

    private void cleanupRequest(String sessionId) {
        pendingRequests.remove(sessionId);
        responseBuilders.remove(sessionId);
    }

    private void cleanupAllRequests(Throwable exception) {
        pendingRequests.values().forEach(future -> future.completeExceptionally(exception));
        pendingRequests.clear();
        responseBuilders.clear();
    }

    private WebSocketSession getOrCreateSession() {
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = connectToAI();
        }
        return currentSession;
    }

    private WebSocketSession connectToAI() {
        try {
            WebSocketHandler handler = createWebSocketHandler();
            WebSocketClient client = webSocketClientConfig.webSocketClient();
            URI uri = URI.create(webSocketClientConfig.getAiWebSocketUrl());

            CompletableFuture<WebSocketSession> future = client.execute(handler, null, uri);
            return future.get();

        } catch (Exception e) {
            log.error("AI 서버 연결 중 오류 발생", e);
            return null;
        }
    }

    private WebSocketHandler createWebSocketHandler() {
        return new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("AI 서버와 WebSocket 연결이 성공했습니다.");
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                if (message instanceof TextMessage) {
                    handleTextMessage((TextMessage) message);
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                log.error("WebSocket 전송 오류 발생", exception);
                currentSession = null;
                cleanupAllRequests(exception);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("AI 서버와의 WebSocket 연결이 종료되었습니다. 상태: {}", closeStatus);
                currentSession = null;
                cleanupAllRequests(new RuntimeException("WebSocket 연결 종료"));
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };
    }

    private void handleTextMessage(TextMessage message) {
        String payload = message.getPayload();
        log.info("AI 서버로부터 응답을 받았습니다: {}", payload);

        try {
            AiResponseDto response = objectMapper.readValue(payload, AiResponseDto.class);
            String type = response.getType();

            switch (type) {
                case MESSAGE_TYPE_CHAT_DELTA:
                    handleChatDeltaMessage(response);
                    break;
                case MESSAGE_TYPE_CHAT_END:
                    handleChatEndMessage(response);
                    break;
                case MESSAGE_TYPE_ERROR:
                    handleErrorMessage(response);
                    break;
                default:
                    log.warn("알 수 없는 메시지 타입: {}", type);
            }
        } catch (Exception e) {
            log.error("AI 응답 파싱 중 오류 발생: {}", payload, e);
        }
    }

    private void handleChatDeltaMessage(AiResponseDto response) {
        String sessionId = getCurrentSessionId();
        if (sessionId != null) {
            StringBuilder builder = responseBuilders.get(sessionId);
            if (builder != null && response.getText() != null) {
                builder.append(response.getText());
                log.debug("텍스트 누적: {}", response.getText());
            }
        } else {
            log.warn("chat.delta 메시지에서 세션 ID를 찾을 수 없습니다");
        }
    }

    private void handleChatEndMessage(AiResponseDto response) {
        String sessionId = getCurrentSessionId();
        if (sessionId != null) {
            CompletableFuture<AiResponseDto> future = pendingRequests.remove(sessionId);
            StringBuilder builder = responseBuilders.remove(sessionId);

            if (future != null && builder != null) {
                String fullResponse = builder.toString();
                AiResponseDto finalResponse = AiResponseDto.createChatComplete(sessionId, fullResponse);
                future.complete(finalResponse);
                log.info("AI 응답 완료: {}", fullResponse);
            }
        } else {
            log.warn("chat.end 메시지에서 세션 ID를 찾을 수 없습니다");
        }
    }

    private void handleErrorMessage(AiResponseDto response) {
        String sessionId = getCurrentSessionId();
        if (sessionId != null) {
            CompletableFuture<AiResponseDto> future = pendingRequests.remove(sessionId);
            responseBuilders.remove(sessionId);

            if (future != null) {
                future.completeExceptionally(new RuntimeException("AI 서버 오류: " + response.getMessage()));
            }
        } else {
            log.warn("error 메시지에서 세션 ID를 찾을 수 없습니다");
        }
    }

    private String getCurrentSessionId() {
        // 현재 처리 중인 세션 ID를 반환
        // AI 서버의 응답에는 session_id가 포함되지 않으므로 현재 세션 ID 사용
        if (currentSessionId != null && pendingRequests.containsKey(currentSessionId)) {
            return currentSessionId;
        }

        // 백업: 대기 중인 첫 번째 세션 ID 반환
        if (!pendingRequests.isEmpty()) {
            return pendingRequests.keySet().iterator().next();
        }

        log.warn("세션 ID를 찾을 수 없습니다.");
        return null;
    }
}
