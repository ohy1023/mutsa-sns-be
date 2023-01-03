package com.likelionsns.final_project.domain.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequest {

    private String userName;
    private String password;

    @Builder
    public UserLoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
