package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final KafkaProducerService kafkaProducerService;


    @PostMapping("/chat")
    public void sendMessage(@RequestBody String message) {
        // 클라이언트에서 받은 메시지를 Kafka Topic으로 전송
        kafkaProducerService.sendMessage(message);
    }
}
