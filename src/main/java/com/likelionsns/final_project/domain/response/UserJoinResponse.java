package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinResponse {

    private Integer userId;
    private String userName;
    private String nickName;

    @Builder
    public UserJoinResponse(Integer userId, String userName, String nickName) {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
    }

    public static UserJoinResponse toResponse(User user) {
        return UserJoinResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .build();
    }
}
