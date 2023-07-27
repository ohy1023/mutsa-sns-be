package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.domain.dto.ChatRequestDto;
import com.likelionsns.final_project.domain.dto.Message;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.service.ChatRoomService;
import com.likelionsns.final_project.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    @PostMapping("/chatroom")
    public ResponseEntity<Response<Chat>> createChatRoom(@RequestBody ChatRequestDto requestDto, Authentication authentication) {

        // 채팅방을 만들어준다.
        Chat chat = chatService.makeChatRoom(authentication.getName(), requestDto);

        return ResponseEntity.ok(Response.success(chat));
    }

    // 채팅내역 조회
//    @GetMapping("/chatroom/{roomNo}")
//    public ResponseEntity<ChattingHistoryResponseDto> chattingList(@PathVariable("roomNo") Integer roomNo) {
//        ChattingHistoryResponseDto chattingList = chatService.getChattingList(roomNo, SecurityUtils.getUser());
//        return ResponseEntity.ok(chattingList);
//    }

//     채팅방 리스트 조회
//    @GetMapping("/chatroom")
//    public ResponseEntity<List<ChatRoomResponseDto>> chatRoomList(@RequestParam(value = "saleNo", required = false) final Integer saleNo) {
//        List<ChatRoomResponseDto> chatList = chatService.getChatList(SecurityUtils.getUser(), saleNo);
//        return ResponseEntity.ok(chatList);
//    }

    @MessageMapping("/message")
    public void sendMessage(Message message, @Header("Authorization") final String accessToken) {
        chatService.sendMessage(message, accessToken);
    }

    // 채팅방 접속 끊기
    @PostMapping("/chatroom/{chatroomNo}")
    public ResponseEntity<Response<String>> disconnectChat(@PathVariable("chatroomNo") Integer chatroomNo,
                                                            @RequestParam("userName") String userName) {

        chatRoomService.disconnectChatRoom(chatroomNo, userName);
        return ResponseEntity.ok(Response.success("접속 끊기"));
    }


    // 메시지 전송 후 callback
//    @PostMapping("/chatroom/notification")
//    public ResponseEntity<Message> sendNotification(@RequestBody Message message) {
//        Message savedMessage = chatService.sendNotificationAndSaveMessage(message);
//        return ResponseEntity.ok(savedMessage);
//    }
}