package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.constant.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {


    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        // Kafka Producer를 통해 메시지를 Kafka Topic으로 전송
        kafkaTemplate.send(KafkaConstants.CHAT_TOPIC, message);
    }
}
