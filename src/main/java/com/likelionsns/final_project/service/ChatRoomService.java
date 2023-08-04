package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.ChatRoom;
import com.likelionsns.final_project.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public void connectChatRoom(Integer chatRoomNo, String userName) {
        ChatRoom chatRoom = ChatRoom.builder()
                        .userName(userName)
                        .chatroomNo(chatRoomNo)
                        .build();

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void disconnectChatRoom(Integer chatRoomNo, String userName) {
        ChatRoom chatRoom = chatRoomRepository.findByChatroomNoAndUserName(chatRoomNo, userName)
                        .orElseThrow(IllegalStateException::new);

        chatRoomRepository.delete(chatRoom);
    }

    public boolean isAllConnected(Integer chatRoomNo) {
        List<ChatRoom> connectedList = chatRoomRepository.findByChatroomNo(chatRoomNo);
        return connectedList.size() == 2;
    }

    public boolean isConnected(Integer chatRoomNo) {
        List<ChatRoom> connectedList = chatRoomRepository.findByChatroomNo(chatRoomNo);
        return connectedList.size() == 1;
    }
}