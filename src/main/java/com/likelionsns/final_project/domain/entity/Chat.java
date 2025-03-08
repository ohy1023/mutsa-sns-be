package com.likelionsns.final_project.domain.entity;

import com.likelionsns.final_project.domain.response.MyChatRoomResponse;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_no")
    private Integer chatNo;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "join_user")
    private String joinUser;

    @Column(name = "reg_date")
    private LocalDateTime regDate;


    public MyChatRoomResponse toResponse(User user, Long cnt, String message, LocalDateTime lastMessageTime) {
        return MyChatRoomResponse.builder()
                .chatRoomId(this.chatNo)
                .joinUserName(user.getUserName())
                .joinNickName(user.getNickName())
                .joinUserImg(user.getUserImg())
                .notReadMessageCnt(cnt)
                .lastContent(message)
                .lastMessageTime(lastMessageTime)
                .build();
    }


}