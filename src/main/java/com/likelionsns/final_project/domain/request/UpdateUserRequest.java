package com.likelionsns.final_project.domain.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String nickName;
    private String curPassword;
    private String newPassword;
}
