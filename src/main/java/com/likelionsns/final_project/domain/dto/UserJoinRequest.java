package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinRequest {

    private String userName;

    private String password;

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .password(this.password)
                .build();
    }

}
