package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.ChatRequestDto;
import com.likelionsns.final_project.domain.dto.ChatResponseDto;
import com.likelionsns.final_project.domain.dto.Message;
import com.likelionsns.final_project.domain.entity.Alarm;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import com.likelionsns.final_project.domain.response.ChattingHistoryResponseDto;
import com.likelionsns.final_project.domain.response.MyChatRoomResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.AlarmRepository;
import com.likelionsns.final_project.repository.ChatRepository;
import com.likelionsns.final_project.repository.UserRepository;
import com.likelionsns.final_project.repository.mongo.MongoChatRepository;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.likelionsns.final_project.domain.constant.KafkaConstants.*;
import static com.likelionsns.final_project.domain.enums.AlarmType.*;
import static com.likelionsns.final_project.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MongoChatRepository mongoChatRepository;

    private final MessageSender sender;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    private final AlarmRepository alarmRepository;

    private final ChatRoomService chatRoomService;

    @Value("${jwt.secret}")
    private String secretKey;


    @Transactional
    public Chat makeChatRoom(String userName, ChatRequestDto requestDto) {

        User findUser = userRepository.findByUserName(userName).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        chatRepository.findActiveChat(findUser.getId(), requestDto.getJoinUserId())
                .ifPresent(chat -> {
                    throw new SnsAppException(ALREADY_CHAT_ROOM, ALREADY_CHAT_ROOM.getMessage());
                });

        Chat chat = Chat.builder().createUser(findUser.getId()).joinUser(requestDto.getJoinUserId()).regDate(LocalDateTime.now()).build();

        return chatRepository.save(chat);
    }

    public ChattingHistoryResponseDto getChattingList(Integer chatRoomNo, String userName) {
        updateCountAllZero(chatRoomNo, userName);
        List<ChatResponseDto> chattingList = mongoChatRepository.findByChatRoomNo(chatRoomNo).stream().map(chat -> new ChatResponseDto(chat, userName)).collect(Collectors.toList());

        return ChattingHistoryResponseDto.builder().chatList(chattingList).userName(userName).build();
    }

    public List<MyChatRoomResponse> getChatRoomList(String userName) {
        // 사용자 이름으로 사용자 정보 조회
        User findUser = userRepository.findByUserName(userName).orElseThrow();

        // 사용자가 참여한 채팅방 목록을 조회하고, 각 채팅방 정보를 변환하여 리스트로 반환
        return chatRepository.findChattingRoom(findUser.getId()).stream().map(chat -> {
            User user;
            if (!Objects.equals(findUser.getId(), chat.getCreateUser())) {
                // 채팅 생성자와 사용자가 다른 경우, 생성자 정보를 조회
                user = userRepository.findById(chat.getCreateUser()).orElseThrow();
            } else {
                // 채팅 생성자와 사용자가 같은 경우, 조인 사용자 정보를 조회
                user = userRepository.findById(chat.getJoinUser()).orElseThrow();
            }

            // 읽지 않은 메시지 수와 마지막 메시지 내용 조회
            long unReadMessages = countUnReadMessages(chat.getChatNo(), userName);
            String lastMessage = findLastMessage(chat.getChatNo());

            // 채팅방 정보를 변환하여 반환
            return chat.toResponse(user, unReadMessages, lastMessage);
        }).collect(Collectors.toList());
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
    public Message sendNotificationAndSaveMessage(Message message, String userName) {

        // 메시지 저장과 알람 발송을 위해 메시지를 보낸 회원을 조회
        User sender = userRepository.findByUserName(message.getSenderName()).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));


        // 상대방이 읽지 않은 경우에만 알림 전송
        if (message.getReadCount().equals(1)) {
            // 알람 전송을 위해 메시지를 받는 사람을 조회한다.
            Chat findChat = chatRepository.findById(message.getChatNo()).orElseThrow();

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


    public void updateMessage(String userName, Integer chatRoomNo) {
        Message message = Message.builder().chatNo(chatRoomNo).content(userName + "님이 돌아오셨습니다.").build();

        sender.send(KAFKA_TOPIC, message);
    }


    // 읽지 않은 메시지 채팅장 입장시 읽음 처리
    public void updateCountAllZero(Integer chatNo, String userName) {
        User findUser = userRepository.findByUserName(userName).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));


        Update update = new Update().set("readCount", 0);
        Query query = new Query(Criteria.where("chatRoomNo").is(chatNo).and("senderName").ne(findUser.getUserName()));

        mongoTemplate.updateMulti(query, update, Chatting.class);
    }

    // 읽지 않은 메시지 카운트
    public long countUnReadMessages(Integer chatRoomNo, String senderName) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo).and("readCount").is(1).and("senderName").ne(senderName));

        return mongoTemplate.count(query, Chatting.class);
    }

    public String findLastMessage(Integer chatRoomNo) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo))
                .with(Sort.by(Sort.Order.desc("sendDate")))
                .limit(1);

        try {
            return mongoTemplate.findOne(query, Chatting.class).getContent();
        } catch (Exception e) {
            log.info(e.getMessage());
            return "";
        }

    }


}