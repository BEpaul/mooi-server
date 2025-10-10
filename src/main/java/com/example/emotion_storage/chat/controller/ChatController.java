package com.example.emotion_storage.chat.controller;

import com.example.emotion_storage.chat.dto.request.ChatRequest;
import com.example.emotion_storage.chat.dto.response.AiChatResponse;
import com.example.emotion_storage.chat.service.ChatService;
import com.example.emotion_storage.global.api.ApiResponse;
import com.example.emotion_storage.global.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    @Operation(summary = "AI와 채팅 메시지 전송", description = "사용자 메시지를 AI 서버로 전송하고 응답을 받습니다.")
    public ResponseEntity<ApiResponse<AiChatResponse>> sendMessage(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        Long userId = userPrincipal != null ? userPrincipal.getId() : 1L; // TODO: 개발 테스트를 위한 코드
        log.info("사용자 {}가 채팅 메시지를 전송했습니다: {}", userId, request.getMessage());
        
        ApiResponse<AiChatResponse> response = chatService.sendMessage(request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-async")
    @Operation(summary = "비동기 채팅 메시지 전송", description = "사용자 메시지를 비동기로 AI 서버에 전송합니다.")
    public ResponseEntity<ApiResponse<AiChatResponse>> sendMessageAsync(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        Long userId = userPrincipal != null ? userPrincipal.getId() : 1L; // TODO: 개발 테스트를 위한 코드
        log.info("사용자 {}가 비동기 채팅 메시지를 전송했습니다: {}", userId, request.getMessage());
        
        ApiResponse<AiChatResponse> response = chatService.sendUserMessage(request, userId);
        return ResponseEntity.ok(response);
    }
}
