package com.likelionsns.final_project.service;


import com.likelionsns.final_project.domain.constant.KafkaConstants;
import com.likelionsns.final_project.domain.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReceiver {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(Message message) {
        messagingTemplate.convertAndSend("/subscribe/" + message.getChatNo(), message);
    }
}
