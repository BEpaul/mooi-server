package com.example.emotion_storage.global.config.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class WebSocketClientConfig {

    @Value("${ai.websocket.url}")
    private String aiWebSocketUrl;

    @Bean
    public StandardWebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    public String getAiWebSocketUrl() {
        return aiWebSocketUrl;
    }
}
