package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.constant.KafkaConstants;
import com.likelionsns.final_project.domain.dto.ChatRequestDto;
import com.likelionsns.final_project.domain.dto.Message;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.ChatRepository;
import com.likelionsns.final_project.repository.UserRepository;
import com.likelionsns.final_project.repository.mongo.MongoChatRepository;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.likelionsns.final_project.domain.constant.KafkaConstants.*;
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

    private final ChatRoomService chatRoomService;

    @Value("${jwt.secret}")
    private String secretKey;


    @Transactional
    public Chat makeChatRoom(String userName, ChatRequestDto requestDto) {

        User findUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Chat chat = Chat.builder()
                .createUser(findUser.getId())
                .joinUser(requestDto.getJoinUserId())
                .regDate(LocalDateTime.now())
                .build();

        Chat savedChat = chatRepository.save(chat);

        // 채팅방 카운트 증가
//        AggregationDto aggregationDto = AggregationDto
//                .builder()
//                .isIncrease("true")
//                .target(AggregationTarget.CHAT)
//                .saleNo(requestDto.getSaleNo())
//                .build();
//
//        aggregationSender.send(ConstantUtil.KAFKA_AGGREGATION, aggregationDto);
        return savedChat;
    }

//    public List<ChatRoomResponseDto> getChatList(SecurityUserDto userDto, Integer saleNo) {
//        List<ChatRoomResponseDto> chatRoomList = chatQueryService.getChattingList(userDto.getMemberNo(), saleNo);
//
//            chatRoomList
//                    .forEach(chatRoomDto -> {
//                        // 채팅방별로 읽지 않은 메시지 개수를 셋팅
//                        long unReadCount = countUnReadMessages(chatRoomDto.getChatNo(), userDto.getMemberNo());
//                        chatRoomDto.setUnReadCount(unReadCount);
//
//                        // 채팅방별로 마지막 채팅내용과 시간을 셋팅
//                        Page<Chatting> chatting =
//                                mongoChatRepository.findByChatRoomNoOrderBySendDateDesc(chatRoomDto.getChatNo(), PageRequest.of(0, 1));
//                        if (chatting.hasContent()) {
//                            Chatting chat = chatting.getContent().get(0);
//                            ChatRoomResponseDto.LatestMessage latestMessage = ChatRoomResponseDto.LatestMessage.builder()
//                                    .context(chat.getContent())
//                                    .sendAt(chat.getSendDate())
//                                    .build();
//                            chatRoomDto.setLatestMessage(latestMessage);
//                        }
//                    });
//
//        return chatRoomList;
//    }
//
//    public ChattingHistoryResponseDto getChattingList(Integer chatRoomNo, SecurityUserDto user) {
//        updateCountAllZero(chatRoomNo, user.getEmail());
//        List<ChatResponseDto> chattingList = mongoChatRepository.findByChatRoomNo(chatRoomNo)
//                .stream()
//                .map(chat -> new ChatResponseDto(chat, user.getMemberNo()))
//                .collect(Collectors.toList());
//
//        return ChattingHistoryResponseDto.builder()
//                .chatList(chattingList)
//                .email(user.getEmail())
//                .build();
//    }


    public void sendMessage(Message message, String accessToken) {
        // 메시지 전송 요청 헤더에 포함된 Access Token에서 email로 회원을 조회한다.
        User user = userRepository.findByUserName(JwtUtils.getUserName(accessToken, secretKey))
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));


        // 채팅방에 모든 유저가 참여중인지 확인한다.
        boolean isConnectedAll = chatRoomService.isAllConnected(message.getChatNo());
        // 1:1 채팅이므로 2명 접속시 readCount 0, 한명 접속시 1
        Integer readCount = isConnectedAll ? 0 : 1;
        // message 객체에 보낸시간, 보낸사람 memberNo, 닉네임을 셋팅해준다.
        message.setSendTimeAndSender(LocalDateTime.now(), user.getUserName(), readCount);
        // 메시지를 전송한다.
        sender.send(KAFKA_TOPIC, message);
    }
//    @Transactional
//    public Message sendNotificationAndSaveMessage(Message message) {
//        // 메시지 저장과 알람 발송을 위해 메시지를 보낸 회원을 조회
//        User findUser = userRepository.findByUserName(message.getSenderName())
//                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
//
//
//        // 상대방이 읽지 않은 경우에만 알림 전송
//        if (message.getReadCount().equals(1)) {
//            // 알람 전송을 위해 메시지를 받는 사람을 조회한다.
//            Member receiveMember = chatQueryService.getReceiverNumber(message.getChatNo(), message.getSenderNo());
//            String content =
//                    message.getContentType().equals("image") ? "image" : message.getContent();
//            // 알람을 보낼 URL을 생성한다.
//            String sendUrl = getNotificationUrl(message.getSaleNo(), message.getChatNo());
//
//            // 알림을 전송한다.
//            notificationService.send(findMember, receiveMember, NotifiTypeEnum.CHAT, sendUrl, content);
//        }
//
//        // 보낸 사람일 경우에만 메시지를 저장 -> 중복 저장 방지
//        if (message.getSenderEmail().equals(findUser.getUserName())) {
//            // Message 객체를 채팅 엔티티로 변환한다.
//            Chatting chatting = message.convertEntity();
//            // 채팅 내용을 저장한다.
//            Chatting savedChat = mongoChatRepository.save(chatting);
//            // 저장된 고유 ID를 반환한다.
//            message.setId(savedChat.getId());
//        }
//
//        return message;
//    }


    public void updateMessage(String email, Integer chatRoomNo) {
        Message message = Message.builder()
                .chatNo(chatRoomNo)
                .content(email + " 님이 돌아오셨습니다.")
                .build();

        sender.send(KAFKA_TOPIC, message);
    }


    // 읽지 않은 메시지 채팅장 입장시 읽음 처리
    public void updateCountAllZero(Integer chatNo, String userName) {
        User findUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Update update = new Update().set("readCount", 0);
        Query query = new Query(Criteria.where("chatRoomNo").is(chatNo)
                .and("senderNo").ne(findUser.getId()));

        mongoTemplate.updateMulti(query, update, Chatting.class);
    }

    // 읽지 않은 메시지 카운트
    long countUnReadMessages(Integer chatRoomNo, Integer senderNo) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo)
                .and("readCount").is(1)
                .and("senderNo").ne(senderNo));

        return mongoTemplate.count(query, Chatting.class);
    }

    private String getNotificationUrl(Integer saleNo, Integer chatNo) {
        return chatNo +
                "?adoptId=" +
                saleNo;
    }


}