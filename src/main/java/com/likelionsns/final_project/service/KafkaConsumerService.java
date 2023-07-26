package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.entity.ChatMessage;
import com.likelionsns.final_project.domain.constant.KafkaConstants;
import com.likelionsns.final_project.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ChatMessageRepository chatMessageRepository;

    @KafkaListener(topics = KafkaConstants.CHAT_TOPIC)
    public void receiveMessage(String message) {
        // Kafka Topic으로부터 메시지를 소비하고 처리하는 로직을 추가합니다.
        log.info("Received message from Kafka: {}", message);

//        // 받은 메시지를 ChatMessage 객체로 변환하여 데이터베이스에 저장
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setContent(message);
//        chatMessageRepository.save(chatMessage);
    }
}