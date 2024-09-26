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

        User findUser = userRepository.findByUserName(userName).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        User joinUser = userRepository.findByUserName(requestDto.getJoinUserName()).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));


        chatRoomRepository.findActiveChat(findUser.getId(), joinUser.getId())
                .ifPresent(chat -> {
                    throw new SnsAppException(ALREADY_CHAT_ROOM, ALREADY_CHAT_ROOM.getMessage());
                });

        Chat chat = Chat.builder().createUser(findUser.getId()).joinUser(joinUser.getId()).regDate(LocalDateTime.now()).build();

        return chatRoomRepository.save(chat);
    }

    public List<MyChatRoomResponse> getChatRoomList(String userName) {
        // 사용자 이름으로 사용자 정보 조회
        User findUser = userRepository.findByUserName(userName).orElseThrow();

        // 사용자가 참여한 채팅방 목록을 조회하고, 각 채팅방 정보를 변환하여 리스트로 반환
        return chatRoomRepository.findChattingRoom(findUser.getId()).stream().map(chat -> {
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

    public void updateUnreadMessagesToRead(Integer chatRoomNo, String userName) {
        User findUser = userRepository.findByUserName(userName).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Update update = new Update().set("readCount", 0);
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo).and("senderName").ne(findUser.getUserName()));

        mongoTemplate.updateMulti(query, update, Chatting.class);
    }
}