package com.likelionsns.final_project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomResponse {

    private Integer chatRoomId;

    private String joinUserName;

    private String joinNickName;

    private String joinUserImg;

    private Long notReadMessageCnt;

    private String lastContent;

    private LocalDateTime lastMessageTime;
}
