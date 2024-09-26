package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.ChatResponseDto;
import com.likelionsns.final_project.domain.dto.Message;
import com.likelionsns.final_project.domain.entity.Alarm;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import com.likelionsns.final_project.domain.response.ChattingHistoryResponseDto;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.AlarmRepository;
import com.likelionsns.final_project.repository.ChatRoomRepository;
import com.likelionsns.final_project.repository.UserRepository;
import com.likelionsns.final_project.repository.mongo.MongoChatRepository;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.likelionsns.final_project.domain.constant.KafkaConstants.*;
import static com.likelionsns.final_project.domain.enums.AlarmType.*;
import static com.likelionsns.final_project.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MongoChatRepository mongoChatRepository;
    private final MessageSender sender;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final ChatRoomService chatRoomService;

    @Value("${jwt.secret}")
    private String secretKey;

    public ChattingHistoryResponseDto getChattingList(Integer chatRoomNo, String userName) {
        List<ChatResponseDto> chattingList = mongoChatRepository.findByChatRoomNo(chatRoomNo).stream().map(chat -> new ChatResponseDto(chat, userName)).collect(Collectors.toList());
        return ChattingHistoryResponseDto.builder().chatList(chattingList).userName(userName).build();
    }

    public void sendMessage(Message message, String accessToken) {

        String token = JwtUtils.extractToken(accessToken);

        // 메시지 전송 요청 헤더에 포함된 AccessToken에서 userName추출해 회원을 조회한다.
        User user = userRepository.findByUserName(JwtUtils.getUserName(token, secretKey)).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        // 채팅방에 모든 유저가 참여중인지 확인한다.
        boolean isConnectedAll = chatRoomService.isAllConnected(message.getChatNo());
        // 1:1 채팅이므로 2명 접속시 readCount 0, 한명 접속시 1
        Integer readCount = isConnectedAll ? 0 : 1;
        // message 객체에 보낸시간, 보낸사람을 셋팅해준다.
        message.setSendTimeAndSender(LocalDateTime.now(), user.getUserName(), readCount);
        // 메시지를 전송한다.
        sender.send(KAFKA_TOPIC, message);
    }

    @Transactional
    public Message sendAlarmAndSaveMessage(Message message, String userName) {

        // 메시지 저장과 알람 발송을 위해 메시지를 보낸 회원을 조회
        User sender = userRepository.findByUserName(message.getSenderName()).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));


        // 상대방이 읽지 않은 경우에만 알림 전송
        if (message.getReadCount() == 1) {
            // 알람 전송을 위해 메시지를 받는 사람을 조회한다.
            Chat findChat = chatRoomRepository.findById(message.getChatNo()).orElseThrow();

            User recipient = userRepository.findById(findChat.getJoinUser()).orElseThrow();

            alarmRepository.save(Alarm.builder().user(recipient).alarmType(NEW_CHAT).text(NEW_CHAT.getAlarmText()).targetId(recipient.getId()).fromUserId(sender.getId()).build());
        }

        // 보낸 사람일 경우에만 메시지를 저장 -> 중복 저장 방지
        if (message.getSenderName().equals(userName)) {
            // Message 객체를 채팅 엔티티로 변환한다.
            Chatting chatting = message.toEntity();
            // 채팅 내용을 저장한다.
            Chatting savedChat = mongoChatRepository.save(chatting);
            // 저장된 고유 ID를 반환한다.
            message.setId(savedChat.getId());
        }

        return message;
    }


    public void updateMessage(String userName, int chatRoomNo) {
        Message message = Message.builder()
                .chatNo(chatRoomNo)
                .content(userName + "님이 입장했습니다.")
                .senderName("notice")
                .build();

        sender.send(KAFKA_TOPIC, message);
    }

    public void leaveMessage(String userName, int chatRoomNo) {
        Message message = Message.builder()
                .chatNo(chatRoomNo)
                .content(userName + "님이 방을 나갔습니다.")
                .senderName("notice")
                .build();

        sender.send(KAFKA_TOPIC, message);
    }
}