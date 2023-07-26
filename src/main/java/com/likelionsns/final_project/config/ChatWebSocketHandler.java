package com.likelionsns.final_project.config;

import com.likelionsns.final_project.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final KafkaProducerService kafkaProducerService;


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 메시지를 받았을 때 동작하는 로직
        String payload = message.getPayload();
        log.info("Received message: {}", payload);

        // Kafka Producer를 통해 메시지를 Kafka Topic으로 전송
        kafkaProducerService.sendMessage(payload);
    }
}