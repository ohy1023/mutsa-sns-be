package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
    private String id;
    private Integer chatRoomNo;
    private Integer senderNo;
    private String senderName;
    private String contentType;
    private String content;
    private long sendDate;
    private long readCount;
    private boolean isMine;

    public ChatResponseDto(Chatting chatting, String userName) {
        this.id = chatting.getId();
        this.chatRoomNo = chatting.getChatRoomNo();
        this.senderName = chatting.getSenderName();
        this.content = chatting.getContent();
        this.sendDate = chatting.getSendDate().atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        this.readCount = chatting.getReadCount();
        this.isMine = chatting.getSenderName().equals(userName);
    }
}
