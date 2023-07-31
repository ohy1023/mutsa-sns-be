package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import com.sun.istack.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    private String id;

    private Integer chatNo;

    private String content;

    private String senderName;

    private long sendTime;

    private Integer readCount;


    public void setSendTimeAndSender(LocalDateTime sendTime, String senderName, Integer readCount) {
        this.senderName = senderName;
        this.sendTime = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        this.readCount = readCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Chatting toEntity() {
        return Chatting.builder()
                .senderName(senderName)
                .chatRoomNo(chatNo)
                .content(content)
                .sendDate(Instant.ofEpochMilli(sendTime).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .readCount(readCount)
                .build();
    }

}