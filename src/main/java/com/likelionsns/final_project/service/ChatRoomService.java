package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.ChatRequestDto;
import com.likelionsns.final_project.domain.dto.ChatRoom;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import com.likelionsns.final_project.domain.response.MyChatRoomResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.RedisChatRoomRepository;
import com.likelionsns.final_project.repository.ChatRoomRepository;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.likelionsns.final_project.exception.ErrorCode.ALREADY_CHAT_ROOM;
import static com.likelionsns.final_project.exception.ErrorCode.USERNAME_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MongoTemplate mongoTemplate;


    @Transactional
    public Chat makeChatRoom(String userName, ChatRequestDto requestDto) {

        User findUser = findUserByUserName(userName);
        User joinUser = findUserByUserName(requestDto.getJoinUserName());

        chatRoomRepository.findActiveChat(findUser.getUserName(), joinUser.getUserName())
                .ifPresent(chat -> {
                    throw new SnsAppException(ALREADY_CHAT_ROOM, ALREADY_CHAT_ROOM.getMessage());
                });

        Chat chat = Chat.builder().createUser(findUser.getUserName()).joinUser(joinUser.getUserName()).regDate(LocalDateTime.now()).build();

        return chatRoomRepository.save(chat);
    }

    public Slice<MyChatRoomResponse> getChatRoomList(String userName, Pageable pageable) {
        User findUser = findUserByUserName(userName);

        // 사용자가 참여한 채팅방 목록을 조회
        Slice<Chat> chatRooms = chatRoomRepository.findChattingRoom(findUser.getUserName(), pageable);

        List<MyChatRoomResponse> chatRoomResponses = chatRooms.getContent().stream().map(chat -> {
                    User user;
                    if (!findUser.getUserName().equals(chat.getCreateUser())) {
                        user = findUserByUserName(chat.getCreateUser());
                    } else {
                        user = findUserByUserName(chat.getJoinUser());
                    }

                    long unReadMessages = countUnReadMessages(chat.getChatNo(), userName);

                    // 마지막 메시지와 메시지 시간 조회
                    Map<String, Object> lastMessageInfo = getLastMessageInfo(chat.getChatNo(), chat.getRegDate());
                    String lastMessage = (String) lastMessageInfo.get("content");
                    LocalDateTime lastMessageTime = (LocalDateTime) lastMessageInfo.get("sendDate");

                    return chat.toResponse(user, unReadMessages, lastMessage, lastMessageTime);
                })
                .sorted(Comparator.comparing(MyChatRoomResponse::getLastMessageTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), chatRoomResponses.size());
        List<MyChatRoomResponse> pagedList = chatRoomResponses.subList(start, end);

        return new SliceImpl<>(pagedList, pageable, end < chatRoomResponses.size());
    }


    @Transactional
    public void connectChatRoom(Integer chatRoomNo, String userName) {
        ChatRoom chatRoom = ChatRoom.builder()
                .userName(userName)
                .chatroomNo(chatRoomNo)
                .build();

        log.info("add redis : {}", chatRoom.getUserName());

        redisChatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void disconnectChatRoom(Integer chatRoomNo, String userName) {
        ChatRoom chatRoom = redisChatRoomRepository.findByChatroomNoAndUserName(chatRoomNo, userName)
                .orElseThrow(IllegalStateException::new);

        log.info("delete redis : {}", chatRoom.getUserName());

        redisChatRoomRepository.delete(chatRoom);
    }

    public boolean isAllConnected(Integer chatRoomNo) {
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        return connectedList.size() == 2;
    }

    public boolean isConnected(Integer chatRoomNo) {
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        return connectedList.size() == 1;
    }

    // 읽지 않은 메시지 카운트
    public long countUnReadMessages(Integer chatRoomNo, String senderName) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo).and("readCount").is(1).and("senderName").ne(senderName));

        return mongoTemplate.count(query, Chatting.class);
    }

    private Optional<Chatting> findLastMessage(Integer chatRoomNo) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo))
                .with(Sort.by(Sort.Order.desc("sendDate"))) // 최신 메시지를 기준으로 정렬
                .limit(1);

        return Optional.ofNullable(mongoTemplate.findOne(query, Chatting.class));
    }

    private Map<String, Object> getLastMessageInfo(Integer chatRoomNo, LocalDateTime regDate) {
        Optional<Chatting> lastMessage = findLastMessage(chatRoomNo);

        if (lastMessage.isPresent()) {
            Chatting chat = lastMessage.get();
            Map<String, Object> messageInfo = new HashMap<>();
            messageInfo.put("content", chat.getContent()); // 마지막 메시지 내용
            messageInfo.put("sendDate", chat.getSendDate()); // 마지막 메시지 시간
            return messageInfo;
        }

        // 메시지가 없는 경우 기본 값 반환
        return Map.of("content", "", "sendDate", regDate);
    }

    public void updateUnreadMessagesToRead(Integer chatRoomNo, String userName) {
        User findUser = userRepository.findByUserName(userName).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Update update = new Update().set("readCount", 0);
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo).and("senderName").ne(findUser.getUserName()));

        mongoTemplate.updateMulti(query, update, Chatting.class);
    }

    private User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
    }
}