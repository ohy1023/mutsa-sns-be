package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.ChatRequestDto;
import com.likelionsns.final_project.domain.dto.Message;
import com.likelionsns.final_project.domain.entity.Chat;
import com.likelionsns.final_project.domain.request.LeaveRequest;
import com.likelionsns.final_project.domain.response.ChattingHistoryResponseDto;
import com.likelionsns.final_project.domain.response.MyChatRoomResponse;
import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.service.ChatRoomService;
import com.likelionsns.final_project.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    @PostMapping("/chatroom")
    public ResponseEntity<Response<Chat>> createChatRoom(@RequestBody ChatRequestDto requestDto, Authentication authentication) {
        String myName = authentication.getName();

        log.info("userName: {}", myName);

        // 채팅방을 만들어준다.
        Chat chat = chatRoomService.makeChatRoom(myName, requestDto);

        return ResponseEntity.ok(Response.success(chat));
    }

    // 채팅내역 조회 (페이징 적용)
    @GetMapping("/chatroom/{roomNo}")
    public ResponseEntity<Response<Slice<ChattingHistoryResponseDto>>> chattingList(
            @PathVariable("roomNo") Integer roomNo,
            Authentication authentication,
            @PageableDefault(size = 10) @SortDefault(sort = "sendDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String userName = authentication.getName();
        Slice<ChattingHistoryResponseDto> chattingList = chatService.getChattingList(roomNo, userName, pageable);
        return ResponseEntity.ok(Response.success(chattingList));
    }

    // 내가 참여 중인 채팅방 조회 (페이징 적용)
    @GetMapping("/my-chatroom")
    public ResponseEntity<Response<Slice<MyChatRoomResponse>>> chatRoomList(
            Authentication authentication, Pageable pageable) {
        String myName = authentication.getName();
        Slice<MyChatRoomResponse> chatRoomList = chatRoomService.getChatRoomList(myName, pageable);
        return ResponseEntity.ok(Response.success(chatRoomList));
    }

    // 알림 전송 및 메세지 저장 (메시지 전송 후 callback )
    @PostMapping("/chatroom/message-alarm-record")
    public ResponseEntity<Response<Message>> sendNotification(@RequestBody Message message, Authentication authentication) {
        String userName = authentication.getName();
        Message savedMessage = chatService.sendAlarmAndSaveMessage(message, userName);
        return ResponseEntity.ok(Response.success(savedMessage));
    }

    // 메세지 전송
    @MessageMapping("/message")
    public void sendMessage(Message message, @Header("Authorization") final String accessToken) {
        log.info("보낸 메세지 : {}", message.toString());
        chatService.sendMessage(message, accessToken);
    }


    @MessageMapping("/chatroom/leave")
    public void leaveChatRoom(@Payload LeaveRequest leaveRequest, @Header("Authorization") final String accessToken) {

        int leaveChatRoomNo = leaveRequest.getChatNo();
        String leaveUserName = leaveRequest.getUserName();
        log.info("나가려는 채팅 방 : {}", leaveChatRoomNo);
        log.info("나가는 사람 : {}", leaveUserName);

        // 채팅방 나가기 로직 처리
        chatRoomService.disconnectChatRoom(leaveChatRoomNo, leaveUserName);
        chatService.leaveMessage(leaveUserName, leaveChatRoomNo);
    }

}