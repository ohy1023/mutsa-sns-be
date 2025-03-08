package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.domain.dto.ChatResponseDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingHistoryResponseDto {

    private String userName;
    private String userImg;
    private List<ChatResponseDto> chatList;
}
