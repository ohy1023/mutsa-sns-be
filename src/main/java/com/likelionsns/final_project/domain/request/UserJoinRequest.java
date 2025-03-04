package com.likelionsns.final_project.domain.request;

import com.likelionsns.final_project.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinRequest {

    private String username;
    private String nickName;
    private String password;

    public User toEntity(String password) {
        return User.builder()
                .userName(this.username)
                .nickName(this.nickName)
                .password(password)
                .build();
    }

    @Builder
    public UserJoinRequest(String username, String nickName, String password) {
        this.username = username;
        this.nickName = nickName;
        this.password = password;
    }
}
