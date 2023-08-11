package com.likelionsns.final_project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomResponse {

    private Integer chatRoomId;

    private String joinUserName;

    private Long notReadMessageCnt;

    private String lastContent;
}
