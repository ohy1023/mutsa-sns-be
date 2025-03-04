package com.likelionsns.final_project.domain.request;

import com.likelionsns.final_project.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinRequest {

    private String userName;
    private String nickName;
    private String password;

    public User toEntity(String password) {
        return User.builder()
                .userName(this.userName)
                .nickName(this.nickName)
                .password(password)
                .build();
    }

    @Builder
    public UserJoinRequest(String userName, String nickName, String password) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
    }
}
