package com.likelionsns.final_project.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinResponse {

    private Integer userId;

    private String userName;

    @Builder
    public UserJoinResponse(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
