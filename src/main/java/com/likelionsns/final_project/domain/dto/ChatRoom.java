package com.likelionsns.final_project.domain.dto;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash(value = "chatRoom")
public class ChatRoom {

    @Id
    private String id;

    @Indexed
    private Integer chatroomNo;

    @Indexed
    private String userName;

    @Builder
    public ChatRoom(Integer chatroomNo, String userName) {
        this.chatroomNo = chatroomNo;
        this.userName = userName;
    }
}