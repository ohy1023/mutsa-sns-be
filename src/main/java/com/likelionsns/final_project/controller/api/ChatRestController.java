package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.ChatRequestDto;
import com.likelionsns.final_project.domain.dto.Message;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.response.ChattingHistoryResponseDto;
import com.likelionsns.final_project.domain.response.MyChatRoomResponse;
import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.service.ChatRoomService;
import com.likelionsns.final_project.service.ChatService;
import com.likelionsns.final_project.service.MessageReceiver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    private final MessageReceiver receiver;

    @PostMapping("/chatroom")
    public ResponseEntity<Response<Chat>> createChatRoom(@RequestBody ChatRequestDto requestDto, Authentication authentication) {
        String myName = authentication.getName();

        log.info("userName: {}", myName);

        // 채팅방을 만들어준다.
        Chat chat = chatService.makeChatRoom(myName, requestDto);

        return ResponseEntity.ok(Response.success(chat));
    }


    // 채팅내역 조회
    @GetMapping("/chatroom/{roomNo}")
    public ResponseEntity<Response<ChattingHistoryResponseDto>> chattingList(@PathVariable("roomNo") Integer roomNo, Authentication authentication) {
        String userName = authentication.getName();
        ChattingHistoryResponseDto chattingList = chatService.getChattingList(roomNo, userName);
        return ResponseEntity.ok(Response.success(chattingList));
    }

    //내가 참여중인 채팅 방 조회
    @GetMapping("/my-chatroom")
    public ResponseEntity<Response<List<MyChatRoomResponse>>> chatRoomList(Authentication authentication) {
        String myName = authentication.getName();

        List<MyChatRoomResponse> chatRoomList = chatService.getChatRoomList(myName);

        return ResponseEntity.ok(Response.success(chatRoomList));
    }


    // 메세지 전송
    @MessageMapping("/message")
    public void sendMessage(Message message, @Header("Authorization") final String accessToken) {
        log.info("보낸 메세지 : {}", message.toString());
        chatService.sendMessage(message, accessToken);
    }

    // 채팅방 접속 끊기
    @PostMapping("/chatroom/{chatroomNo}")
    public ResponseEntity<Response<String>> disconnectChat(@PathVariable("chatroomNo") Integer chatroomNo,
                                                           @RequestParam("userName") String userName) {

        chatRoomService.disconnectChatRoom(chatroomNo, userName);
        chatService.leaveMessage(userName, chatroomNo);
        return ResponseEntity.ok(Response.success("접속 끊기"));
    }


    // 메시지 전송 후 callback
    @PostMapping("/chatroom/notification")
    public ResponseEntity<Response<Message>> sendNotification(@RequestBody Message message, Authentication authentication) {
        String userName = authentication.getName();
        Message savedMessage = chatService.sendNotificationAndSaveMessage(message, userName);
        return ResponseEntity.ok(Response.success(savedMessage));
    }
}